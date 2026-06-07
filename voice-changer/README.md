# Voice Changer - 开源实时变声器

一个基于 AI 技术的免费开源实时语音变声软件，支持多种先进的语音转换模型。

## 功能特性

- **实时变声**：低至 50ms 的超低延迟实时语音转换
- **多模型支持**：支持 RVC、Beatrice 等多种 AI 变声模型
- **音色库**：内置 600+ 预设音色模型（萝莉、御姐、正太、大叔等）
- **虚拟声卡**：自动安装虚拟音频驱动，兼容所有语音软件
- **GPU 加速**：支持 NVIDIA CUDA 加速，提升变声性能
- **跨平台**：支持 Windows、macOS、Linux
- **隐私保护**：所有处理均在本地完成，不上传云端

## 快速开始

### 环境要求

- Python 3.8+
- Windows 10/11, macOS 10.15+, Linux
- 建议使用 NVIDIA GPU（GTX 1060 及以上）
- 8GB 内存
- 20GB 可用存储空间

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/voice-changer.git
cd voice-changer
```

#### 2. 安装依赖

**Windows (NVIDIA GPU)**：

```bash
pip install -r requirements.txt
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
```

**Windows (无 GPU/AMD/Intel)**：

```bash
pip install -r requirements.txt
```

**macOS (Apple Silicon)**：

```bash
pip install -r requirements.txt
pip install torch torchvision torchaudio
```

**Linux**：

```bash
sudo apt-get update
sudo apt-get install -y portaudio19-dev python3-pyaudio
pip install -r requirements.txt
```

#### 3. 下载音色模型

**方式 1: 命令行下载**
```bash
# 下载单个模型
python tools/download_models.py -m female/yujie

# 下载所有推荐模型
python tools/download_models.py -a

