"""
音频切片工具
================
将长音频切分为多个短片段，用于训练数据准备
"""

import argparse
import librosa
import soundfile as sf
from pathlib import Path
from typing import List
import os


def slice_audio(
    input_file: str,
    output_dir: str,
    duration: float = 10.0,
    overlap: float = 1.0,
    min_length: float = 5.0,
    padding: bool = True
):
    """
    将长音频切分为多个片段
    
    Args:
        input_file: 输入音频文件
        output_dir: 输出目录
        duration: 每个片段的时长（秒）
        overlap: 重叠时长（秒）
        min_length: 最小保留长度（秒）
        padding: 是否对不足长度的片段进行填充
    """
    # 加载音频
    print(f"加载音频：{input_file}")
    audio, sample_rate = librosa.load(input_file, sr=None, mono=True)
    
    # 计算参数
    total_length = len(audio)
    total_duration = total_length / sample_rate
    hop_length = int((duration - overlap) * sample_rate)
    segment_length = int(duration * sample_rate)
    
    print(f"音频总长度：{total_duration:.2f} 秒")
    print(f"片段长度：{duration} 秒")
    print(f"重叠长度：{overlap} 秒")
    
    # 创建输出目录
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    # 切片
    segments = []
    start_idx = 0
    count = 0
    
    while start_idx < total_length:
        end_idx = min(start_idx + segment_length, total_length)
        segment = audio[start_idx:end_idx]
        
        # 跳过过短的片段
        if len(segment) / sample_rate < min_length:
            start_idx += hop_length
            continue
        
        # 填充不足长度的片段
        if padding and len(segment) < segment_length:
            pad_length = segment_length - len(segment)
            segment = librosa.util.pad_center(segment, size=segment_length)
        
        segments.append(segment)
        
        # 输出文件名
        output_file = output_path / f"{Path(input_file).stem}_{count:04d}.wav"
        sf.write(str(output_file), segment, sample_rate)
        
        count += 1
        start_idx += hop_length
    
    print(f"切片完成：{count} 个片段")
    print(f"输出目录：{output_dir}")
    
    return segments


def slice_directory(
    input_dir: str,
    output_dir: str,
    duration: float = 10.0,
    overlap: float = 1.0
):
    """
    批量处理目录中的所有音频文件
    
    Args:
        input_dir: 输入目录
        output_dir: 输出目录
        duration: 片段时长
        overlap: 重叠时长
    """
    input_path = Path(input_dir)
    
    # 获取所有音频文件
    audio_files = list(input_path.glob("*.wav")) + \
                  list(input_path.glob("*.mp3")) + \
                  list(input_path.glob("*.flac"))
    
    if not audio_files:
        print(f"错误：在 {input_dir} 中未找到音频文件")
        return
    
    print(f"找到 {len(audio_files)} 个文件")
    print("=" * 60)
    
    total_segments = 0
    
    for audio_file in audio_files:
        # 为每个文件创建独立输出目录
        file_output_dir = Path(output_dir) / Path(audio_file).stem
        segments = slice_audio(
            str(audio_file),
            str(file_output_dir),
            duration,
            overlap
        )
        total_segments += len(segments)
        print()
    
    print("=" * 60)
    print(f"完成：{total_segments} 个片段")


def detect_silence(
    audio: np.ndarray,
    sample_rate: int,
    threshold: float = 0.01,
    min_duration: float = 0.5
) -> List[tuple]:
    """
    检测静音段，用于智能切片
    
    Args:
        audio: 音频数据
        sample_rate: 采样率
        threshold: 静音阈值
        min_duration: 最小静音长度（秒）
        
    Returns:
        静音段列表 [(start, end), ...]
    """
    # 计算能量
    energy = audio ** 2
    
    # 检测静音点
    silence_mask = energy < threshold
    
    # 找静音段
    silence_segments = []
    in_silence = False
    silence_start = 0
    min_samples = int(min_duration * sample_rate)
    
    for i, is_silence in enumerate(silence_mask):
        if is_silence and not in_silence:
            silence_start = i
            in_silence = True
        elif not is_silence and in_silence:
            silence_end = i
            if silence_end - silence_start >= min_samples:
                silence_segments.append((silence_start, silence_end))
            in_silence = False
    
    return silence_segments


def smart_slice(
    input_file: str,
    output_dir: str,
    target_duration: float = 10.0,
    min_duration: float = 5.0,
    max_duration: float = 15.0
):
    """
    基于静音检测的智能切片
    
    Args:
        input_file: 输入文件
        output_dir: 输出目录
        target_duration: 目标时长
        min_duration: 最小长度
        max_duration: 最大长度
    """
    audio, sample_rate = librosa.load(input_file, sr=None, mono=True)
    
    # 检测静音
    silence_segments = detect_silence(audio, sample_rate)
    
    # 输出目录
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    count = 0
    
    # 在静音处切分
    prev_end = 0
    for silence_start, silence_end in silence_segments:
        segment_length = (silence_start - prev_end) / sample_rate
        
        # 符合长度要求的保存
        if min_duration <= segment_length <= max_duration:
            segment = audio[prev_end:silence_start]
            output_file = output_path / f"{Path(input_file).stem}_{count:04d}.wav"
            sf.write(str(output_file), segment, sample_rate)
            count += 1
        
        prev_end = silence_end
    
    print(f"智能切片完成：{count} 个片段")
    return count


def main():
    """命令行入口"""
    parser = argparse.ArgumentParser(
        description="音频切片工具"
    )
    
    parser.add_argument(
        "-i", "--input",
        type=str,
        required=True,
        help="输入音频文件或目录"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        required=True,
        help="输出目录"
    )
    
    parser.add_argument(
        "-d", "--duration",
        type=float,
        default=10.0,
        help="片段时长（秒）"
    )
    
    parser.add_argument(
        "-O", "--overlap",
        type=float,
        default=1.0,
        help="重叠时长（秒）"
    )
    
    parser.add_argument(
        "-s", "--smart",
        action="store_true",
        help="使用智能切片（基于静音检测）"
    )
    
    parser.add_argument(
        "--min-duration",
        type=float,
        default=5.0,
        help="最小片段时长"
    )
    
    parser.add_argument(
        "--max-duration",
        type=float,
        default=15.0,
        help="最大片段时长"
    )
    
    args = parser.parse_args()
    
    input_path = Path(args.input)
    
    if input_path.is_file():
        # 单个文件
        if args.smart:
            smart_slice(
                str(input_path),
                args.output,
                args.min_duration,
                args.max_duration
            )
        else:
            slice_audio(
                str(input_path),
                args.output,
                args.duration,
                args.overlap
            )
    else:
        # 批量处理目录
        slice_directory(
            str(input_path),
            args.output,
            args.duration,
            args.overlap
        )


if __name__ == "__main__":
    main()
