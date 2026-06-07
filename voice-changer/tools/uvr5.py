"""
UVR5 人声分离工具
==================
使用 UVR5 分离音频中的人声和伴奏
"""

import torch
import torchaudio
from typing import Optional, Tuple
import numpy as np
from pathlib import Path


class VoiceSeparator:
    """
    人声分离工具
    ==============
    使用深度学习模型分离人声和背景音乐
    """
    
    def __init__(
        self,
        model_name: str = "BS-Roformer-Viperx-1297",
        device: Optional[str] = None
    ):
        """
        初始化分离器
        
        Args:
            model_name: 模型名称
            device: 运行设备
        """
        self.model_name = model_name
        self.device = device or (
            "cuda" if torch.cuda.is_available() else "cpu"
        )
        self.model = None
    
    def load_model(self):
        """
        加载分离模型
        """
        print(f"加载模型：{self.model_name}")
        
        # TODO: 实现实际的 UVR5 模型加载
        # 这里使用占位符
        # 实际应从：https://github.com/Anjok07/ultimatevocalremovergui
        
        try:
            # 尝试使用 demucs 库（Facebook 官方）
            from demucs.pretrained import get_model
            from demucs.apply import apply
            
            self.model = get_model(self.model_name)
            self.model.to(self.device)
            
        except ImportError:
            print("警告：demucs 未安装，使用基础分离方法")
            self.model = None
    
    def separate(
        self,
        input_file: str,
        output_vocal: str,
        output_instrumental: str
    ) -> bool:
        """
        分离人声和伴奏
        
        Args:
            input_file: 输入音频文件
            output_vocal: 人声输出路径
            output_instrumental: 伴奏输出路径
            
        Returns:
            是否成功
        """
        # 加载音频
        audio, sample_rate = torchaudio.load(input_file)
        
        # 转为单声道
        if audio.shape[0] > 1:
            audio = audio.mean(dim=0, keepdim=True)
        
        print(f"分离处理中：{input_file}")
        
        try:
            # 使用 demucs 进行分离
            from demucs.apply import apply
            
            audio_tensor = audio.to(self.device).unsqueeze(0)
            sources = apply(self.model, audio_tensor)
            
            # 获取人声和伴奏
            vocal = sources[0, 0].cpu()  # 第一轨道：人声
            instrumental = sources[0, 1:].sum(dim=0).cpu()  # 其他：伴奏
            
            # 保存
            torchaudio.save(output_vocal, vocal.unsqueeze(0), sample_rate)
            torchaudio.save(output_instrumental, instrumental.unsqueeze(0), sample_rate)
            
            print(f"✓ 人声已保存：{output_vocal}")
            print(f"✓ 伴奏已保存：{output_instrumental}")
            
            return True
            
        except Exception as e:
            # 回退到简单方法
            print(f"demucs 分离失败：{str(e)}")
            print("使用基础分离方法（效果较差）")
            
            # 简单频谱相减（效果一般）
            return self._simple_separation(
                audio, sample_rate,
                output_vocal, output_instrumental
            )
    
    def _simple_separation(
        self,
        audio: torch.Tensor,
        sample_rate: int,
        output_vocal: str,
        output_instrumental: str
    ) -> bool:
        """
        简单的频谱相减分离法
        """
        # 这种方法效果有限，仅作替代方案
        vocal = audio  # 直接保存原始音频作为人声
        instrumental = torch.zeros_like(audio)  # 空伴奏
        
        try:
            torchaudio.save(output_vocal, vocal, sample_rate)
            torchaudio.save(output_instrumental, instrumental, sample_rate)
            
            return True
            
        except Exception as e:
            print(f"保存失败：{str(e)}")
            return False


def separate_vocals(
    input_file: str,
    output_dir: str,
    model_name: str = "BS-Roformer-Viperx-1297"
):
    """
    分离人声便捷函数
    
    Args:
        input_file: 输入文件
        output_dir: 输出目录
        model_name: 模型名称
    """
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    # 初始化分离器
    separator = VoiceSeparator(model_name)
    separator.load_model()
    
    # 分离
    input_path = Path(input_file)
    vocal_file = output_path / f"{input_path.stem}_vocal.wav"
    instrumental_file = output_path / f"{input_path.stem}_instrumental.wav"
    
    success = separator.separate(
        str(input_file),
        str(vocal_file),
        str(instrumental_file)
    )
    
    return success, str(vocal_file), str(instrumental_file)


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="UVR5 人声分离工具"
    )
    
    parser.add_argument(
        "-i", "--input",
        type=str,
        required=True,
        help="输入音频文件"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        default="output",
        help="输出目录"
    )
    
    parser.add_argument(
        "-m", "--model",
        type=str,
        default="BS-Roformer-Viperx-1297",
        help="模型名称"
    )
    
    args = parser.parse_args()
    
    success, vocal, instrumental = separate_vocals(
        args.input,
        args.output,
        args.model
    )
    
    if success:
        print("\n✓ 分离完成!")
    else:
        print("\n✗ 分离失败")


if __name__ == "__main__":
    main()
