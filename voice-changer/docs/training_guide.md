# 模型训练指南

本指南介绍如何训练自己的 RVC 音色模型。

## 环境准备

### 硬件要求

- **GPU**: NVIDIA 显卡 (GTX 1060 及以上，推荐 RTX 3060+)
- **显存**: 最低 6GB，推荐 12GB+
- **内存**: 16GB+
- **存储**: 至少 50GB 可用空间

### 软件依赖

```bash
pip install torch torchaudio librosa soundfile pyworld
pip install -r requirements.txt
```

## 步骤 1: 准备训练数据

### 音频素材要求

- **格式**: WAV (44100Hz 或 48000Hz, 16bit 或 24bit)
- **时长**: 最少 10 分钟，推荐 30 分钟 +
- **质量**: 清晰的人声，无背景音乐，无噪音
- **内容**: 说话的音频（唱歌数据需特殊处理）

### 音频预处理

1. **降噪处理**

使用音频编辑软件（如 Audacity）进行降噪：

```
效果 -> 降噪 -> 获取噪声样本 -> 效果 -> 降噪 -> 确定
```

2. **人声分离**（如有背景音乐）

使用 UVR5 或在线工具分离人声：

```bash
python tools/uvr5.py --input audio.mp3 --output vocal.wav
```

3. **音频切片**

将长音频切分为 5-10 秒的片段：

```bash
python tools/slice_audio.py \
    --input vocal.wav \
    --output output_folder \
    --duration 10 \
    --overlap 1
```

### 数据集组织

```
datasets/
└── my_voice/           # 你的音色名称
    ├── 0001.wav
    ├── 0002.wav
    ├── 0003.wav
    └── ...
```

## 步骤 2: 提取特征

运行特征提取脚本：

```bash
python tools/extract_features.py \
    --input_dir datasets/my_voice \
    --output_dir features/my_voice \
    --num_processes 4
```

参数说明：
- `--input_dir`: 原始音频目录
- `--output_dir`: 特征输出目录
- `--num_processes`: 并行处理进程数

## 步骤 3: 训练模型

### 基础训练命令

```bash
python tools/train_model.py \
    --experiment_name my_voice \
    --features_dir features/my_voice \
    --batch_size 16 \
    --epochs 100 \
    --save_every 10
```

### 参数详解

| 参数 | 说明 | 推荐值 |
|------|------|--------|
| `batch_size` | 批次大小 | 16-32 (根据显存调整) |
| `epochs` | 训练轮数 | 100-300 |
| `learning_rate` | 学习率 | 1e-4 |
| `save_every` | 保存间隔 | 10 |

### 训练进度监控

训练过程中会输出:

```
Epoch [50/100] Loss: 0.3245  Time: 2m 15s
Checkpoint saved: checkpoints/my_voice/g_50.pth
```

## 步骤 4: 测试模型

训练完成后，测试模型效果：

```bash
python tools/test_model.py \
    --model checkpoints/my_voice/g_100.pth \
    --input test_audio.wav \
    --output output.wav \
    --pitch_shift 0
```

## 步骤 5: 导出模型

将训练好的模型部署到变声器：

```bash
python tools/export_model.py \
    --checkpoint checkpoints/my_voice/g_100.pth \
    --output models/custom/my_voice.pth
```

## 高级技巧

### 混合精度训练

使用混合精度训练可以加速训练并减少显存占用：

```bash
python tools/train_model.py \
    --experiment_name my_voice \
    --mixed_precision true
```

### 迁移学习

基于预训练模型微调，可以显著减少训练时间：

```bash
python tools/train_model.py \
    --experiment_name my_voice \
    --pretrained_model checkpoints/pretrained/baseline.pth \
    --epochs 50
```

### 数据增强

通过音高偏移和时间伸缩增加数据多样性：

```bash
python tools/augment_data.py \
    --input_dir datasets/my_voice \
    --output_dir datasets/my_voice_augmented \
    --pitch_range -5 5 \
    --time_stretch 0.9 1.1
```

## 常见问题

### Q: 训练过程中显存不足怎么办？

A: 解决方法：

1. 减小 `batch_size` (如 16 -> 8 -> 4)
2. 使用混合精度训练 (`--mixed_precision true`)
3. 减少音频采样率 (48000 -> 22050)

### Q: 模型音质不佳?

A: 可能原因：

1. **训练数据不足**: 增加到 30 分钟 +
2. **数据质量差**: 重新预处理，确保无噪音
3. **训练轮数不够**: 增加 epochs 到 200+
4. **过拟合**: 添加数据增强，减小模型容量

### Q: 训练收敛慢?

A: 优化建议：

1. 使用预训练模型初始化
2. 调整学习率（过大或过小都会影响收敛）
3. 检查数据标签是否正确

### Q: 变声时有电音/机械音?

A: 解决方法：

1. 使用更好的音高提取方法 (`--f0_method harvest`)
2. 调整推理时的 `pitch_shift` 参数
3. 增加训练数据量

## 参考资源

- [RVC 官方教程](https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI)
- [音频特征提取原理](https://librosa.org/doc/main/feature.html)
- [VITS 论文](https://arxiv.org/abs/2106.06103)

## 模型分享

训练好的模型可以分享到社区：

- GitHub Releases
- HuggingFace Datasets
- 网盘分享

---

**注意**: 训练音色模型请遵守法律法规，不要侵犯他人声音权益。
