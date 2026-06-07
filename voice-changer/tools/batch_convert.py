"""
批量音频转换工具
==================
用于批量转换音频文件的音色
"""

import argparse
import os
from pathlib import Path
from typing import List

from core.rvc_inference import RVCInference


def convert_audio_file(
    engine: RVCInference,
    input_file: str,
    output_file: str,
    pitch_shift: int
):
    """
    转换单个音频文件
    
    Args:
        engine: RVC 推理引擎
        input_file: 输入文件路径
        output_file: 输出文件路径
        pitch_shift: 音调偏移
    """
    import torchaudio
    import numpy as np
    
    # 加载音频
    audio_data, sample_rate = torchaudio.load(input_file)
    
    # 转为单声道
    if audio_data.shape[0] > 1:
        audio_data = audio_data.mean(dim=0)
    
    audio_data = audio_data.numpy()
    
    # 重采样到 22050
    if sample_rate != 22050:
        import librosa
        audio_data = librosa.resample(audio_data, orig_sr=sample_rate, target_sr=22050)
    
    # 执行变声
    converted = engine.convert(audio_data, pitch_shift)
    
    # 保存结果
    torchaudio.save(
        output_file,
        torch.from_numpy(converted).unsqueeze(0),
        22050
    )
    
    print(f"✓ 转换完成：{output_file}")


def batch_convert(
    model_path: str,
    input_dir: str,
    output_dir: str,
    pitch_shift: int,
    file_pattern: str = "*.wav"
):
    """
    批量转换音频文件
    
    Args:
        model_path: 模型路径
        input_dir: 输入目录
        output_dir: 输出目录
        pitch_shift: 音调偏移
        file_pattern: 文件匹配模式
    """
    import torch
    
    # 创建输出目录
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    # 加载模型
    print(f"加载模型：{model_path}")
    engine = RVCInference(model_path)
    engine.load_model()
    
    # 获取所有音频文件
    input_path = Path(input_dir)
    audio_files = list(input_path.glob(file_pattern))
    
    if not audio_files:
        print(f"错误：在 {input_dir} 中未找到 {file_pattern} 文件")
        return
    
    print(f"找到 {len(audio_files)} 个音频文件")
    print("=" * 60)
    
    # 批量转换
    success_count = 0
    for audio_file in audio_files:
        try:
            output_file = output_path / audio_file.name
            
            convert_audio_file(
                engine,
                str(audio_file),
                str(output_file),
                pitch_shift
            )
            
            success_count += 1
            
        except Exception as e:
            print(f"✗ 转换失败 {audio_file.name}: {str(e)}")
    
    print("=" * 60)
    print(f"转换完成：{success_count}/{len(audio_files)} 个文件成功")


def main():
    """命令行入口"""
    parser = argparse.ArgumentParser(
        description="批量音频变声转换工具",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 转换单个文件
  python batch_convert.py -m models/female/yujie.pth -i input.wav -o output.wav
  
  # 批量转换整个目录
  python batch_convert.py -m models/female/yujie.pth -i input_dir/ -o output_dir/
  
  # 指定音调偏移（男变女 +12）
  python batch_convert.py -m models/female/yujie.pth -i input/ -o output/ -p 12
        """
    )
    
    parser.add_argument(
        "-m", "--model",
        type=str,
        required=True,
        help="RVC 模型路径 (.pth 文件)"
    )
    
    parser.add_argument(
        "-i", "--input",
        type=str,
        required=True,
        help="输入文件或目录路径"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        required=True,
        help="输出文件或目录路径"
    )
    
    parser.add_argument(
        "-p", "--pitch-shift",
        type=int,
        default=0,
        help="音调偏移（半音），默认 0"
    )
    
    parser.add_argument(
        "-e", "--extension",
        type=str,
        default="*.wav",
        help="输入文件匹配模式，默认 *.wav"
    )
    
    args = parser.parse_args()
    
    # 检查模型文件
    if not Path(args.model).exists():
        print(f"错误：模型文件不存在：{args.model}")
        return
    
    # 判断是单个文件还是目录
    input_path = Path(args.input)
    
    if input_path.is_file():
        # 单个文件转换
        output_path = Path(args.output)
        if output_path.is_dir():
            output_path = output_path / input_path.name
        
        import torch
        engine = RVCInference(args.model)
        engine.load_model()
        convert_audio_file(engine, args.input, str(output_path), args.pitch_shift)
    
    else:
        # 批量转换
        batch_convert(
            args.model,
            args.input,
            args.output,
            args.pitch_shift,
            args.extension
        )


if __name__ == "__main__":
    main()
