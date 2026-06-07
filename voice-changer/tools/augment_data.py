"""
数据增强工具
===============
通过音高偏移、时间拉伸等方法增加训练数据
"""

import numpy as np
import librosa
import soundfile as sf
from pathlib import Path
from typing import Tuple, Optional
import random


def pitch_shift(
    audio: np.ndarray,
    sr: int,
    semitones: float
) -> np.ndarray:
    """
    音高偏移
    
    Args:
        audio: 音频数据
        sr: 采样率
        semitones: 偏移半音数 (-12 ~ +12)
        
    Returns:
        处理后的音频
    """
    return librosa.effects.pitch_shift(
        audio,
        sr=sr,
        n_steps=semitones
    )


def time_stretch(
    audio: np.ndarray,
    rate: float
) -> np.ndarray:
    """
    时间拉伸（不改变音调）
    
    Args:
        audio: 音频数据
        rate: 拉伸率 (<1 变慢，>1 变快)
        
    Returns:
        处理后的音频
    """
    return librosa.effects.time_stretch(audio, rate=rate)


def add_noise(
    audio: np.ndarray,
    noise_level: float = 0.1,
    noise_type: str = "white"
) -> np.ndarray:
    """
    添加噪声
    
    Args:
        audio: 音频数据
        noise_level: 噪声强度
        noise_type: 噪声类型 (white, pink, brown)
        
    Returns:
        含噪声的音频
    """
    if noise_type == "white":
        noise = np.random.randn(len(audio))
    elif noise_type == "pink":
        # 粉红噪声近似
        noise = librosa.colornoise.power_noise(
            10,
            len(audio),
            sampling_rate=22050,
            color="pink"
        )
    elif noise_type == "brown":
        noise = librosa.colornoise.power_noise(
            10,
            len(audio),
            sampling_rate=22050,
            color="brown"
        )
    else:
        noise = np.random.randn(len(audio))
    
    # 标准化噪声
    noise = noise / np.max(np.abs(noise))
    
    # 混合
    return audio + noise_level * noise


def change_speed(
    audio: np.ndarray,
    speed_factor: float
) -> np.ndarray:
    """
    改变速度
    
    Args:
        audio: 音频数据
        speed_factor: 速度因子
        
    Returns:
        处理后的音频
    """
    # 使用 resample 方法
    length = int(len(audio) / speed_factor)
    return librosa.resample(
        audio,
        orig_sr=22050,
        target_sr=22050 * speed_factor
    )[:length]


def apply_reverb(
    audio: np.ndarray,
    room_size: float = 0.5,
    damp: float = 0.5,
    width: float = 1.0
) -> np.ndarray:
    """
    简单混响效果
    
    Args:
        audio: 音频数据
        room_size: 房间大小
        damp: 阻尼
        width: 宽度
        
    Returns:
        含混响的音频
    """
    # 简化的混响实现（实际项目应使用专业库）
    delay_times = [0.01, 0.03, 0.05, 0.07]  # 延迟时间
    decay = [0.7, 0.6, 0.5, 0.4]  # 衰减
    
    output = audio.copy()
    sr = 22050
    
    for delay, d in zip(delay_times, decay):
        delay_samples = int(delay * sr)
        delayed = np.zeros_like(audio)
        delayed[delay_samples:] = audio[:-delay_samples] * d
        output += delayed
    
    return output


def augment_audio(
    audio: np.ndarray,
    sr: int = 22050,
    pitch_range: Tuple[float, float] = (0, 0),
    time_range: Tuple[float, float] = (1.0, 1.0),
    noise_range: Tuple[float, float] = (0, 0)
) -> np.ndarray:
    """
    组合增强
    
    Args:
        audio: 音频数据
        sr: 采样率
        pitch_range: 音高偏移范围
        time_range: 时间拉伸范围
        noise_range: 噪声强度范围
        
    Returns:
        增强后的音频
    """
    # 应用增强
    if pitch_range[0] != pitch_range[1]:
        semitones = random.uniform(*pitch_range)
        audio = pitch_shift(audio, sr, semitones)
    
    if time_range != (1.0, 1.0):
        rate = random.uniform(*time_range)
        audio = time_stretch(audio, rate)
    
    if noise_range[1] > 0:
        noise_level = random.uniform(*noise_range)
        audio = add_noise(audio, noise_level)
    
    return audio


def generate_augmented_dataset(
    input_dir: str,
    output_dir: str,
    num_augmentations: int = 3,
    pitch_range: Tuple[float, float] = (-5, 5),
    time_range: Tuple[float, float] = (0.9, 1.1),
    noise_range: Tuple[float, float] = (0, 0.05)
):
    """
    生成增强数据集
    
    Args:
        input_dir: 原始音频目录
        output_dir: 输出目录
        num_augmentations: 每个样本的增强数量
        pitch_range: 音高偏移范围
        time_range: 时间拉伸范围
        noise_range: 噪声范围
    """
    input_path = Path(input_dir)
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    # 获取所有音频文件
    audio_files = list(input_path.glob("*.wav")) + \
                  list(input_path.glob("*.flac"))
    
    if not audio_files:
        print(f"错误：在 {input_dir} 中未找到音频文件")
        return
    
    print(f"找到 {len(audio_files)} 个文件")
    print(f"每个文件增强 {num_augmentations} 次")
    print(f"总共生成 {len(audio_files) * (num_augmentations + 1)} 个文件")
    print("=" * 60)
    
    count = 0
    
    for audio_file in audio_files:
        # 加载音频
        audio, sr = sf.read(str(audio_file))
        audio = audio.astype(np.float32)
        
        # 保存原始文件
        base_name = Path(audio_file).stem
        original_output = output_path / f"{base_name}_original.wav"
        sf.write(str(original_output), audio, sr)
        count += 1
        
        # 生成增强版本
        for i in range(num_augmentations):
            augmented = augment_audio(
                audio, sr,
                pitch_range=pitch_range,
                time_range=time_range,
                noise_range=noise_range
            )
            
            augmented_name = output_path / f"{base_name}_aug{i+1}.wav"
            sf.write(str(augmented_name), augmented, sr)
            count += 1
        
        if (count) % 10 == 0:
            print(f"已处理 {count} 个文件")
    
    print("=" * 60)
    print(f"增强完成：{count} 个文件")


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="音频数据增强工具"
    )
    
    parser.add_argument(
        "-i", "--input",
        type=str,
        required=True,
        help="输入音频目录"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        required=True,
        help="输出目录"
    )
    
    parser.add_argument(
        "-n", "--num-aug",
        type=int,
        default=3,
        help="每个文件的增强数量"
    )
    
    parser.add_argument(
        "-p", "--pitch-range",
        type=float,
        nargs=2,
        default=[-5, 5],
        help="音高偏移范围"
    )
    
    parser.add_argument(
        "-t", "--time-range",
        type=float,
        nargs=2,
        default=[0.9, 1.1],
        help="时间拉伸范围"
    )
    
    parser.add_argument(
        "-N", "--noise-range",
        type=float,
        nargs=2,
        default=[0, 0.05],
        help="噪声强度范围"
    )
    
    args = parser.parse_args()
    
    generate_augmented_dataset(
        args.input,
        args.output,
        num_augmentations=args.num_aug,
        pitch_range=tuple(args.pitch_range),
        time_range=tuple(args.time_range),
        noise_range=tuple(args.noise_range)
    )


if __name__ == "__main__":
    main()
