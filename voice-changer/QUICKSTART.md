# 快速开始指南

## 5 分钟快速上手

### 1. 安装依赖 (Windows)

双击运行 `go-realtime-gui.bat`，或手动执行：

```bash
cd voice-changer
pip install -r requirements.txt
```

### 2. 下载模型

**方式 1: 使用下载工具**
```bash
python tools/download_models.py --all
```

**方式 2: 手动下载**
从以下地址下载模型包：
- 夸克网盘：https://pan.quark.cn/s/df5642c6567b
- 解压后放入 `models/` 目录

### 3. 安装虚拟声卡

**Windows 用户**:
1. 运行程序
2. 点击"安装虚拟声卡"按钮
3. 下载安装 VB-Audio Virtual Cable
4. 重启电脑

**macOS 用户**:
1. 下载 BlackHole: https://existential.audio/blackhole/
2. 安装后重启

### 4. 运行变声器

```bash
python main.py
```

或双击 `go-realtime-gui.bat`

### 5. 开始变声

1. 左侧选择一个音色模型
2. 点击"开始变声"按钮
3. 在语音软件中选择"VB-Audio Virtual Cable"作为麦克风

## 常用场景

### Discord/YY 语音

1. Discord 设置 -> 语音和视频
2. 输入设备选择：VB-Audio Virtual Cable
3. 变声器正常运行

### 游戏开黑

1. 游戏音频设置
2. 麦克风输入选择虚拟声卡

### 直播推流

1. OBS 设置 -> 音频
2. 添加音频输入捕捉
3. 选择虚拟声卡作为音源

## 基础调节

### 男变女
- 选择女声模型
- 音调：+8 到 +12
- 音量：80-100%

### 女变男
- 选择男声模型
- 音调：-8 到 -12
- 音量：80-100%

### 降低延迟
- 设置 -> 缓冲区大小 -> 256 或 512
- 使用 GPU 加速
- 关闭后台应用

## 遇到问题？

查看详细文档：
- [故障排查](docs/troubleshooting.md)
- [模型训练](docs/training_guide.md)

或访问：
- GitHub Issues: https://github.com/yourusername/voice-changer/issues
- QQ 群：123456789

---

**提示**: 首次使用请确保下载至少一个模型文件！
