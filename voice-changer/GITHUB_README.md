# Voice Changer - GitHub 仓库

## 📦 项目信息

- **名称**: Voice Changer (变声器)
- **版本**: v2.1.4
- **许可证**: MIT
- **语言**: Python
- **框架**: PyTorch, Kivy

## 🌟 功能特性

- ✨ 实时变声，低延迟 <60ms
- 🎤 支持 600+ RVC 模型
- 🖥️ 跨平台：Windows/macOS/Linux/Android
- 🆓 完全免费开源
- 🔒 本地处理，保护隐私

## 🚀 快速开始

### 桌面版

```bash
# 克隆项目
git clone https://github.com/yourusername/My-Laptop-Git.git
cd My-Laptop-Git/voice-changer

# 安装依赖
pip install -r requirements.txt

# 下载模型
python tools/download_models.py -a

# 运行程序
python main.py
```

### Android 版

```bash
# 构建 APK
cd mobile
buildozer android debug

# 安装到手机
adb install bin/VoiceChanger-2.1.4-debug.apk
```

## 📚 文档

- [快速上手](QUICKSTART.md)
- [功能列表](FEATURES.md)
- [模型下载指南](docs/model_download_guide.md)
- [模型训练教程](docs/model_training_tutorial.md)
- [故障排查](docs/troubleshooting.md)

## 🛠️ 技术栈

- **核心**: Python 3.8+, PyTorch 2.0+
- **GUI**: CustomTkinter (桌面), Kivy (移动)
- **音频**: PyAudio, Librosa, SoundFile
- **AI**: RVC (Retrieval-based Voice Conversion)

## 📊 项目统计

- **代码**: 6,500+ 行
- **文件**: 46 个
- **文档**: 11 份
- **工具**: 14 个

## 🤝 贡献

欢迎提交 Issue 和 Pull Request!

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

感谢以下开源项目:

- [RVC-Project](https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI)
- [PyTorch](https://pytorch.org/)
- [CustomTkinter](https://github.com/TomSchimansky/CustomTkinter)
- [Kivy](https://kivy.org/)

## 📞 联系方式

- **Issue**: https://github.com/yourusername/My-Laptop-Git/issues
- **Email**: your_email@example.com

---

**Star** ⭐ 这个项目如果你也喜欢它！
