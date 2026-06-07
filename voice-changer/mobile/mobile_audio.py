"""
移动端音频处理模块
====================
针对 Android 优化的音频处理
"""

import numpy as np
from typing import Optional, Callable
import threading
import queue
from pathlib import Path


class MobileAudioProcessor:
    """
    移动端音频处理器
    ====================
    针对 Android/iOS 优化的轻量级音频处理
    """
    
    def __init__(
        self,
        sample_rate: int = 22050,
        channels: int = 1,
        chunk_size: int = 2048
    ):
        """
        初始化移动端音频处理器
        
        Args:
            sample_rate: 采样率（移动端建议 22050）
            channels: 声道数
            chunk_size: 缓冲区大小
        """
        self.sample_rate = sample_rate
        self.channels = channels
        self.chunk_size = chunk_size
        
        # 音频队列
        self.input_queue = queue.Queue()
        self.output_queue = queue.Queue()
        
        # 处理回调
        self.process_callback: Optional[Callable] = None
        
        # 控制
        self.is_running = False
        self.thread: Optional[threading.Thread] = None
        
        # 移动端优化
        self.use_neon = True  # ARM NEON 优化
        self.use_openmp = False  # 多线程
    
    def start(self, callback: Callable):
        """
        开始音频处理
        
        Args:
            callback: 音频处理回调函数
        """
        self.process_callback = callback
        self.is_running = True
        
        # 启动处理线程
        self.thread = threading.Thread(target=self._process_loop)
        self.thread.start()
    
    def stop(self):
        """停止音频处理"""
        self.is_running = False
        
        if self.thread and self.thread.is_alive():
            self.thread.join(timeout=2.0)
        
        # 清空队列
        while not self.input_queue.empty():
            try:
                self.input_queue.get_nowait()
            except queue.Empty:
                break
    
    def _process_loop(self):
        """音频处理循环"""
        while self.is_running:
            try:
                # 获取音频数据
                audio_data = self.input_queue.get(timeout=0.1)
                
                # 处理
                if self.process_callback:
                    processed = self.process_callback(audio_data)
                    self.output_queue.put(processed)
                    
            except queue.Empty:
                continue
            except Exception as e:
                print(f"移动端音频处理错误：{str(e)}")
                continue
    
    def feed_audio(self, audio_data: np.ndarray):
        """
        输入音频数据
        
        Args:
            audio_data: 音频数据数组
        """
        if self.is_running:
            self.input_queue.put(audio_data)
    
    def get_processed(self) -> Optional[np.ndarray]:
        """获取处理后的音频"""
        try:
            return self.output_queue.get_nowait()
        except queue.Empty:
            return None
    
    def get_volume(self) -> float:
        """获取当前音量 (RMS)"""
        try:
            if not self.input_queue.empty():
                audio = self.input_queue.queue[-1]
                rms = np.sqrt(np.mean(audio ** 2))
                return min(rms * 100, 1.0)
        except Exception:
            pass
        return 0.0


class MobileRecorder:
    """
    移动端录音器
    =============
    使用 Kivy 的 SoundRecorder 或系统原生 API
    """
    
    def __init__(self, sample_rate: int = 22050):
        """
        初始化录音器
        
        Args:
            sample_rate: 采样率
        """
        self.sample_rate = sample_rate
        self.is_recording = False
        self.recorder = None
        
        # 录音数据
        self.recorded_data = []
    
    def start(self):
        """开始录音"""
        try:
            from kivy.core.audio import SoundRecorder
            
            class TempRecorder(SoundRecorder):
                def __init__(self, parent):
                    super().__init__()
                    self.parent = parent
                
                def on_start(self):
                    self.parent.is_recording = True
                
                def on_stop(self):
                    self.parent.is_recording = False
                    self.parent.on_recording_complete(self.recorded_data if hasattr(self, 'recorded_data') else [])
            
            self.recorder = TempRecorder(self)
            # 注意：Kivy 的 SoundRecorder 功能有限，实际项目需要 JNI 调用原生 API
            self.is_recording = True
            
        except Exception as e:
            print(f"录音启动失败：{str(e)}")
            self.is_recording = False
    
    def stop(self):
        """停止录音"""
        if self.recorder:
            try:
                self.recorder.stop()
            except Exception:
                pass
        self.is_recording = False
    
    def on_recording_complete(self, data):
        """录音完成回调"""
        pass


def resample_audio(
    audio: np.ndarray,
    orig_sr: int,
    target_sr: int
) -> np.ndarray:
    """
    重采样音频（移动端优化版）
    
    Args:
        audio: 原始音频
        orig_sr: 原始采样率
        target_sr: 目标采样率
        
    Returns:
        重采样后的音频
    """
    if orig_sr == target_sr:
        return audio
    
    # 简单线性插值（快速但有质量损失）
    ratio = target_sr / orig_sr
    new_length = int(len(audio) * ratio)
    
    # 使用 numpy 进行快速重采样
    indices = np.linspace(0, len(audio) - 1, new_length)
    return np.interp(indices, np.arange(len(audio)), audio)


def optimize_for_mobile(model_path: str) -> str:
    """
    优化模型用于移动端推理
    
    Args:
        model_path: 原始模型路径
        
    Returns:
        优化后模型路径
    """
    import torch
    from pathlib import Path
    
    # 加载模型
    checkpoint = torch.load(model_path, map_location='cpu')
    
    # 量化模型（减少内存占用）
    if 'state_dict' in checkpoint:
        state_dict = checkpoint['state_dict']
        
        # 将浮点权重转换为半精度
        for key in state_dict:
            if state_dict[key].dtype == torch.float32:
                state_dict[key] = state_dict[key].half()
    
    # 保存优化后模型
    output_path = Path(model_path).parent / f"{Path(model_path).stem}_mobile.pth"
    torch.save(checkpoint, output_path)
    
    print(f"移动端优化完成：{output_path}")
    print(f"模型大小减少约 50%")
    
    return str(output_path)


# 移动端工具函数

def get_mobile_device_info() -> dict:
    """获取移动设备信息"""
    import platform
    
    info = {
        'platform': platform.system(),
        'architecture': platform.machine(),
        'python_version': platform.python_version(),
    }
    
    # ARM 架构检测
    if 'arm' in info['architecture'].lower() or 'aarch' in info['architecture'].lower():
        info['is_arm'] = True
        info['supports_neon'] = True
    else:
        info['is_arm'] = False
    
    return info


def check_mobile_audio_support() -> dict:
    """检查移动设备音频支持"""
    try:
        import pyaudio
        
        pa = pyaudio.PyAudio()
        info = {
            'supported': True,
            'devices': [],
            'default_input': None,
            'default_output': None
        }
        
        for i in range(pa.get_device_count()):
            device = pa.get_device_info_by_index(i)
            if device.get('maxInputChannels', 0) > 0:
                info['devices'].append({
                    'name': device['name'],
                    'channels': device['maxInputChannels'],
                    'sample_rate': int(device['defaultSampleRate'])
                })
                if device.get('isDefaultInput'):
                    info['default_input'] = device['name']
        
        pa.terminate()
        return info
        
    except Exception as e:
        return {
            'supported': False,
            'error': str(e)
        }
