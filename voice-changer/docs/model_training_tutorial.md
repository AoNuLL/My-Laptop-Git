# 模型训练详细教程

本教程将指导你从零开始训练自己的 RVC 音色模型。

## 目录

1. [环境准备](#环境准备)
2. [数据采集](#数据采集)
3. [数据预处理](#数据预处理)
4. [特征提取](#特征提取)
5. [模型训练](#模型训练)
6. [模型测试](#模型测试)
7. [模型优化](#模型优化)
8. [常见问题](#常见问题)

---

## 环境准备

### 硬件要求

| 配置 | 最低要求 | 推荐配置 |
|------|----------|----------|
| GPU | GTX 1060 (6GB) | RTX 3070 (8GB+) |
| CPU | 4 核 | 8 核+ |
| 内存 | 8GB | 16GB+ |
| 存储 | 50GB | 100GB+ SSD |

### 软件安装

```bash
# 1. 安装 PyTorch (带 GPU 支持)
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118

# 2. 安装依赖
pip install -r requirements.txt

# 3. 安装额外工具
pip install pyworld torchcrepe fairseq
```

### 验证安装

```bash
python tools/test_system.py
```

---

## 数据采集

### 素材要求

- **时长**: 最少 10 分钟，推荐 30-60 分钟
- **质量**: 清晰、无背景噪音、无混响
- **格式**: WAV (16bit/24bit, 44.1kHz/48kHz)
- **内容**: 自然说话，避免唱歌

### 采集方法

#### 方法 1: 自己录制

```bash
# 使用 Audacity 或其他录音软件
# 1. 设置采样率 48kHz
# 2. 在安静环境录制
# 3. 朗读文本（新闻、书籍等）
```

#### 方法 2: 从视频提取

```bash
# 使用 ffmpeg 从视频提取音频
ffmpeg -i input_video.mp4 -vn -acodec pcm_s16le -ar 48000 output.wav
```

#### 方法 3: 下载现成数据集

- AISHELL-3 (中文)
- LJ Speech (英文)
- VCTK (多说话人)

### 数据集组织

```
datasets/
└── my_voice/
    ├── raw/              # 原始音频
    │   ├── 001.wav
    │   ├── 002.wav
    │   └── ...
    └── sliced/           # 切片后
        ├── 001_0001.wav
        ├── 001_0002.wav
        └── ...
```

---

## 数据预处理

### 1. 人声分离

如果素材有背景音乐，先分离人声：

```bash
python tools/uvr5.py -i audio.mp3 -o output/
```

输出：
- `audio_vocal.wav` - 人声
- `audio_instrumental.wav` - 伴奏

### 2. 音频切片

将长音频切分为 5-10 秒片段：

```bash
python tools/slice_audio.py \
    -i datasets/my_voice/raw/ \
    -o datasets/my_voice/sliced/ \
    -d 10 \
    -O 1
```

参数说明：
- `-d 10`: 每段 10 秒
- `-O 1`: 重叠 1 秒

### 3. 数据增强

增加数据多样性：

```bash
python tools/augment_data.py \
    -i datasets/my_voice/sliced/ \
    -o datasets/my_voice/augmented/ \
    -n 3 \
    -p -5 5 \
    -t 0.9 1.1
```

这将为每个文件生成 3 个增强版本（音高偏移 ±5 半音，速度 0.9-1.1 倍）。

---

## 特征提取

### 提取音频特征

```bash
python tools/extract_features.py \
    -i datasets/my_voice/augmented/ \
    -o features/my_voice/ \
    -p 4 \
    -m harvest \
    --hubert
```

参数说明：
- `-p 4`: 4 个并行进程
- `-m harvest`: 使用 harvest 方法提取音高（更准确）
- `--hubert`: 提取 HuBERT 内容特征

### 输出文件

每个音频文件生成一个 `.pkl` 文件，包含：
- `audio`: 原始音频
- `f0`: 音高序列
- `mel`: 梅尔频谱
- `hubert`: HuBERT 特征（如果启用）

---

## 模型训练

### 基础训练

```bash
python tools/train_model.py \
    -n my_voice_model \
    -f features/my_voice/ \
    -b 16 \
    -e 100 \
    --lr 0.0001 \
    --save-every 10
```

参数说明：
- `-n`: 实验名称
- `-f`: 特征目录
- `-b`: 批次大小（根据显存调整）
- `-e`: 训练轮数
- `--lr`: 学习率
- `--save-every`: 每 N 轮保存一次

### 训练过程

训练时会看到类似输出：

```
使用设备：cuda
加载 320 个特征文件
============================================================
Epoch 1/100, Batch 10, Loss: 0.5234
Epoch 1/100, Batch 20, Loss: 0.4891
...
Epoch 1/100 - Loss: 0.5012
✓ 检查点已保存：checkpoints/my_voice_model/g_10.pth
...
============================================================
训练完成!
============================================================
```

### 监控训练

使用 TensorBoard 监控：

```bash
# 安装 TensorBoard
pip install tensorboard

# 启动
tensorboard --logdir checkpoints/my_voice_model

# 浏览器访问 http://localhost:6006
```

### 混合精度训练（节省显存）

```bash
python tools/train_model.py \
    -n my_voice_model \
    -f features/my_voice/ \
    --mixed-precision
```

---

## 模型测试

### 1. 使用测试音频

```bash
python tools/batch_convert.py \
    -m checkpoints/my_voice_model/g_final.pth \
    -i test_audio.wav \
    -o output.wav \
    -p 0
```

### 2. 调节音调

男变女（+8 到 +12 半音）：

```bash
python tools/batch_convert.py \
    -m checkpoints/my_voice_model/g_final.pth \
    -i test_male.wav \
    -o output_female.wav \
    -p 10
```

女变男（-8 到 -12 半音）：

```bash
python tools/batch_convert.py \
    -m checkpoints/my_voice_model/g_final.pth \
    -i test_female.wav \
    -o output_male.wav \
    -p -10
```

### 3. 实时测试

将模型文件复制到 `models/custom/` 目录，然后在 GUI 中选择测试。

---

## 模型优化

### 1. 调整超参数

| 参数 | 效果不好 | 建议调整 |
|------|----------|----------|
| 学习率太高 (震荡) | 0.001 | 降低到 0.0001 |
| 收敛太慢 | 0.0001 | 提高到 0.0005 |
| 批次太小 (显存不足) | 4 | 使用混合精度 |

### 2. 增加数据

- 录制更多高质量音频
- 增加增强倍数 (`-n 5` 而不是 `-n 3`)
- 调整增强范围

### 3. 训练更久

```bash
# 增加轮数到 200-300
python tools/train_model.py \
    -n my_voice_model \
    -f features/my_voice/ \
    -e 200
```

### 4. 模型融合

融合多个模型创造独特音色：

```bash
python tools/merge_models.py merge \
    -m model1.pth model2.pth \
    -w 0.7 0.3 \
    -o merged.pth
```

---

## 常见问题

### Q1: CUDA out of memory

**解决方案**:

```bash
# 1. 减小批次大小
-b 8  # 或 -b 4

# 2. 使用混合精度
--mixed-precision

# 3. 降低采样率
在配置中设置 sample_rate = 22050
```

### Q2: 训练损失不下降

**原因**:
- 学习率太低
- 数据质量问题
- 模型架构不匹配

**解决方案**:
1. 提高学习率到 0.0005
2. 检查音频质量，重新预处理
3. 增加训练轮数

### Q3: 变声效果差

**原因**:
- 模型训练不足
- 音调设置不当
- 数据不够

**解决方案**:
1. 继续训练模型（增加轮数）
2. 调整 `pitch_shift` 参数
3. 录制更多高质量数据

### Q4: 有电音/机械音

**原因**:
- 音高提取方法不好
- 模型质量问题

**解决方案**:
1. 使用 `harvest` 或 `crepe` 方法
2. 重新训练模型
3. 尝试其他预训练模型

### Q5: 训练很慢

**优化方法**:

```bash
# 1. 使用 GPU
确保 PyTorch 已正确安装 CUDA 版本

# 2. 增加并行进程
-p 8  # 根据你的 CPU 核心数

# 3. 使用更快的 SSD 存储数据
```

---

## 进阶技巧

### 1. 迁移学习

基于预训练模型微调：

```bash
# 在训练脚本中加载预训练权重
python tools/train_model.py \
    -n my_finetuned_model \
    -f features/my_voice/ \
    --pretrained checkpoints/pretrained/base.pth \
    -e 50  # 较少轮数即可
```

### 2. 多说话人模型

训练可切换音色的模型：
- 需要多个说话人的数据
- 每个说话人至少 10 分钟
- 在配置中启用 `multi_speaker = true`

### 3. 高质量模型

追求极致音质：

```bash
# 使用更大的模型
在 config 中设置：
  hidden_channels = 512
  num_layers = 12

# 训练更久
-e 300

# 使用更好的特征提取
-m crepe
--hubert
```

---

## 模型分享

训练好的模型可以分享到社区：

### 1. 打包模型

```bash
# 创建压缩包
zip -r my_voice_model.zip \
    checkpoints/my_voice_model/g_final.pth \
    README.md
```

### 2. 上传平台

- GitHub Releases
- HuggingFace Datasets
- 百度网盘 / 夸克网盘

### 3. 提供信息

在分享时提供：
- 模型名称
- 训练数据描述
- 推荐参数（pitch_shift 等）
- 使用示例

---

## 总结

训练高质量 RVC 模型的关键：

1. **数据质量第一** - 清晰、无噪音
2. **足够的数据量** - 30 分钟+
3. **合适的超参数** - 根据显存调整
4. **充分的训练** - 100-200 轮
5. **仔细的测试** - 调整音调参数

祝你训练成功！如有问题，请查看 `docs/troubleshooting.md` 或在 GitHub 提 Issue。
