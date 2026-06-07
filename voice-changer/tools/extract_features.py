"""
音频特征提取工具
==================
用于训练 RVC 模型时提取音频特征
"""

import numpy as np
import librosa
import torch
import torchaudio
from pathlib import Path
from typing import Tuple, List, Optional
from concurrent.futures import ProcessPoolExecutor
import os


def load_audio(file_path: str, sr: int = 22050) -> np.ndarray:
    """
    加载音频文件
    
    Args:
        file_path: 音频文件路径
        sr: 目标采样率
        
    Returns:
        音频数据 (numpy array, float32)
    """
    try:
        # 使用 librosa 加载
        audio, sample_rate = librosa.load(file_path, sr=sr, mono=True)
        return audio.astype(np.float32)
    
    except Exception as e:
        print(f"加载音频失败 {file_path}: {str(e)}")
        return None


def compute_f0(
    audio: np.ndarray,
    sr: int = 22050,
    method: str = "pm",
    hop_length: int = 512
) -> np.ndarray:
    """
    提取音高 (F0) 特征
    
    Args:
        audio: 音频数据
        sr: 采样率
        method: 提取方法 (pm, harvest, crepe)
        hop_length: 跳帧长度
        
    Returns:
        音高序列
    """
    if method == "pm":
        # Parselmouth 方法 (快速)
        import librosa
        f0, voiced_flag, _ = librosa.pyin(
            audio,
            fmin=65,
            fmax=1100,
            sr=sr,
            frame_length=2048,
            hop_length=hop_length,
            fill_method="zero"
        )
        return f0
    
    elif method == "harvest":
        # Harvest 方法 (高质量，需要 pyworld)
        try:
            import pyworld as pw
            f0, _ = pw.harvest(
                audio.astype(np.float64),
                sr,
                frame_period=hop_length / sr * 1000
            )
            return f0
        except ImportError:
            print("警告：pyworld 未安装，回退到 pm 方法")
            return compute_f0(audio, sr, "pm", hop_length)
    
    elif method == "crepe":
        # Crepe 方法 (深度学习，最准确)
        try:
            import torchcrepe
            audio_tensor = torch.from_numpy(audio).float().unsqueeze(0)
            
            f0 = torchcrepe.predict(
                audio_tensor,
                sr,
                hop_length,
                fmin=65,
                fmax=1100,
                batch_size=512,
                device="cuda" if torch.cuda.is_available() else "cpu",
                return_periodicity=False
            )
            
            return f0.squeeze().cpu().numpy()
            
        except ImportError:
            print("警告：torchcrepe 未安装，回退到 pm 方法")
            return compute_f0(audio, sr, "pm", hop_length)
    
    else:
        raise ValueError(f"不支持的音高提取方法：{method}")


def compute_mfcc(
    audio: np.ndarray,
    sr: int = 22050,
    n_mfcc: int = 13,
    hop_length: int = 512
) -> np.ndarray:
    """
    提取 MFCC 特征
    
    Args:
        audio: 音频数据
        sr: 采样率
        n_mfcc: MFCC 系数数量
        hop_length: 跳帧长度
        
    Returns:
        MFCC 特征矩阵
    """
    mfcc = librosa.feature.mfcc(
        y=audio,
        sr=sr,
        n_mfcc=n_mfcc,
        hop_length=hop_length
    )
    
    # 标准化
    mfcc = (mfcc - mfcc.mean(axis=0)) / mfcc.std(axis=0)
    
    return mfcc.T


def compute_spectrogram(
    audio: np.ndarray,
    sr: int = 22050,
    n_fft: int = 2048,
    hop_length: int = 512
) -> Tuple[np.ndarray, np.ndarray]:
    """
    提取频谱特征
    
    Args:
        audio: 音频数据
        sr: 采样率
        n_fft: FFT 大小
        hop_length: 跳帧长度
        
    Returns:
        (幅度谱，相位谱)
    """
    # 短时傅里叶变换
    D = librosa.stft(audio, n_fft=n_fft, hop_length=hop_length)
    
    # 幅度谱和相位谱
    magnitude = np.abs(D)
    phase = np.angle(D)
    
    return magnitude, phase


def extract_mel_spectrogram(
    audio: np.ndarray,
    sr: int = 22050,
    n_mels: int = 128,
    hop_length: int = 512
) -> np.ndarray:
    """
    提取梅尔频谱
    
    Args:
        audio: 音频数据
        sr: 采样率
        n_mels: 梅尔滤波器数量
        hop_length: 跳帧长度
        
    Returns:
        梅尔频谱 (dB)
    """
    mel_spec = librosa.feature.melspectrogram(
        y=audio,
        sr=sr,
        n_mels=n_mels,
        hop_length=hop_length
    )
    
    # 转换为 dB
    mel_spec_db = librosa.power_to_db(mel_spec, ref=np.max)
    
    return mel_spec_db


