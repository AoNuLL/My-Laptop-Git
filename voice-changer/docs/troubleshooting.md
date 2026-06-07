# 故障排查指南

## 安装问题

### 1. Python 版本错误

**错误信息**: `SyntaxError` 或 `ImportError`

**解决方案**:

```bash
python --version
# 必须是 3.8 或更高版本

# 如版本过低，请升级
# Windows: 从 python.org 下载安装
# macOS: brew install python@3.8
# Linux: sudo apt install python3.8
```

### 2. 依赖安装失败

**错误信息**: `Could not build wheels for pyaudio`

**解决方案**:

**Windows**:
```bash
pip install pipwin
pipwin install pyaudio
pip install -r requirements.txt
```

**macOS**:
```bash
brew install portaudio
pip install -r requirements.txt
```

**Linux**:
```bash
sudo apt-get update
sudo apt-get install -y portaudio19-dev python3-pyaudio
pip install -r requirements.txt
```

### 3. PyTorch CUDA 安装失败

**错误信息**: `CUDA not available`

**解决方案**:

1. 检查 NVIDIA 驱动:
```bash
nvidia-smi
```

2. 安装匹配的 CUDA 版本:
```bash
# CUDA 11.8
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118

# CUDA 12.1
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121

# CPU 版本 (无 GPU)
pip install torch torchvision torchaudio
```

## 运行问题

### 4. 程序启动失败

**错误信息**: `ModuleNotFoundError: No module named 'customtkinter'`

**解决方案**:
```bash
pip install -r requirements.txt --upgrade
```

### 5. 无音频设备

**错误信息**: `Error opening InputStream` 或 `No audio input devices found`

**解决方案**:

**检查设备连接**:
- Windows: 右键声控图标 -> 声音设置 -> 确认麦克风已连接
- macOS: 系统偏好设置 -> 声音 -> 输入
- Linux: `arecord -l`

**选择其他设备**:
在程序 UI 中点击"选择音频设备"，尝试其他输入设备

### 6. 虚拟声卡未识别

**错误信息**: 无法选择虚拟声卡作为输出设备

**解决方案**:

**Windows**:
1. 打开"声音设置" -> "声音控制面板"
2. 确认"VB-Audio Virtual Cable"已出现在播放设备
3. 如未出现，重新安装：
   - 以管理员身份运行 VB-Audio 安装程序
   - 右键安装程序 -> 属性 -> 兼容性 -> 以管理员身份运行

**macOS**:
1. 打开"音频 MIDI 设置"
2. 确认 BlackHole 已列在音频设备
3. 创建多输出设备：
   - 点击 "+" -> "创建多输出设备"
   - 勾选 BlackHole 和扬声器

**Linux**:
```bash
# 手动创建 PulseAudio 虚拟设备
pactl load-module module-null-sink sink_name=vc_output

# 永久生效：添加到 ~/.pulse/default.pa
```

## 变声质量问题

### 7. 延迟过高

**现象**: 说话后很久才能听到变声

**解决方案**:

1. **减小缓冲区大小**:
   - 设置 -> 缓冲区大小 -> 调整为 256 或 512

2. **关闭后台应用**:
   释放 CPU 和内存资源

3. **使用 GPU 加速**:
   - 确认 CUDA 已正确安装
   - 检查 GPU 使用率

4. **使用 ONNX 模型**:
   ```bash
   python tools/export_model.py --to-onnx model.pth
   ```

### 8. 声音机械/电音重

**现象**: 变声后像机器人声音

**解决方案**:

1. **调整音调**:
   - 男变女：尝试 +8 到 +12
   - 女变男：尝试 -8 到 -12

2. **更换模型**:
   某些模型质量较差，尝试其他模型

3. **改进录音质量**:
   - 使用更好的麦克风
   - 降低环境噪音
   - 调整麦克风距离

4. **使用更好的音高提取方法**:
   ```python
   rvc_engine.set_f0_method("harvest")  # 或 "crepe"
   ```

### 9. 破音/爆音

