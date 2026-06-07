"""
模型训练工具
==============
用于训练自定义 RVC 音色模型
"""

import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader
import numpy as np
from pathlib import Path
from typing import Optional, Tuple, Dict
import json
from datetime import datetime
import pickle


class AudioDataset(Dataset):
    """
    音频特征数据集
    """
    
    def __init__(
        self,
        feature_dir: str,
        max_frames: int = 30720
    ):
        """
        初始化数据集
        
        Args:
            feature_dir: 特征目录
            max_frames: 最大帧数
        """
        self.feature_dir = Path(feature_dir)
        self.max_frames = max_frames
        
        # 加载所有特征文件路径
        self.feature_files = list(self.feature_dir.glob("*.pkl"))
        
        print(f"加载 {len(self.feature_files)} 个特征文件")
    
    def __len__(self) -> int:
        return len(self.feature_files)
    
    def __getitem__(self, idx: int) -> Dict[str, torch.Tensor]:
        feature_file = self.feature_files[idx]
        
        # 加载特征
        with open(feature_file, "rb") as f:
            features = pickle.load(f)
        
        # 提取各项特征
        audio = features["audio"]
        f0 = features["f0"]
        mel = features["mel"]
        
        # 截断或填充
        if len(mel) > self.max_frames:
            start = np.random.randint(0, len(mel) - self.max_frames)
            end = start + self.max_frames
            
            audio = audio[start * 256:end * 256]
            f0 = f0[start:end]
            mel = mel[start:end]
        else:
            # 填充
            pad_length = self.max_frames - len(mel)
            mel = np.pad(mel, ((0, pad_length), (0, 0)))
            f0 = np.pad(f0, (0, pad_length))
        
        # 转为 Tensor
        return {
            "audio": torch.from_numpy(audio).float(),
            "f0": torch.from_numpy(f0).float(),
            "mel": torch.from_numpy(mel).float().transpose(0, 1),  # (D, T)
            "filename": str(feature_file)
        }


