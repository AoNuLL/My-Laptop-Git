"""
音频处理核心模块
=================
负责音频录制、播放、特征提取和实时处理
"""

import numpy as np
import pyaudio
import sounddevice as sd
from typing import Optional, Callable, Generator
import threading
import queue
import time
from dataclasses import dataclass
from enum import Enum


class AudioState(Enum):
    """音频设备状态"""
    IDLE = "idle"
    RECORDING = "recording"
    PROCESSING = "processing"
    PLAYING = "playing"
    STOPPED = "stopped"


@dataclass
class AudioConfig:
    """音频配置参数"""
    sample_rate: int = 48000  # 采样率
    channels: int = 1  # 声道数
    chunk_size: int = 512  # 缓冲区大小（影响延迟）
    device_index: Optional[int] = None  # 输入设备索引
    output_device_index: Optional[int] = None  # 输出设备索引
    dtype: np.dtype = np.float32  # 数据类型


class AudioProcessor:
    """
    音频处理器
    ============
    处理音频的录制、变声和播放
    
    Attributes:
        config: 音频配置
        state: 当前音频状态
        audio_callback: 处理后的音频回调函数
    """
    
    def __init__(self, config: Optional[AudioConfig] = None):
        """
        初始化音频处理器
        
        Args:
            config: 音频配置，使用默认配置如果为 None
        """
        self.config = config or AudioConfig()
        self.state = AudioState.IDLE
        
        # 音频流
        self.input_stream: Optional[pyaudio.Stream] = None
        self.output_stream: Optional[pyaudio.Stream] = None
        
        # 音频队列
        self.input_queue = queue.Queue()
        self.output_queue = queue.Queue()
        
        # 回调函数
        self.audio_callback: Optional[Callable] = None
        self.error_callback: Optional[Callable] = None
        
        # PyAudio 实例
        self.pa = pyaudio.PyAudio()
        
        # 处理线程
        self.processing_thread: Optional[threading.Thread] = None
        self.stop_event = threading.Event()
    
    def get_input_devices(self) -> list:
        """
        获取所有可用的输入设备
        
        Returns:
            设备信息列表，每个设备包含 index 和 name
        """
        devices = []
        try:
            info = self.pa.get_host_api_info_by_index(0)
            for i in range(info.get('deviceCount')):
                device_info = self.pa.get_device_info_by_host_and_device_index(0, i)
                if device_info.get('maxInputChannels') > 0:
                    devices.append({
                        'index': i,
                        'name': device_info.get('name'),
                        'channels': device_info.get('maxInputChannels'),
                        'sample_rate': int(device_info.get('defaultSampleRate'))
                    })
        except Exception as e:
            if self.error_callback:
                self.error_callback(f"获取输入设备失败：{str(e)}")
        return devices
    
    def get_output_devices(self) -> list:
        """
        获取所有可用的输出设备
        
        Returns:
            设备信息列表
        """
        devices = []
        try:
            info = self.pa.get_host_api_info_by_index(0)
            for i in range(info.get('deviceCount')):
                device_info = self.pa.get_device_info_by_host_and_device_index(0, i)
                if device_info.get('maxOutputChannels') > 0:
                    devices.append({
                        'index': i,
                        'name': device_info.get('name'),
                        'channels': device_info.get('maxOutputChannels'),
                        'sample_rate': int(device_info.get('defaultSampleRate'))
                    })
        except Exception as e:
            if self.error_callback:
                self.error_callback(f"获取输出设备失败：{str(e)}")
        return devices
    
    def start_recording(self, audio_callback: Callable):
        """
        开始录制音频
        
        Args:
            audio_callback: 处理后的音频回调函数 callable(audio_data: np.ndarray)
        """
        if self.state == AudioState.RECORDING:
            return
        
        self.audio_callback = audio_callback
        self.stop_event.clear()
        
        # 创建输入流
        try:
            self.input_stream = self.pa.stream(
                format=pyaudio.paFloat32,
                channels=self.config.channels,
                rate=self.config.sample_rate,
                input=True,
                frames_per_buffer=self.config.chunk_size,
                input_device_index=self.config.device_index,
                stream_callback=self._input_callback
            )
            
            self.input_stream.start_stream()
            self.state = AudioState.RECORDING
            
            # 启动处理线程
            self.processing_thread = threading.Thread(target=self._process_audio)
            self.processing_thread.start()
            
        except Exception as e:
            if self.error_callback:
                self.error_callback(f"开始录制失败：{str(e)}")
            self.state = AudioState.STOPPED
    
    def _input_callback(self, in_data, frame_count, time_info, status):
        """
        PyAudio 输入回调
        
        Args:
            in_data: 输入音频数据
            frame_count: 帧数
            time_info: 时间信息
            status: 状态标志
            
        Returns:
            (data, continue_flag) 元组
        """
        if status:
            if self.error_callback:
                self.error_callback(f"输入流状态：{status}")
        
        if in_data and self.state == AudioState.RECORDING:
            audio_data = np.frombuffer(in_data, dtype=self.config.dtype)
            self.input_queue.put(audio_data)
        
        return (None, pyaudio.paContinue)
    
    def _process_audio(self):
        """
        音频处理线程函数
        
        从输入队列获取原始音频，调用回调函数处理，
        将处理后的音频放入输出队列
        """
        while not self.stop_event.is_set():
            try:
                # 从队列获取音频数据
                audio_data = self.input_queue.get(timeout=0.1)
                
                # 调用处理函数（由子类实现具体的变声逻辑）
                if self.audio_callback:
                    processed_audio = self.audio_callback(audio_data)
                    self.output_queue.put(processed_audio)
                
            except queue.Empty:
                continue
            except Exception as e:
                if self.error_callback:
                    self.error_callback(f"音频处理错误：{str(e)}")
                continue
    
    def start_playback(self):
        """
        开始播放处理后的音频
        
        从输出队列获取处理后的音频数据并播放
        """
        if self.state != AudioState.RECORDING:
            return
        
        try:
            self.output_stream = self.pa.stream(
                format=pyaudio.paFloat32,
                channels=self.config.channels,
                rate=self.config.sample_rate,
                output=True,
                frames_per_buffer=self.config.chunk_size,
                output_device_index=self.config.output_device_index,
                stream_callback=self._output_callback
            )
            
            self.output_stream.start_stream()
            self.state = AudioState.PLAYING
            
        except Exception as e:
            if self.error_callback:
                self.error_callback(f"开始播放失败：{str(e)}")
            self.state = AudioState.STOPPED
    
    def _output_callback(self, in_data, frame_count, time_info, status):
        """
        PyAudio 输出回调
        
        Args:
            in_data: 输入数据（播放时为 None）
            frame_count: 帧数
            time_info: 时间信息
            status: 状态标志
            
        Returns:
            (data, continue_flag) 元组
        """
        if status:
            if self.error_callback:
                self.error_callback(f"输出流状态：{status}")
        
        try:
            # 从队列获取处理后的音频
            audio_data = self.output_queue.get(timeout=0.01)
            return (audio_data.tobytes(), pyaudio.paContinue)
        except queue.Empty:
            # 队列空时返回静音
            return (np.zeros(frame_count * self.config.channels, dtype=self.config.dtype).tobytes(), 
                    pyaudio.paContinue)
    
    def stop(self):
        """停止录制和播放"""
        self.stop_event.set()
        
        # 停止并关闭输入流
        if self.input_stream:
            if self.input_stream.is_active():
                self.input_stream.stop_stream()
            self.input_stream.close()
            self.input_stream = None
        
        # 停止并关闭输出流
        if self.output_stream:
            if self.output_stream.is_active():
                self.output_stream.stop_stream()
            self.output_stream.close()
            self.output_stream = None
        
        # 等待处理线程结束
        if self.processing_thread and self.processing_thread.is_alive():
            self.processing_thread.join(timeout=2.0)
        
        # 清空队列
        while not self.input_queue.empty():
            try:
                self.input_queue.get_nowait()
            except queue.Empty:
                break
        
        while not self.output_queue.empty():
            try:
                self.output_queue.get_nowait()
            except queue.Empty:
                break
        
        self.state = AudioState.STOPPED
    
    def record_to_file(self, filename: str, duration: float = 5.0):
        """
        录制音频到文件
        
        Args:
            filename: 输出文件名
            duration: 录制时长（秒）
        """
        self.state = AudioState.RECORDING
        recorded_audio = []
        
        def callback(audio_data):
            recorded_audio.append(audio_data.copy())
            return audio_data  # 直接返回原始音频用于监听
        
        # 开始录制
        self.start_recording(callback)
        
        # 录制指定时长
        time.sleep(duration)
        
        # 停止录制
        self.stop()
        
        # 保存音频
        if recorded_audio:
            import soundfile as sf
            audio = np.concatenate(recorded_audio)
            sf.write(filename, audio, self.config.sample_rate)
    
    def get_volume(self) -> float:
        """
        获取当前输入音量（RMS 值）
        
        Returns:
            RMS 音量值（0.0 - 1.0）
        """
        try:
            if not self.input_queue.empty():
                audio_data = self.input_queue.queue[-1]
                rms = np.sqrt(np.mean(audio_data ** 2))
                return min(rms * 100, 1.0)  # 放大并限制在 0-1 范围
        except Exception:
            pass
        return 0.0
    
    def __del__(self):
        """析构函数，清理资源"""
        self.stop()
        self.pa.terminate()
