# Android 部署指南

本指南介绍如何将 Voice Changer 部署到 Android 手机。

## 方法一：使用 Buildozer（推荐）

### 1. 环境准备

**在 Linux 或 macOS 上**（Windows 需要使用 WSL）：

```bash
# 安装 Python 3.8+
sudo apt-get install -y python3 python3-pip python3-venv

# 安装依赖
sudo apt-get install -y \
    git build-essential libssl-dev zlib1g-dev \
    libbz2-dev libreadline-dev libsqlite3-dev \
    wget llvm libncurses5-dev libncursesw5-dev \
    xz-utils tk-dev libffi-dev liblzma-dev \
    autoconf libtool pkg-config

# 安装 Buildozer
pip3 install buildozer cython
```

### 2. 初始化项目

```bash
cd /workspace/voice-changer/mobile

# 初始化 buildozer（如果还没有 spec 文件）
buildozer init

# 使用我们提供的配置文件
# 已包含 buildozer.spec
```

### 3. 安装 Android SDK/NDK

```bash
# Buildozer 会自动下载，也可以手动安装

# 下载 Android SDK
# https://developer.android.com/studio#command-tools

# 设置环境变量
export ANDROID_HOME=$HOME/android-sdk
export ANDROID_NDK_HOME=$HOME/android-ndk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### 4. 编译 APK

```bash
cd mobile

# 清理旧构建
buildozer android clean

# 构建调试版 APK
buildozer android debug

# 构建发布版（需要签名）
buildozer android release

# 输出位置:
# bin/VoiceChanger-2.1.4-debug.apk
# bin/VoiceChanger-2.1.4-release.apk
```

### 5. 部署到手机

```bash
# 连接手机（启用 USB 调试）
adb devices

# 安装 APK
buildozer android deploy run

# 或手动安装
adb install bin/VoiceChanger-2.1.4-debug.apk
```

---

## 方法二：使用 Termux（在手机上直接开发）

### 1. 安装 Termux

从 F-Droid 下载 Termux:
https://f-droid.org/packages/com.termux/

### 2. 配置 Termux

```bash
# 更新包
pkg update && pkg upgrade

# 安装 Python 和依赖
pkg install python rust clang libjpeg-turbo libpng

# 安装 Termux:API（用于访问麦克风）
pkg install termux-api

# 授予权限
termux-setup-storage
```

### 3. 安装 Voice Changer

```bash
# 克隆项目
git clone https://github.com/yourusername/voice-changer.git
cd voice-changer

# 安装依赖
pip install numpy
pip install kivy

# 运行移动端
python mobile/mobile_app.py
```

### 4. 权限设置

```bash
# 授予录音权限
termux-wake-lock
termux-notification --title "Voice Changer" --content "Running"
```

---

## 方法三：使用 Android Studio

### 1. 创建 Android 项目

1. 打开 Android Studio
2. New Project -> Empty Activity
3. Package name: `org.monkeycode.voicechanger`
4. Language: Kotlin 或 Java
5. Minimum SDK: API 21

### 2. 集成 Python for Android

使用 Chaquopy 插件：

```gradle
// 在 build.gradle (Project) 中添加
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.0.0'
        classpath "com.chaquo.python:gradle:14.0.0"
    }
}

// 在 build.gradle (App) 中添加
plugins {
    id 'com.android.application'
    id 'com.chaquo.python'
}

android {
    ...
    defaultConfig {
        ...
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a'
        }
        python {
            version "3.8"
            pip {
                install "numpy"
                install "kivy"
            }
        }
    }
}
```

### 3. 复制核心代码

将 `core/` 和 `mobile/` 目录复制到 `app/src/main/python/`

### 4. 构建 APK

Build -> Build Bundle(s) / APK(s) -> Build APK(s)

---

## 移动端优化建议

### 性能优化

```python
# 1. 降低采样率（移动端）
sample_rate = 22050  # 而不是 48000