class VoiceChangerTrainer:
    """
    语音转换器训练器
    ===================
    """
    
    def __init__(
        self,
        experiment_name: str,
        features_dir: str,
        checkpoint_dir: str = "checkpoints",
        batch_size: int = 16,
        learning_rate: float = 1e-4,
        num_epochs: int = 100,
        save_every: int = 10,
        num_workers: int = 4,
        mixed_precision: bool = False
    ):
        """
        初始化训练器
        
        Args:
            experiment_name: 实验名称
            features_dir: 特征目录
            checkpoint_dir: 检查点目录
            batch_size: 批次大小
            learning_rate: 学习率
            num_epochs: 训练轮数
            save_every: 保存间隔
            num_workers: 数据加载线程数
            mixed_precision: 混合精度训练
        """
        self.experiment_name = experiment_name
        self.features_dir = features_dir
        self.checkpoint_dir = Path(checkpoint_dir) / experiment_name
        self.batch_size = batch_size
        self.learning_rate = learning_rate
        self.num_epochs = num_epochs
        self.save_every = save_every
        self.num_workers = num_workers
        self.mixed_precision = mixed_precision
        
        # 创建检查点目录
        self.checkpoint_dir.mkdir(parents=True, exist_ok=True)
        
        # 设备
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print(f"使用设备：{self.device}")
        
        # 模型、优化器等
        self.model = None
        self.optimizer = None
        self.scheduler = None
        
        # 训练日志
        self.training_log = {
            "start_time": datetime.now().isoformat(),
            "config": {
                "batch_size": batch_size,
                "learning_rate": learning_rate,
                "num_epochs": num_epochs,
                "mixed_precision": mixed_precision
            },
            "epochs": []
        }
    
    def build_model(self):
        """
        构建模型
        """
        # TODO: 实现完整的 RVC 模型架构
        # 这里使用简单示例模型
        
        class SimpleVoiceModel(nn.Module):
            def __init__(self):
                super().__init__()
                # 简化的模型占位符
                self.conv = nn.Conv1d(128, 128, 3, padding=1)
                self.fc = nn.Linear(128, 128)
            
            def forward(self, x, f0):
                # 占位符前向传播
                return self.conv(x)
        
        self.model = SimpleVoiceModel().to(self.device)
        
        # 优化器
        self.optimizer = optim.AdamW(
            self.model.parameters(),
            lr=self.learning_rate
        )
        
        # 学习率调度器
        self.scheduler = optim.lr_scheduler.CosineAnnealingLR(
            self.optimizer,
            T_max=self.num_epochs
        )
    
    def train_one_epoch(self, dataloader: DataLoader, epoch: int) -> float:
        """
        训练一个 epoch
        
        Args:
            dataloader: 数据加载器
            epoch: 当前轮数
            
        Returns:
            平均损失
        """
        self.model.train()
        total_loss = 0.0
        num_batches = 0
        
        # 混合精度训练
        scaler = torch.cuda.amp.GradScaler() if self.mixed_precision else None
        
        for batch_idx, batch in enumerate(dataloader):
            # 移动数据到设备
            mel = batch["mel"].to(self.device)
            f0 = batch["f0"].to(self.device)
            
            # 前向传播
            if scaler:
                with torch.cuda.amp.autocast():
                    output = self.model(mel, f0)
                    loss = nn.MSELoss()(output, mel)
                
                # 反向传播
                scaler.scale(loss).backward()
                scaler.step(self.optimizer)
                scaler.update()
            else:
                output = self.model(mel, f0)
                loss = nn.MSELoss()(output, mel)
                
                # 反向传播
                self.optimizer.zero_grad()
                loss.backward()
                self.optimizer.step()
            
            total_loss += loss.item()
            num_batches += 1
            
            # 进度显示
            if (batch_idx + 1) % 10 == 0:
                avg_loss = total_loss / num_batches
                print(f"Epoch {epoch+1}/{self.num_epochs}, "
                      f"Batch {batch_idx+1}, Loss: {avg_loss:.4f}")
        
        return total_loss / num_batches
    
    def save_checkpoint(self, epoch: int, loss: float):
        """
        保存检查点
        
        Args:
            epoch: 当前轮数
            loss: 损失值
        """
        checkpoint = {
            "epoch": epoch,
            "model_state_dict": self.model.state_dict(),
            "optimizer_state_dict": self.optimizer.state_dict(),
            "loss": loss,
            "config": self.training_log["config"]
        }
        
        checkpoint_path = self.checkpoint_dir / f"g_{epoch}.pth"
        torch.save(checkpoint, checkpoint_path)
        print(f"✓ 检查点已保存：{checkpoint_path}")
    
    def train(self):
        """
        开始训练
        """
        print("=" * 60)
        print(f"开始训练：{self.experiment_name}")
        print("=" * 60)
        
        # 加载数据
        dataset = AudioDataset(self.features_dir)
        dataloader = DataLoader(
            dataset,
            batch_size=self.batch_size,
            shuffle=True,
            num_workers=self.num_workers,
            collate_fn=lambda x: {
                key: torch.stack([item[key] for item in x]) 
                if key != "filename" 
                else [item["filename"] for item in x]
                for key in x[0].keys()
            }
        )
        
        # 构建模型
        self.build_model()
        
        # 训练循环
        best_loss = float("inf")
        
        for epoch in range(self.num_epochs):
            # 训练
            train_loss = self.train_one_epoch(dataloader, epoch)
            
            # 更新学习率
            self.scheduler.step()
            
            # 记录日志
            epoch_info = {
                "epoch": epoch + 1,
                "train_loss": train_loss,
                "lr": self.scheduler.get_last_lr()[0],
                "time": datetime.now().isoformat()
            }
            
            self.training_log["epochs"].append(epoch_info)
            
            print(f"Epoch {epoch+1}/{self.num_epochs} - Loss: {train_loss:.4f}")
            
            # 保存检查点
            if (epoch + 1) % self.save_every == 0:
                self.save_checkpoint(epoch + 1, train_loss)
                if train_loss < best_loss:
                    best_loss = train_loss
        
        # 保存最终模型
        final_checkpoint = self.checkpoint_dir / "g_final.pth"
        torch.save({
            "model_state_dict": self.model.state_dict(),
            "config": self.training_log["config"]
        }, final_checkpoint)
        print(f"✓ 最终模型已保存：{final_checkpoint}")
        
        # 保存训练日志
        log_path = self.checkpoint_dir / "training_log.json"
        with open(log_path, "w") as f:
            json.dump(self.training_log, f, indent=2)
        
        print("=" * 60)
        print("训练完成!")
        print("=" * 60)


def train_model(
    experiment_name: str,
    features_dir: str,
    **kwargs
):
    """
    训练模型
    
    Args:
        experiment_name: 实验名称
        features_dir: 特征目录
        **kwargs: 其他训练参数
    """
    trainer = VoiceChangerTrainer(
        experiment_name=experiment_name,
        feature_dir=features_dir,
        **kwargs
    )
    trainer.train()


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="RVC 模型训练工具"
    )
    
    parser.add_argument(
        "-n", "--experiment-name",
        type=str,
        required=True,
        help="实验名称"
    )
    
    parser.add_argument(
        "-f", "--features-dir",
        type=str,
        required=True,
        help="特征目录"
    )
    
    parser.add_argument(
        "-b", "--batch-size",
        type=int,
        default=16,
        help="批次大小"
    )
    
    parser.add_argument(
        "-e", "--epochs",
        type=int,
        default=100,
        help="训练轮数"
    )
    
    parser.add_argument(
        "--lr",
        type=float,
        default=1e-4,
        help="学习率"
    )
    
    parser.add_argument(
        "--save-every",
        type=int,
        default=10,
        help="保存间隔"
    )
    
    parser.add_argument(
        "--mixed-precision",
        action="store_true",
        help="启用混合精度训练"
    )
    
    args = parser.parse_args()
    
    train_model(
        args.experiment_name,
        args.features_dir,
        batch_size=args.batch_size,
        num_epochs=args.epochs,
        learning_rate=args.lr,
        save_every=args.save_every,
        mixed_precision=args.mixed_precision
    )


if __name__ == "__main__":
    main()
