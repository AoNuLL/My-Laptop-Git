"""
音频可视化工具
================
实时显示音频波形和频谱
"""

import numpy as np
from typing import Optional, Tuple


class AudioVisualizer:
    """
    音频可视化器
    ============
    用于实时显示音频波形和频谱信息
    """
    
    def __init__(
        self,
        sample_rate: int = 48000,
        fft_size: int = 2048,
        hop_size: int = 512
    ):
        """
        初始化可视化器
        
        Args:
            sample_rate: 采样率
            fft_size: FFT 大小
            hop_size: 跳帧大小
        """
        self.sample_rate = sample_rate
        self.fft_size = fft_size
        self.hop_size = hop_size
        
        # 窗口函数
        self.window = np.hanning(fft_size)
    
    def compute_waveform(
        self,
        audio_data: np.ndarray,
        num_points: int = 256
    ) -> np.ndarray:
        """
        计算波形数据
        
        Args:
            audio_data: 音频数据
            num_points: 输出点数
            
        Returns:
            波形数据数组
        """
        if len(audio_data) < num_points:
            # 填充
            audio_data = np.pad(
                audio_data,
                (0, num_points - len(audio_data))
            )
        
        # 降采样
        step = len(audio_data) // num_points
        waveform = audio_data[::step][:num_points]
        
        return waveform
    
    def compute_spectrum(
        self,
        audio_data: np.ndarray
    ) -> Tuple[np.ndarray, np.ndarray]:
        """
        计算频谱
        
        Args:
            audio_data: 音频数据
            
        Returns:
            (频率轴，幅度谱)
        """
        # 应用 FFT
        spectrum = np.fft.rfft(audio_data * self.window)
        magnitude = np.abs(spectrum)
        
        # 频率轴
        frequencies = np.fft.rfftfreq(
            len(audio_data),
            1.0 / self.sample_rate
        )
        
        # 转换为 dB
        magnitude_db = 20 * np.log10(magnitude + 1e-10)
        
        # 归一化
        magnitude_db = magnitude_db - magnitude_db.max()
        
        return frequencies, magnitude_db
    
    def compute_spectrogram(
        self,
        audio_data: np.ndarray,
        num_frames: Optional[int] = None
    ) -> np.ndarray:
        """
        计算语谱图
        
        Args:
            audio_data: 音频数据
            num_frames: 帧数
            
        Returns:
            语谱图 (频率 x 时间)
        """
        if num_frames is None:
            num_frames = len(audio_data) // self.hop_size
        
        spectrogram = []
        
        for i in range(num_frames):
            start = i * self.hop_size
            end = start + self.fft_size
            
            if end > len(audio_data):
                # 填充
                frame = np.zeros(self.fft_size)
                available = len(audio_data) - start
                frame[:available] = audio_data[start:]
            else:
                frame = audio_data[start:end]
            
            # 计算频谱
            spectrum = np.fft.rfft(frame * self.window)
            magnitude = np.abs(spectrum)
            magnitude_db = 20 * np.log10(magnitude + 1e-10)
            
            spectrogram.append(magnitude_db)
        
        return np.array(spectrogram).T  # 转置为 频率 x 时间
    
    def compute_rms(
        self,
        audio_data: np.ndarray,
        window_size: int = 1024
    ) -> np.ndarray:
        """
        计算 RMS (均方根) 音量
        
        Args:
            audio_data: 音频数据
            window_size: 窗口大小
            
        Returns:
            RMS 序列
        """
        num_frames = len(audio_data) // window_size
        rms = []
        
        for i in range(num_frames):
            start = i * window_size
            frame = audio_data[start:start + window_size]
            rms_value = np.sqrt(np.mean(frame ** 2))
            rms.append(rms_value)
        
        return np.array(rms)
    
    def compute_zcr(
        self,
        audio_data: np.ndarray,
        window_size: int = 1024
    ) -> np.ndarray:
        """
        计算过零率 (Zero Crossing Rate)
        
        Args:
            audio_data: 音频数据
            window_size: 窗口大小
            
        Returns:
            过零率序列
        """
        num_frames = len(audio_data) // window_size
        zcr = []
        
        for i in range(num_frames):
            start = i * window_size
            frame = audio_data[start:start + window_size]
            
            # 计算过零数
            zero_crossings = np.sum(np.abs(np.diff(np.sign(frame))))
            zcr.append(zero_crossings / len(frame))
        
        return np.array(zcr)
    
    def compute_mfcc(
        self,
        audio_data: np.ndarray,
        num_mfcc: int = 13
    ) -> np.ndarray:
        """
        计算 MFCC 特征
        
        Args:
            audio_data: 音频数据
            num_mfcc: MFCC 系数数量
            
        Returns:
            MFCC 特征矩阵
        """
        try:
            import librosa
            mfcc = librosa.feature.mfcc(
                y=audio_data,
                sr=self.sample_rate,
                n_mfcc=num_mfcc,
                n_fft=self.fft_size,
                hop_length=self.hop_size
            )
            return mfcc
        except ImportError:
            # 简化版 MFCC 计算
            spectrogram = self.compute_spectrogram(audio_data)
            # 使用 DCT 近似
            from scipy.fftpack import dct
            mfcc = dct(spectrogram, type=2, axis=0, norm='ortho')[:num_mfcc]
            return mfcc
    
    def compute_pitch(
        self,
        audio_data: np.ndarray,
        fmin: float = 65.0,
        fmax: float = 1047.0
    ) -> np.ndarray:
        """
        计算音高轨迹
        
        Args:
            audio_data: 音频数据
            fmin: 最低频率
            fmax: 最高频率
            
        Returns:
            音高序列 (Hz)
        """
        try:
            import librosa
            f0, _, _ = librosa.pyin(
                audio_data,
                fmin=fmin,
                fmax=fmax,
                sr=self.sample_rate,
                frame_length=self.fft_size,
                hop_length=self.hop_size
            )
            return np.nan_to_num(f0, nan=0.0)
        except Exception:
            # 简化的自相关方法
            return self._autocorrelation_pitch(audio_data)
    
    def _autocorrelation_pitch(
        self,
        audio_data: np.ndarray
    ) -> np.ndarray:
        """使用自相关方法估计音高"""
        num_frames = len(audio_data) // self.hop_size
        pitch = []
        
        for i in range(num_frames):
            start = i * self.hop_size
            frame = audio_data[start:start + self.fft_size]
            
            # 自相关
            autocorr = np.correlate(frame, frame, mode='full')
            autocorr = autocorr[len(autocorr)//2:]
            
            # 找峰值
            if len(autocorr) > 0:
                peak = np.argmax(autocorr[1:]) + 1
                period = peak
                
                if period > 0:
                    frequency = self.sample_rate / period
                else:
                    frequency = 0
            else:
                frequency = 0
            
            pitch.append(frequency)
        
        return np.array(pitch)
    
    def get_features(
        self,
        audio_data: np.ndarray,
        feature_types: list = None
    ) -> dict:
        """
        获取多种特征
        
        Args:
            audio_data: 音频数据
            feature_types: 要计算的特征列表
            
        Returns:
            特征字典
        """
        if feature_types is None:
            feature_types = ['waveform', 'spectrum', 'rms']
        
        features = {}
        
        if 'waveform' in feature_types:
            features['waveform'] = self.compute_waveform(audio_data)
        
        if 'spectrum' in feature_types:
            freq, spec = self.compute_spectrum(audio_data)
            features['frequencies'] = freq
            features['spectrum'] = spec
        
        if 'rms' in feature_types:
            features['rms'] = self.compute_rms(audio_data)
        
        if 'zcr' in feature_types:
            features['zcr'] = self.compute_zcr(audio_data)
        
        if 'spectrogram' in feature_types:
            features['spectrogram'] = self.compute_spectrogram(audio_data)
        
        if 'mfcc' in feature_types:
            features['mfcc'] = self.compute_mfcc(audio_data)
        
        if 'pitch' in feature_types:
            features['pitch'] = self.compute_pitch(audio_data)
        
        return features


def visualize_audio_in_terminal(
    audio_data: np.ndarray,
    sample_rate: int = 48000,
    width: int = 80,
    height: int = 20
):
    """
    在终端中可视化音频
    
    Args:
        audio_data: 音频数据
        sample_rate: 采样率
        width: 终端宽度
        height: 终端高度
    """
    visualizer = AudioVisualizer(sample_rate)
    
    print("\n音频波形可视化:")
    print("=" * width)
    
    # 波形
    waveform = visualizer.compute_waveform(audio_data, num_points=width)
    
    for i in range(height):
        threshold = 1.0 - (i / height) * 2
        line = ""
        for value in waveform:
            if abs(value) > threshold:
                line += "█"
            elif abs(value) > threshold / 2:
                line += "▄"
            elif abs(value) > threshold / 4:
                line += "."
            else:
                line += " "
        print(line)
    
    print("=" * width)


if __name__ == "__main__":
    # 测试可视化器
    import matplotlib.pyplot as plt
    
    # 生成测试音频
    t = np.linspace(0, 1, 48000)
    audio = 0.5 * np.sin(2 * np.pi * 440 * t)  # A4 音符
    
    viz = AudioVisualizer()
    features = viz.get_features(audio, [
        'waveform', 'spectrum', 'rms', 'pitch'
    ])
    
    # 绘图
    fig, axes = plt.subplots(4, 1, figsize=(10, 8))
    
    axes[0].plot(features['waveform'])
    axes[0].set_title("Waveform")
    
    axes[1].plot(features['frequencies'], features['spectrum'])
    axes[1].set_title("Spectrum")
    axes[1].set_xlabel("Frequency (Hz)")
    
    axes[2].plot(features['rms'])
    axes[2].set_title("RMS")
    
    axes[3].plot(features['pitch'])
    axes[3].set_title("Pitch")
    axes[3].set_xlabel("Frame")
    
    plt.tight_layout()
    plt.savefig("audio_visualization.png")
    print("✓ 可视化已保存到 audio_visualization.png")
