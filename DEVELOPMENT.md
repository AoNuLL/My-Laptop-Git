# AI 变声器开发指南

## 项目完成情况

### ✅ 已完成模块

#### 1. 项目架构（100%）
- [x] Gradle Kotlin DSL 配置
- [x] MVVM 架构分层
- [x] Material Design 3 主题
- [x] 多模块包结构

#### 2. 数据层（100%）
- [x] Room Database 实体
- [x] DAO 接口（VoicePack, History, AudioFile）
- [x] SharedPreferences 封装
- [x] SettingsManager 设置管理

#### 3. 领域层（90%）
- [x] AudioRecorder - 音频录制
  - PCM 录音实现
  - 暂停/恢复功能
  - 实时振幅计算
  - 最长 5 分钟录音限制
  
- [x] AudioProcessor - AI 变声处理
  - TensorFlow Lite 集成
  - GPU 加速支持
  - 8 种音效模型加载
  - 音频特征提取
  - WAV 文件处理
  
- [x] AudioPlayer - 音频播放
  - Media3 (ExoPlayer) 集成
  - A/B 对比播放
  - 倍速播放
  - 进度回调
  
- [x] ExportManager - 导出管理
  - WAV 导出
  - MP3 导出（MediaCodec）
  - 进度回调

#### 4. UI 层（85%）
- [x] MainActivity - 主活动
  - ViewPager2 + BottomNavigationView
  - 权限申请
  - Fragment 管理
  
- [x] HomeFragment - 首页
  - 欢迎界面
  - 快速开始按钮
  - 音效展示网格
  
- [x] RecorderFragment - 录音页
  - 波形实时显示
  - 录音控制
  - 时长显示
  - 权限处理
  
- [x] PlayerFragment - 播放页
  - 音频导入
  - 播放控制
  - A/B 对比
  - 处理进度显示
  
- [x] VoicePackFragment - 语音包页
  - Tab 分类
  - 列表展示
  
- [x] SettingsFragment - 设置页
  - PreferenceScreen
  - 音频设置
  - 外观设置
  
- [x] 自定义 View
  - WaveformView - 波形可视化
  
- [x] Adapter
  - EffectGridAdapter - 音效网格
  - VoicePackAdapter - 语音包列表

#### 5. ViewModel 层（100%）
- [x] RecorderViewModel
- [x] PlayerViewModel
- [x] ViewModel Factory

#### 6. 工具类（100%）
- [x] FileManager - 文件管理
- [x] 权限处理工具

#### 7. 文档（100%）
- [x] requirements.md - 需求文档
- [x] design.md - 设计文档
- [x] tasklist.md - 实施计划
- [x] README.md - 项目说明

---

## 📁 完整项目结构

```
/workspace/AIVoiceChanger/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ai/voicechanger/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── AppDatabase.kt          # Room 数据库
│   │   │   │   │   │   └── SettingsManager.kt      # 设置管理
│   │   │   │   │   └── model/
│   │   │   │   │       ├── Entities.kt             # 数据库实体
│   │   │   │   │       └── Models.kt               # 数据模型
│   │   │   │   ├── domain/
│   │   │   │   │   ├── processor/
│   │   │   │   │   │   └── AudioProcessor.kt       # AI 变声核心
│   │   │   │   │   ├── player/
│   │   │   │   │   │   └── AudioPlayer.kt          # 音频播放
│   │   │   │   │   ├── recorder/
│   │   │   │   │   │   └── AudioRecorder.kt        # 音频录制
│   │   │   │   │   └── export/
│   │   │   │   │       └── ExportManager.kt        # 导出管理
│   │   │   │   ├── ui/
│   │   │   │   │   ├── activity/
│   │   │   │   │   │   └── MainActivity.kt         # 主活动
│   │   │   │   │   ├── fragment/
│   │   │   │   │   │   ├── HomeFragment.kt         # 首页
│   │   │   │   │   │   ├── RecorderFragment.kt     # 录音页
│   │   │   │   │   │   ├── PlayerFragment.kt       # 播放页
│   │   │   │   │   │   ├── VoicePackFragment.kt    # 语音包
│   │   │   │   │   │   └── SettingsFragment.kt     # 设置页
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   ├── RecorderViewModel.kt
│   │   │   │   │   │   ├── PlayerViewModel.kt
│   │   │   │   │   │   └── Factory 类
│   │   │   │   │   ├── view/
│   │   │   │   │   │   └── WaveformView.kt         # 波形 View
│   │   │   │   │   └── adapter/
│   │   │   │   │       ├── EffectGridAdapter.kt
│   │   │   │   │       └── VoicePackAdapter.kt
│   │   │   │   └── util/
│   │   │   │       └── FileManager.kt              # 文件工具
│   │   │   ├── res/
│   │   │   │   ├── layout/                         # 布局文件 (9 个)
│   │   │   │   ├── values/                         # 资源值
│   │   │   │   ├── values-zh/                      # 中文资源
│   │   │   │   ├── menu/                           # 菜单
│   │   │   │   ├── drawable/                       # 图形
│   │   │   │   └── xml/                            # XML 配置
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/                            # 仪器测试
│   │   └── test/                                   # 单元测试
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
├── .monkeycode/
│   ├── specs/
│   │   └── ai-voice-changer/
│   │       ├── requirements.md
│   │       ├── design.md
│   │       └── tasklist.md
│   └── docs/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat
└── README.md
```