**现象**: 偶尔出现爆音或破音

**解决方案**:

1. **降低输入音量**:
   - 系统麦克风音量调至 80%
   - 程序中的"输出音量"调至 80-90%

2. **启用限幅器**:
   在设置中启用音频限幅功能

3. **更换模型**:
   某些模型可能导致输出过大

## 模型问题

### 10. 模型下载失败

**错误信息**: `ConnectionError` 或 `404 Not Found`

**解决方案**:

1. **使用国内镜像**:
   从夸克网盘下载：
   https://pan.quark.cn/s/df5642c6567b

2. **检查网络**:
   ```bash
   ping huggingface.co
   ```

3. **手动下载**:
   下载后放入 `models/` 目录

### 11. 模型加载失败

**错误信息**: `RuntimeError: Error(s) in loading state_dict`

**解决方案**:

1. **检查模型文件完整性**:
   ```bash
   # 重新下载模型
   python tools/download_models.py -m female/yujie
   ```

2. **模型格式不兼容**:
   确认模型是 RVC 格式（.pth 或 .onnx）

3. **版本不匹配**:
   更新 RVC 库到最新版本

### 12. 模型效果差

**现象**: 与预期音色不符

**解决方案**:

1. **调整音调**:
   使用音调滑块微调整体音高

2. **使用合适的模型**:
   - 男变女：选择女声模型
   - 女变男：选择男声模型
   - 跨度过大可能效果不佳

3. **自定义训练**:
   参考 docs/training_guide.md 训练自己的模型

## 性能问题

### 13. CPU/GPU 占用率高

**现象**: 程序运行时系统卡顿

**解决方案**:

1. **降低采样率**:
   - 设置 -> 采样率 -> 22050 或 44100

2. **减少后台进程**:
   关闭不必要的应用

3. **使用 ONNX Runtime**:
   ```bash
   pip install onnxruntime-gpu
   ```

### 14. 内存泄漏

**现象**: 运行时间越长，内存占用越高

**解决方案**:

1. **更新程序**:
   使用最新版本

2. **定期重启**:
   每运行 1-2 小时重启程序

3. **检查内存泄漏**:
   ```bash
   # Linux
   watch -n 1 free -m
   
   # Windows
   任务管理器 -> 性能 -> 内存
   ```

## Discord/语音软件测试

### 15. Discord 无法听到变声

**解决方案**:

1. **Discord 设置**:
   - 用户设置 -> 语音和视频
   - 输入设备：选择 "VB-Audio Virtual Cable"

2. **检查输出**:
   - 变声器输出设备：VB-Audio Virtual Cable
   - 监听设备：你的耳机

3. **权限检查**:
   确保 Discord 有麦克风权限

### 16. 游戏语音无变声

**解决方案**:

1. **游戏音频设置**:
   将游戏麦克风设置为虚拟声卡

2. **立体声混音**:
   Windows 用户可能需要启用"立体声混音"

3. **使用 Voicemeeter**:
   更高级的音频路由工具

## 其他问题

### 17. GUI 界面闪退

**解决方案**:

```bash
# 清除 GUI 缓存
rm -rf ~/.config/Tk
rm -rf ~/.tk

# 重新安装 customtkinter
pip uninstall customtkinter
pip install customtkinter --no-cache-dir
```

### 18. 日志收集

如需提交 Issue，请提供以下信息：

```bash
# 系统信息
uname -a  # Linux/macOS
ver       # Windows

# Python 版本
python --version

# 依赖版本
pip freeze > requirements_frozen.txt

# 程序日志
# 运行后查看控制台输出
```

## 获取帮助

如果以上方法都无法解决问题：

1. **查看 GitHub Issues**:
   https://github.com/yourusername/voice-changer/issues

2. **提交新 Issue**:
   提供详细的错误信息和复现步骤

3. **社区支持**:
   - QQ 群：123456789
   - Discord: https://discord.gg/voicechanger

---

**提示**: 多数问题可以通过重启程序、重新安装依赖或更换模型解决。