# 2. 增大缓冲区
chunk_size = 2048  # 减少 CPU 占用

# 3. 使用模型量化
torch.quantization.quantize_dynamic(model, {torch.nn.Linear}, dtype=torch.qint8)

# 4. 使用半精度推理
model.half()  # FP16
```

### 内存优化

```python
# 1. 只加载必要模型
# 2. 及时释放资源
import gc
del large_object
gc.collect()

# 3. 使用内存映射
import mmap
```

### 电池优化

```python
# 1. 使用 WakeLock 保持后台运行
from android wakeref import wakelock

# 2. 空闲时暂停处理
if not is_active:
    processor.pause()

# 3. 降低后台频率
```

---

## 常见问题

### Q1: 构建时出现"Cython build failed"

**解决方案**:
```bash
# 清除缓存
buildozer android clean
rm -rf .buildozer

# 重新构建
buildozer android debug
```

### Q2: 录音权限被拒绝

**解决方案**:
1. 在 Android 设置中手动授予麦克风权限
2. 确保 AndroidManifest.xml 包含 `RECORD_AUDIO` 权限
3. 使用 Android 6.0+ 的动态权限请求

### Q3: APK 体积过大 (>100MB)

**解决方案**:
```bash
# 1. 使用 APK 分裂（按 CPU 架构）
android.split_apks = true

# 2. 使用 App Bundle
buildozer android release appbundle

# 3. 压缩模型文件
python tools/optimize_models.py --quantize
```

### Q4: 在手机上运行卡顿

**解决方案**:
1. 降低采样率到 22050 Hz
2. 增大缓冲区到 2048 或 4096
3. 使用量化模型
4. 关闭后台其他应用
5. 连接充电器（某些手机会降频）

### Q5: 模型文件太大无法下载

**解决方案**:
```bash
# 在应用中实现按需下载
def download_model_on_demand(model_name):
    url = f"https://your-cdn.com/models/{model_name}_mobile.pth"
    response = requests.get(url, stream=True)
    total_downloaded = 0
    
    with open(f"models/{model_name}.pth", 'wb') as f:
        for chunk in response.iter_content(chunk_size=8192):
            if chunk:
                f.write(chunk)
                total_downloaded += len(chunk)
                # 更新进度条
```

---

## 发布到应用商店

### Google Play

1. **创建开发者账号** ($25 一次性)
2. **准备素材**:
   - 应用图标 (512x512)
   - 功能图形 (1024x500)
   - 截图 (手机、平板)
3. **填写应用信息**:
   - 标题、描述、分类
   - 隐私政策
4. **上传 AAB**:
   ```bash
   buildozer android release appbundle
   ```
5. **发布**

### 国内应用商店

- 小米应用商店
- 华为应用市场
- OPPO 软件商店
- vivo 应用商店
- 应用宝

每个商店都有各自的开发者平台，需要分别注册。

---

## 隐私政策模板

```
Voice Changer 隐私政策

1. 数据收集
本应用不会收集或传输任何用户数据到服务器。
所有音频处理均在本地设备完成。

2. 权限使用
- 麦克风权限：用于录制用户声音进行变声
- 存储权限：用于保存模型文件和录音

3. 第三方服务
本应用不包含任何第三方 SDK 或分析工具。

4. 数据存储
用户录音和模型文件存储在本地设备，用户可随时删除。

5. 儿童隐私
本应用不面向 13 岁以下儿童设计。

6. 政策更新
我们保留更新本隐私政策的权利。
```

---

## 下一步

1. ✅ 测试应用基本功能
2. ✅ 优化性能和内存占用
3. ✅ 设计应用图标和 UI
4. ✅ 编写隐私政策
5. ✅ 准备应用商店素材
6. ✅ 提交审核

祝你发布成功！🎉

---

**文档版本**: 1.0  
**最后更新**: 2026-05-15