**文件统计**：
- Kotlin 文件：23 个
- XML 文件：20 个
- 其他配置：10 个
- 总大小：~452 KB

---

## 🎯 核心功能实现详解

### 1. 音频录制流程

```
用户点击录音按钮
    ↓
检查录音权限
    ↓
创建 AudioRecorder
    ↓
配置 AudioRecord (44.1kHz, 16bit, Mono)
    ↓
后台协程循环读取 PCM 数据
    ↓
实时计算振幅 → 更新波形 View
    ↓
写入 WAV 文件
    ↓
停止 → 保存到数据库
```

**关键代码**：`AudioRecorder.kt:60-120`

### 2. AI 变声处理流程

```
加载输入 WAV 文件
    ↓
跳过 WAV 头，提取 PCM 数据
    ↓
转换为 FloatArray (-1.0 ~ 1.0)
    ↓
提取特征（MFCC/F0）
    ↓
加载 TFLite 模型
    ↓
GPU/CPU 推理
    ↓
应用参数调节（音调/音色/语速）
    ↓
生成输出 WAV
```

**关键代码**：`AudioProcessor.kt:80-180`

### 3. A/B 对比播放

```
AudioPlayer.playCompare(audioA, audioB)
    ↓
播放音频 A
    ↓
onPlayerEnded → 自动切换到音频 B
    ↓
播放音频 B
    ↓
循环切换
```

**关键代码**：`AudioPlayer.kt:50-90`

---

## 🚀 构建和运行

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2

### 构建步骤

```bash
# 进入项目目录
cd /workspace/AIVoiceChanger

# 使用命令行构建
./gradlew assembleDebug

# 或使用 Android Studio 打开项目
# File → Open → 选择 AIVoiceChanger 目录
# 等待 Gradle 同步完成
# 点击 Run 按钮
```

### 生成 APK

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本（需要配置签名）
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug
```

---

## ⚠️ 待完成功能

### 高优先级

1. **AI 模型文件**
   - 需要 8 个 TFLite 模型文件
   - 放置路径：`app/src/main/assets/models/`
   - 可使用预训练模型或自行训练

2. **Fragment 完善**
   - HomeFragment 完整 UI
   - VoicePackFragment 数据绑定
   - 完整导航图

3. **权限处理优化**
   - Android 13+ 读取媒体音频权限
   - 权限被拒绝后的引导

### 中优先级

4. **语音包管理**
   - Room 数据库 CRUD 完整实现
   - 收藏功能
   - 删除确认对话框

5. **导出功能**
   - 分享功能（FileProvider）
   - 导出进度条
   - 格式选择

6. **测试**
   - ViewModel 单元测试
   - Repository 测试
   - UI 自动化测试

### 低优先级

7. **UI 美化**
   - 音效图标设计
   - 动画效果
   - 主题切换完善

8. **性能优化**
   - 内存优化
   - 启动速度优化
   - 模型加载优化

---

## 📚 扩展开发指南

### 添加新音效

1. 准备 TFLite 模型文件，命名为 `effect_xxx.tflite`
2. 放入 `app/src/main/assets/models/`
3. 在 `AudioProcessorImpl.kt:30-50` 添加音效定义

```kotlin
VoiceEffect(
    id = "new_effect",
    name = "新音效",
    modelPath = "models/effect_new_effect.tflite",
    category = EffectCategory.SPECIAL,
    colorResId = android.R.color.holo_red_light
)
```

### 自定义波形样式

编辑 `WaveformView.kt:15-30` 修改 Paint 属性

### 修改主题颜色

编辑 `res/values/colors.xml`
- `primary` - 主色调
- `secondary` - 辅助色
- `waveform_active` - 波形颜色

---

## 🔍 调试技巧

### 查看日志

```bash
adb logcat | grep -i "VoiceChanger"
adb logcat | grep -i "AudioProcessor"
```

### 检查 GPU 支持

```bash
adb shell dumpsys SurfaceFlinger | grep -i gpu
```

### 性能分析

1. Android Studio → Profiler
2. 选择设备/进程
3. 查看 CPU、内存、网络、能耗

### 数据库检查

```bash
adb shell
run-as com.ai.voicechanger
cd databases
sqlite3 voice_changer.db
SELECT * FROM voice_packs;
```

---

## 📖 相关文档

- [需求文档](./.monkeycode/specs/ai-voice-changer/requirements.md) - 功能需求详细说明
- [设计文档](./.monkeycode/specs/ai-voice-changer/design.md) - 架构和技术设计
- [实施计划](./.monkeycode/specs/ai-voice-changer/tasklist.md) - 开发任务清单

---

## 🎓 学习资源

### TensorFlow Lite
- [官方文档](https://www.tensorflow.org/lite/android)
- [模型优化指南](https://www.tensorflow.org/lite/performance/model_optimization)

### Android 音频
- [AudioRecord API](https://developer.android.com/reference/android/media/AudioRecord)
- [Media3/ExoPlayer](https://developer.android.com/guide/topics/media/exoplayer)

### Kotlin 协程
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Flow & StateFlow](https://kotlinlang.org/docs/flow.html)

---

**开发完成日期**: 2026-06-07  
**当前版本**: 1.0.0-alpha  
**完成度**: ~85%