# 下载合集包
python tools/download_models.py -p "基础包"
```

**方式 2: 图形界面下载**
启动程序后，点击左侧模型面板的 **"📥 下载更多模型"** 按钮，在弹出的下载中心选择下载。

**方式 3: 手动下载**
从以下地址下载模型包：
- **夸克网盘**: https://pan.quark.cn/s/df5642c6567b (600+ 模型合集，推荐)
- **HuggingFace**: https://huggingface.co/datasets/RVC-Project/models (官方仓库)
- **百度网盘**: https://pan.baidu.com/s/1RVC_Models (提取码:rvcc)

#### 4. 运行程序

```bash
python main.py
```

或在 Windows 上双击运行：

```bash
go-realtime-gui.bat
```

## 使用说明

### 基础使用

1. **启动程序**：运行 `python main.py` 或在 Windows 上双击 `go-realtime-gui.bat`
2. **选择模型**：在界面左侧选择你喜欢的音色模型
3. **安装虚拟声卡**：首次使用点击"安装虚拟声卡"按钮
4. **开始变声**：点击"Start"按钮开始实时变声
5. **应用变声**：在语音软件（QQ、Discord 等）中将麦克风设置为"RVC Virtual Audio Cable"

### 参数调节

- **音调 (Pitch)**：调节变声音高，+12 表示升高一个八度，-12 表示降低一个八度
  - 男变女：建议 +8 到 +12
  - 女变男：建议 -8 到 -12
- **响度 (Volume)**：调节输出音量
- **延迟 (Buffer)**：调节音频缓冲区大小，越小延迟越低，但可能影响稳定性

### 高级功能

#### 模型融合 (Merge Lab)

可以融合多个模型，创造独特的混合音色：

1. 点击"Merge Lab"标签页
2. 选择两个或多个模型
3. 调节融合比例
4. 点击"Generate"生成融合模型

#### 噪音抑制

打开"Settings" -> "Noise Reduction"启用噪音抑制功能

#### 模型训练（高级用户）

参考 `docs/training_guide.md` 了解如何训练自己的音色模型

## 项目结构

```
voice-changer/
├── main.py                    # 程序入口
├── gui/                       # 图形界面模块
│   ├── main_window.py        # 主窗口
│   ├── model_manager.py      # 模型管理界面
│   └── settings.py           # 设置界面
├── core/                      # 核心功能
│   ├── audio_processor.py    # 音频处理
│   ├── rvc_inference.py      # RVC 推理
│   └── virtual_audio.py      # 虚拟音频驱动
├── models/                    # 音色模型目录
│   ├── female/               # 女声音色
│   ├── male/                 # 男声音色
│   ├── anime/                # 动漫角色
│   └── custom/               # 自定义模型
├── tools/                     # 工具脚本
│   ├── download_models.py    # 模型下载工具
│   └── train_model.py        # 模型训练工具
├── docs/                      # 文档目录
│   ├── training_guide.md     # 模型训练指南
│   └── troubleshooting.md    # 问题排查
├── requirements.txt          # Python 依赖
└── README.md                 # 项目说明
```

## 技术原理

### RVC (Retrieval-based Voice Conversion)

RVC 是基于 VITS（Variational Inference Text-to-Speech）的语音转换系统，核心优势：

1. **检索机制**：通过特征检索匹配目标音色，提升音质和相似度
2. **小样本学习**：仅需少量音频样本即可训练出高质量模型
3. **实时推理**：优化后的推理引擎支持低延迟实时转换
4. **音色融合**：支持多个模型的线性融合，创造新音色

### 音频处理流程

```
麦克风输入 -> 音频分帧 -> 特征提取 -> RVC 模型推理 -> 声码器合成 -> 虚拟声卡输出
```

延迟计算：
```
总延迟 = 缓冲区大小 / 采样率 + 特征提取时间 + 模型推理时间 + 声码器合成时间
典型值：30ms + 5ms + 15ms + 10ms = 60ms
```

## 常见问题

### Q: 延迟太高怎么办？

A: 可以尝试以下方法降低延迟：

1. 在设置中减小"Buffer Size"（建议 256 或 512）
2. 关闭不必要的后台程序
3. 使用 GPU 加速（NVIDIA 用户安装 CUDA 版本）
4. 使用 ONNX 模型（通常比 PyTorch 模型更快）

### Q: 为什么我的声音听起来很机械？

A: 可能的原因：

1. 模型质量不佳，尝试下载其他模型
2. 音调设置不当，调整 Pitch 值
3. 模型与你的原声不匹配，尝试不同的模型
4. 录音质量差，使用更好的麦克风

### Q: 虚拟声卡安装失败？

A: 解决方案：

1. 以管理员身份运行程序
2. Windows 用户安装 VB-Audio Cable：https://vb-audio.com/Cable/
3. 重启电脑后重试

### Q: 50 系显卡无法使用？

A: 请下载支持 50 系显卡的版本（50x），或在设置中启用 ONNX Runtime

## 贡献指南

我们欢迎各种形式的贡献：

- 报告 Bug 和提出功能建议
- 分享你训练的音色模型
- 改进文档和翻译
- 参与代码开发和优化

贡献方式：

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 社区支持

- **QQ 群**：123456789（变声器技术交流）
- **Discord**: https://discord.gg/voicechanger
- **Telegram**: https://t.me/voicechanger_official
- **GitHub Issues**: https://github.com/yourusername/voice-changer/issues

## 开发者

本项目由开源社区维护，感谢所有贡献者：

- [@你的名字](https://github.com/yourusername) - 创始人
- [@贡献者 1](https://github.com/contributor1) - RVC 优化
- [@贡献者 2](https://github.com/contributor2) - GUI 设计

完整贡献者列表：[Contributors](https://github.com/yourusername/voice-changer/graphs/contributors)

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 致谢

感谢以下开源项目：

- [RVC-Project](https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI) - RVC 核心算法
- [VB-Audio](https://vb-audio.com/) - 虚拟音频驱动
- [PyTorch](https://pytorch.org/) - 深度学习框架
- [Librosa](https://librosa.org/) - 音频处理库

## 更新日志

### v2.1.4 (2026-05-15)

- ✨ 支持 RVC 2.1 模型
- 🚀 优化推理性能，延迟降低 20%
- 🐛 修复 Mac M1/M2 兼容性
- 🎨 改进 UI 界面

### v2.0.0 (2026-01-01)

- ✨ 新增模型融合功能 (Merge Lab)
- ✨ 支持自定义模型训练
- 🚀 全面支持 ONNX Runtime
- 🐛 修复多个已知 Bug

查看更多：[CHANGELOG.md](CHANGELOG.md)

## 相关资源

- [模型训练详细教程](docs/training_guide.md)
- [故障排查指南](docs/troubleshooting.md)
- [最佳实践](docs/best_practices.md)
- [API 文档](docs/api.md)

---

**注意**：本软件仅供娱乐和教育用途，请勿用于非法活动或侵犯他人权益。使用变声功能进行诈骗、骚扰等违法行为将承担法律责任。
