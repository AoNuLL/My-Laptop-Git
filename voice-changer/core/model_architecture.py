"""
RVC 模型架构实现
==================
完整的 RVC 模型定义，用于推理和训练
"""

import torch
import torch.nn as nn
import torch.nn.functional as F
import math
from typing import Optional, Tuple


class SineWave(nn.Module):
    """正弦波激活函数"""
    
    def __init__(self, w0=30.0):
        super().__init__()
        self.w0 = w0
    
    def forward(self, x):
        return torch.sin(self.w0 * x)


class ResidualBlock(nn.Module):
    """残差块"""
    
    def __init__(
        self,
        channels: int,
        dilation: int = 1,
        kernel_size: int = 3,
        use_sn: bool = False
    ):
        super().__init__()
        padding = (kernel_size * dilation - dilation) // 2
        
        self.conv1 = nn.Conv1d(
            channels, channels,
            kernel_size=kernel_size,
            dilation=dilation,
            padding=padding
        )
        self.conv2 = nn.Conv1d(
            channels, channels,
            kernel_size=kernel_size,
            dilation=1,
            padding=kernel_size // 2
        )
        
        self.act = nn.LeakyReLU(0.2)
        
        # Spectral normalization (可选)
        if use_sn:
            self.conv1 = nn.utils.spectral_norm(self.conv1)
            self.conv2 = nn.utils.spectral_norm(self.conv2)
    
    def forward(self, x):
        residual = x
        out = self.act(x)
        out = self.conv1(out)
        out = self.act(out)
        out = self.conv2(out)
        return out + residual


class Encoder(nn.Module):
    """
    RVC 编码器
    =============
    将音频特征编码为潜在表示
    """
    
    def __init__(
        self,
        in_channels: int = 128,
        out_channels: int = 256,
        hidden_channels: int = 256,
        num_layers: int = 6,
        kernel_size: int = 3,
        dropout: float = 0.0
    ):
        super().__init__()
        self.in_channels = in_channels
        self.out_channels = out_channels
        self.hidden_channels = hidden_channels
        
        # 初始卷积
        self.conv_in = nn.Conv1d(
            in_channels, hidden_channels,
            kernel_size=kernel_size,
            padding=kernel_size // 2
        )
        
        # 残差块序列
        self.res_blocks = nn.ModuleList()
        for i in range(num_layers):
            dilation = 2 ** (i % 4)
            self.res_blocks.append(
                ResidualBlock(hidden_channels, dilation, kernel_size)
            )
        
        # 输出卷积
        self.conv_out = nn.Conv1d(
            hidden_channels, out_channels,
            kernel_size=1
        )
        
        self.dropout = nn.Dropout(dropout) if dropout > 0 else None
        self.act = nn.LeakyReLU(0.2)
    
    def forward(self, x):
        """
        Args:
            x: 输入张量 (B, D, T)
        Returns:
            编码后的特征 (B, out_channels, T)
        """
        x = self.conv_in(x)
        x = self.act(x)
        
        if self.dropout:
            x = self.dropout(x)
        
        for res_block in self.res_blocks:
            x = res_block(x)
        
        x = self.conv_out(x)
        return x


class Decoder(nn.Module):
    """
    RVC 解码器
    =============
    从潜在表示生成音频
    """
    
    def __init__(
        self,
        in_channels: int = 256,
        out_channels: int = 512,
        hidden_channels: int = 256,
        upsample_rates: Tuple[int, ...] = (8, 8, 2, 2),
        upsample_kernel_sizes: Tuple[int, ...] = (16, 16, 4, 4),
        num_res_blocks: int = 8
    ):
        super().__init__()
        self.out_channels = out_channels
        
        # 计算总上采样倍率
        self.upsample_factor = math.prod(upsample_rates)
        
        # 初始卷积
        self.conv_in = nn.Conv1d(
            in_channels, hidden_channels,
            kernel_size=7,
            padding=3
        )
        
        # 上采样层
        self.upsamples = nn.ModuleList()
        for rate, kernel_size in zip(upsample_rates, upsample_kernel_sizes):
            self.upsamples.append(
                nn.ConvTranspose1d(
                    hidden_channels, hidden_channels,
                    kernel_size=kernel_size,
                    stride=rate,
                    padding=kernel_size // 2,
                    output_padding=(kernel_size - rate) % 2
                )
            )
        
        # 残差块
        self.res_blocks = nn.ModuleList()
        for _ in range(num_res_blocks):
            for _ in range(len(upsample_rates)):
                self.res_blocks.append(
                    ResidualBlock(hidden_channels, kernel_size=3)
                )
        
        # 输出层
        self.conv_out = nn.Sequential(
            nn.LeakyReLU(0.2),
            nn.Conv1d(hidden_channels, out_channels, kernel_size=7, padding=3)
        )
        
        self.act = nn.LeakyReLU(0.2)
    
    def forward(self, x):
        x = self.conv_in(x)
        x = self.act(x)
        
        res_block_idx = 0
        for upsample in self.upsamples:
            x = upsample(x)
            x = self.act(x)
            
            # 应用残差块
            for _ in range(len(self.upsamples)):
                if res_block_idx < len(self.res_blocks):
                    x = self.res_blocks[res_block_idx](x)
                    res_block_idx += 1
        
        return self.conv_out(x)


