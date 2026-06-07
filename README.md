# AI Voice Changer

[![Platform](https://img.shields.io/badge/platform-Android%207.0+-green)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple)](https://kotlinlang.org)
[![TensorFlow Lite](https://img.shields.io/badge/TensorFlow%20Lite-2.14.0-orange)](https://www.tensorflow.org/lite)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue)](https://developer.android.com/topic/architecture)

基于深度学习的 Android 智能变声应用，支持 8 种变声效果，实时高质量语音转换。

## 📱 功能特性

### 核心功能
- **高质量 AI 变声**：基于 TensorFlow Lite 的深度神经网络推理，声音自然不失真
- **实时录音变声**：支持录制音频后实时处理变声
- **8 种预设音效**：
  - 🚹 → 🚺 男变女
  - 🚺 → 🚹 女变男
  - 👶 儿童音
  - 👴 老人音
  - 🤖 机器人音
  - 🎭 卡通音
  - 👹 怪兽音
  - 📻 电台主播音
- **A/B 对比播放**：一键对比原始音频与变声效果
- **参数微调**：支持音调、音色、语速三维度调节
- **语音包管理**：保存、收藏、分享变声作品
- **多格式导出**：支持 MP3、WAV 格式导出

### 技术亮点
- **完全离线**：AI 模型内置，无需联网
- **GPU 加速**：支持 GPU 推理加速（可选）
- **低延迟**：5 分钟音频 < 10 秒处理时间
- **波形可视化**：实时显示音频波形和播放进度

## 🏗️ 技术架构

```
┌─────────────────────────────────────────┐
│          UI Layer                       │
│  ┌─────────────────────────────────┐   │
│  │ Activities / Fragments          │   │
│  └─────────────────────────────────┘   │
│  ┌─────────────────────────────────┐   │
│  │ Custom Views (WaveformView)     │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│       ViewModel Layer                   │
│  ┌─────────────────────────────────┐   │
│  │ RecorderViewModel               │   │
│  │ PlayerViewModel                 │   │
│  │ VoicePackViewModel              │   │
│  │ SettingsViewModel               │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│        Domain Layer                     │
│  ┌─────────────────────────────────┐   │
│  │ AudioRecorder                   │   │
│  │ AudioProcessor (TFLite)         │   │
│  │ AudioPlayer (ExoPlayer/Media3)  │   │
│  │ VoicePackManager                │   │
│  │ ExportManager                   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         Data Layer                      │
│  ┌─────────────────────────────────┐   │
│  │ Repository Pattern              │   │
│  │ Room Database                   │   │
│  │ SharedPreferences               │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

## 🛠️ 技术栈

- **语言**：Kotlin 1.9+
- **架构**：MVVM + Repository 模式
- **AI 推理**：TensorFlow Lite 2.14
- **音频播放**：Androidx Media3 (ExoPlayer)
- **本地存储**：Room Database
- **异步处理**：Kotlin Coroutines + Flow
- **UI 框架**：Material Design 3
- **日志**：Timber

## 📦 项目结构

```
app/
├── src/main/
│   ├── java/com/ai/voicechanger/
│   │   ├── data/
│   │   │   ├── local/           # Room Database, DAOs
│   │   │   └── model/            # Data models
│   │   ├── domain/
│   │   │   ├── processor/        # AI 变声处理
│   │   │   ├── player/           # 音频播放
│   │   │   └── recorder/         # 音频录制
│   │   ├── ui/
│   │   │   ├── activity/         # Activities
│   │   │   ├── fragment/         # Fragments
│   │   │   ├── viewmodel/        # ViewModels
│   │   │   └── view/             # Custom Views
│   │   ├── util/                 # Utils
│   │   └── AppApplication.kt     # Application class
│   ├── res/
│   │   ├── layout/               # UI layouts
│   │   ├── values/               # Resources
│   │   └── xml/                  # XML configs
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog 或更高版本
- JDK 17+
- Android SDK 34
- 最低支持 Android 7.0 (API 24)

### 构建步骤

1. **克隆项目**
```bash
cd /workspace/AIVoiceChanger
```

2. **同步 Gradle 依赖**
在 Android Studio 中打开项目，自动同步 Gradle

3. **添加 AI 模型**（可选）
将 TFLite 模型文件放入 `app/src/main/assets/models/`：
```
models/
├── effect_male_to_female.tflite
├── effect_female_to_male.tflite
├── effect_child.tflite
├── effect_elderly.tflite
├── effect_robot.tflite
├── effect_cartoon.tflite
├── effect_monster.tflite
└── effect_radio_host.tflite
```

4. **运行应用**


```bash
./gradlew installDebug
```

或使用 Android Studio 的 Run 按钮

## 📖 使用指南

### 1. 录音变声
1. 点击首页"开始录音"或进入录音页面
2. 点击录音按钮开始录制
3. 录制完成选择音效
4. 系统自动处理变声


### 2. A/B 对比
- 在播放页面点击"A/B 对比"按钮
- 原始音频和变声后音频交替播放
- 一键对比效果差异

### 3. 调节参数
- 在播放页面下方找到参数滑块
- 调节音调（-12 ~ +12 半音）
- 调节音色（-1.0 ~ 1.0）
- 调节语速（0.5x ~ 2.0x）

### 4. 导出分享
- 处理完成后点击"导出"
- 选择 MP3 或 WAV 格式
- 保存到本地或分享给朋友

## ⚙️ 高级配置

### GPU 加速
```kotlin
// 在 AudioProcessorImpl 中
private fun initializeGpuDelegate() {
    try {
        gpuDelegate = GpuDelegate()
    } catch (e: Exception) {
        gpuDelegate = null  // 回退到 CPU
    }
}
```

### 模型量化
- 使用 FP16 量化减少模型大小 50%
- 动态范围量化提升 CPU 推理速度

## 📄 开发文档

- [需求文档](./.monkeycode/specs/ai-voice-changer/requirements.md)
- [设计文档](./.monkeycode/specs/ai-voice-changer/design.md)
- [实施计划](./.monkeycode/specs/ai-voice-changer/tasklist.md)

## 🔍 调试技巧

### 查看音频处理日志
```bash
adb logcat | grep -i "AudioProcessor"
```

### 检查 GPU 支持
```bash
adb shell dumpsys SurfaceFlinger | grep -i gpu
```

### 性能分析
- Android Studio Profiler - 内存和 CPU
- Perfetto - 系统级性能追踪

## 📝 许可证

MIT License
 
## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📮 联系方式

- 问题反馈：GitHub Issues
- 功能建议：GitHub Discussions

---

**构建时间**：2026-06-07  
**版本**：1.0.0  
**目标平台**：Android 7.0+