def extract_hubert_features(
    audio: np.ndarray,
    sr: int = 22050,
    model_name: str = "facebook/hubert-base-ls960"
) -> torch.Tensor:
    """
    提取 HuBERT 内容特征
    
    Args:
        audio: 音频数据
        sr: 采样率
        model_name: HuBERT 模型名称
        
    Returns:
        HuBERT 特征张量
    """
    try:
        from transformers import HubertModel, Wav2Vec2FeatureExtractor
        
        # 加载模型和特征提取器
        model = HubertModel.from_pretrained(model_name)
        feature_extractor = Wav2Vec2FeatureExtractor.from_pretrained(model_name)
        
        # 重采样到 16kHz (HuBERT 要求)
        if sr != 16000:
            audio = librosa.resample(audio, orig_sr=sr, target_sr=16000)
        
        # 提取特征
        inputs = feature_extractor(
            audio,
            sampling_rate=16000,
            return_tensors="pt",
            padding=True
        )
        
        with torch.no_grad():
            outputs = model(**inputs)
        
        # 获取最后一层隐藏状态
        features = outputs.last_hidden_state
        
        # 上采样到目标帧率
        features = features.transpose(1, 2)  # (B, D, T)
        
        return features.squeeze(0)  # (D, T)
        
    except ImportError:
        print("警告：transformers 未安装，返回空特征")
        return torch.zeros(768, len(audio) // 320)


def extract_all_features(
    audio_file: str,
    output_dir: str,
    f0_method: str = "pm",
    use_hubert: bool = False
):
    """
    提取所有特征
    
    Args:
        audio_file: 音频文件路径
        output_dir: 输出目录
        f0_method: 音高提取方法
        use_hubert: 是否提取 HuBERT 特征
    """
    import pickle
    import json
    
    # 加载音频
    audio = load_audio(audio_file)
    if audio is None:
        return
    
    # 提取特征
    features = {}
    
    # 1. 音高特征
    features["f0"] = compute_f0(audio, method=f0_method)
    
    # 2. 梅尔频谱
    features["mel"] = extract_mel_spectrogram(audio)
    
    # 3. MFCC
    features["mfcc"] = compute_mfcc(audio)
    
    # 4. 原始音频
    features["audio"] = audio
    
    # 5. HuBERT 特征 (可选)
    if use_hubert:
        try:
            features["hubert"] = extract_hubert_features(audio)
        except Exception as e:
            print(f"HuBERT 提取失败：{str(e)}")
    
    # 保存特征
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    audio_name = Path(audio_file).stem
    
    # 保存为 pkl 文件
    pkl_path = output_path / f"{audio_name}.pkl"
    with open(pkl_path, "wb") as f:
        pickle.dump(features, f)
    
    # 保存基本信息
    info = {
        "file": str(audio_file),
        "duration": len(audio) / 22050,
        "f0_method": f0_method,
        "use_hubert": use_hubert
    }
    
    info_path = output_path / f"{audio_name}.info.json"
    with open(info_path, "w") as f:
        json.dump(info, f, indent=2)
    
    print(f"✓ 特征提取完成：{audio_name}")
    return pkl_path


def process_directory(
    input_dir: str,
    output_dir: str,
    num_processes: int = 4,
    f0_method: str = "pm",
    use_hubert: bool = False
):
    """
    批量处理目录中的所有音频文件
    
    Args:
        input_dir: 输入音频目录
        output_dir: 输出特征目录
        num_processes: 并行进程数
        f0_method: 音高提取方法
        use_hubert: 是否提取 HuBERT 特征
    """
    input_path = Path(input_dir)
    output_path = Path(output_dir)
    
    # 获取所有音频文件
    audio_files = list(input_path.glob("*.wav")) + \
                  list(input_path.glob("*.mp3")) + \
                  list(input_path.glob("*.flac"))
    
    if not audio_files:
        print(f"错误：在 {input_dir} 中未找到音频文件")
        return
    
    print(f"找到 {len(audio_files)} 个音频文件")
    print(f"使用 {num_processes} 个进程处理")
    print("=" * 60)
    
    # 创建任务列表
    tasks = [
        (str(audio_file), str(output_dir), f0_method, use_hubert)
        for audio_file in audio_files
    ]
    
    # 并行处理
    if num_processes > 1:
        with ProcessPoolExecutor(max_workers=num_processes) as executor:
            from functools import partial
            
            process_func = partial(
                extract_all_features,
                output_dir=output_dir,
                f0_method=f0_method,
                use_hubert=use_hubert
            )
            
            # executor.map 不支持多参数，使用 starmap
            list(executor.map(
                lambda x: extract_all_features(*x),
                tasks
            ))
    else:
        # 串行处理
        for audio_file in audio_files:
            extract_all_features(
                str(audio_file),
                output_dir,
                f0_method,
                use_hubert
            )
    
    print("=" * 60)
    print(f"特征提取完成：{len(audio_files)} 个文件")


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="音频特征提取工具"
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
        help="输出特征目录"
    )
    
    parser.add_argument(
        "-p", "--processes",
        type=int,
        default=4,
        help="并行进程数"
    )
    
    parser.add_argument(
        "-m", "--f0-method",
        type=str,
        choices=["pm", "harvest", "crepe"],
        default="pm",
        help="音高提取方法"
    )
    
    parser.add_argument(
        "--hubert",
        action="store_true",
        help="提取 HuBERT 特征"
    )
    
    args = parser.parse_args()
    
    input_path = Path(args.input)
    
    if input_path.is_file():
        # 单个文件
        extract_all_features(
            str(input_path),
            args.output,
            args.f0_method,
            args.hubert
        )
    else:
        # 批量处理目录
        process_directory(
            str(input_path),
            args.output,
            args.processes,
            args.f0_method,
            args.hubert
        )


if __name__ == "__main__":
    main()