class F0Predictor(nn.Module):
    """
    F0 (音高) 预测器
    =================
    """
    
    def __init__(
        self,
        in_channels: int = 256,
        hidden_channels: int = 256,
        num_layers: int = 3
    ):
        super().__init__()
        
        layers = []
        for i in range(num_layers):
            layers.extend([
                nn.Conv1d(
                    in_channels if i == 0 else hidden_channels,
                    hidden_channels,
                    kernel_size=3,
                    padding=1
                ),
                nn.LeakyReLU(0.2),
                nn.BatchNorm1d(hidden_channels)
            ])
        layers.append(
            nn.Conv1d(hidden_channels, 1, kernel_size=1)
        )
        
        self.network = nn.Sequential(*layers)
    
    def forward(self, x):
        """
        Args:
            x: 输入特征 (B, D, T)
        Returns:
            F0 预测值 (B, 1, T)
        """
        return self.network(x)


class RVCModel(nn.Module):
    """
    RVC 主模型
    ===========
    完整的语音转换模型
    """
    
    def __init__(
        self,
        input_channels: int = 128,
        encoder_channels: int = 256,
        decoder_channels: int = 512,
        hidden_channels: int = 256,
        num_encoder_layers: int = 6,
        num_decoder_layers: int = 8,
        use_f0: bool = True,
        use_hubert: bool = True
    ):
        super().__init__()
        
        self.use_f0 = use_f0
        self.use_hubert = use_hubert
        
        # 编码器
        self.encoder = Encoder(
            in_channels=input_channels,
            out_channels=encoder_channels,
            hidden_channels=hidden_channels,
            num_layers=num_encoder_layers
        )
        
        # F0 预测器
        if use_f0:
            self.f0_predictor = F0Predictor(
                in_channels=encoder_channels,
                hidden_channels=hidden_channels
            )
        
        # 解码器
        self.decoder = Decoder(
            in_channels=encoder_channels + (1 if use_f0 else 0),
            out_channels=decoder_channels,
            hidden_channels=hidden_channels,
            num_res_blocks=num_decoder_layers
        )
        
        # 声码器 (简化版)
        self.vocoder = nn.Conv1d(
            decoder_channels, 1,
            kernel_size=7,
            padding=3
        )
        
        self.act = nn.LeakyReLU(0.2)
    
    def forward(
        self,
        x: torch.Tensor,
        f0: Optional[torch.Tensor] = None,
        target_f0: Optional[torch.Tensor] = None
    ) -> torch.Tensor:
        """
        前向传播
        
        Args:
            x: 输入特征 (B, D, T)
            f0: 源音高 (B, 1, T)
            target_f0: 目标音高 (B, 1, T)
            
        Returns:
            生成的音频 (B, 1, T*upsample_factor)
        """
        # 编码
        encoded = self.encoder(x)
        
        # 使用目标 F0 (如果提供)
        if self.use_f0 and target_f0 is not None:
            # 将 F0 拼接到特征中
            encoded = torch.cat([encoded, target_f0], dim=1)
        elif self.use_f0 and f0 is not None:
            encoded = torch.cat([encoded, f0], dim=1)
        
        # 解码
        decoded = self.decoder(encoded)
        
        # 生成音频
        audio = self.vocoder(decoded)
        
        return audio
    
    def infer(
        self,
        features: torch.Tensor,
        f0: torch.Tensor,
        pitch_shift: float = 0.0
    ) -> torch.Tensor:
        """
        推理模式
        
        Args:
            features: 内容特征 (B, D, T)
            f0: 音高序列 (B, 1, T)
            pitch_shift: 音调偏移量
            
        Returns:
            生成的音频
        """
        # 音高偏移
        if pitch_shift != 0:
            f0 = f0 * (2 ** (pitch_shift / 12))
        
        # 前向传播
        output = self.forward(features, f0=f0, target_f0=f0)
        
        return output


def create_rvc_model(
    version: str = "v2",
    checkpoint: Optional[dict] = None
) -> RVCModel:
    """
    创建 RVC 模型
    
    Args:
        version: 模型版本 (v1, v2)
        checkpoint: 检查点字典
        
    Returns:
        模型实例
    """
    if version == "v1":
        model = RVCModel(
            input_channels=128,
            encoder_channels=256,
            decoder_channels=512,
            hidden_channels=256,
            num_encoder_layers=6,
            num_decoder_layers=8,
            use_f0=True,
            use_hubert=False
        )
    else:  # v2
        model = RVCModel(
            input_channels=256,
            encoder_channels=256,
            decoder_channels=512,
            hidden_channels=256,
            num_encoder_layers=6,
            num_decoder_layers=8,
            use_f0=True,
            use_hubert=True
        )
    
    # 加载检查点
    if checkpoint is not None:
        if "state_dict" in checkpoint:
            model.load_state_dict(checkpoint["state_dict"])
        else:
            model.load_state_dict(checkpoint)
    
    return model


# 测试代码
if __name__ == "__main__":
    print("测试 RVC 模型架构...")
    
    # 创建模型
    model = create_rvc_model("v2")
    
    # 计算参数量
    num_params = sum(p.numel() for p in model.parameters())
    print(f"模型参数量：{num_params / 1e6:.2f}M")
    
    # 测试前向传播
    batch_size = 1
    seq_len = 128
    input_features = torch.randn(batch_size, 256, seq_len)
    f0 = torch.randn(batch_size, 1, seq_len)
    
    output = model.infer(input_features, f0)
    print(f"输入形状：{input_features.shape}")
    print(f"输出形状：{output.shape}")
    
    print("✓ 模型测试通过")
