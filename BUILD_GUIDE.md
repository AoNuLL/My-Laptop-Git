# AI 变声器 - 构建指南

## 📋 环境要求

### 必需软件

1. **JDK 17 或更高版本**
   - Ubuntu/Debian: `sudo apt install openjdk-17-jdk`
   - macOS (Homebrew): `brew install openjdk@17`
   - Windows: 从 [Oracle](https://www.oracle.com/java/technologies/downloads/) 下载

2. **Android SDK**
   - 需要 Android SDK Platform 34
   - 需要 Android SDK Build-Tools 34
   - 需要 Android SDK Command-line Tools

3. **环境变量**
   ```bash
   export ANDROID_HOME=/path/to/android-sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

### 推荐 IDE

- **Android Studio Hedgehog (2023.1.1)** 或更高版本
- 下载地址：https://developer.android.com/studio

---

## 🚀 快速开始

### 方法一：使用 Android Studio（推荐）

1. **打开项目**
   - 启动 Android Studio
   - 选择 `File` → `Open`
   - 选择 `/workspace/AIVoiceChanger` 目录

2. **等待 Gradle 同步**
   - 首次打开会自动下载依赖
   - 可能需要几分钟时间

3. **构建 APK**
   - 点击菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - 等待构建完成

4. **查看 APK**
   - 构建完成后点击 `locate` 按钮
   - APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方法二：使用命令行

#### Linux/macOS

```bash
cd /workspace/AIVoiceChanger

# 赋予执行权限
chmod +x gradlew

# 清理并构建
./gradlew clean assembleDebug

# 或使用一键构建脚本
chmod +x build.sh
./build.sh
```

#### Windows

```cmd
cd \workspace\AIVoiceChanger

# 清理并构建
gradlew.bat clean assembleDebug
```

---

## 📦 构建产物

### Debug 版本

- **位置**: `app/build/outputs/apk/debug/app-debug.apk`
- **特点**: 
  - 包含调试信息
  - 未签名（使用 debug 密钥自动签名）
  - 适合开发和测试

### Release 版本（需要配置签名）

```bash
./gradlew assembleRelease
```

- **位置**: `app/build/outputs/apk/release/app-release.apk`
- **特点**:
  - 已优化（如果启用 ProGuard）
  - 需要手动签名
  - 适合发布

---

## 🔧 常见问题

### 1. Java 找不到

**错误**: `JAVA_HOME is not set`

**解决**:
```bash
# Linux/macOS
export JAVA_HOME=$(which java)
export PATH=$JAVA_HOME/bin:$PATH

# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

### 2. Gradle 同步失败

**错误**: `Could not resolve all dependencies`

**解决**:
```bash
# 清理 Gradle 缓存
./gradlew clean --refresh-dependencies

# 删除 .gradle 目录
rm -rf .gradle
./gradlew
```

### 3. SDK 未找到

**错误**: `SDK location not found`

**解决**:
创建 `local.properties` 文件:
```properties
sdk.dir=/path/to/android-sdk
```

或在 Android Studio 中:
- `File` → `Project Structure` → `SDK Location`
- 设置 Android SDK 路径

### 4. 内存不足

**错误**: `OutOfMemoryError`

**解决**:
编辑 `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
```

---

## 📱 安装到设备

### 使用 ADB

```bash
# 连接设备
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 如果已安装，覆盖安装
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 从 Android Studio

1. 点击工具栏的 `Run` 按钮
2. 选择连接的设备和
3. 应用会自动安装并启动

---

## ⚙️ 构建配置

### 修改应用 ID

编辑 `app/build.gradle.kts`:
```kotlin
defaultConfig {
    applicationId = "com.yourcompany.voicechanger"
}
```

### 修改版本号

```kotlin
defaultConfig {
    versionCode = 2
    versionName = "1.0.1"
}
```

### 启用 ProGuard 混淆

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

---

## 🎯 构建性能优化

### 使用 Gradle Daemon

```bash
# 启用守护进程（默认已启用）
org.gradle.daemon=true
```

### 并行构建

`gradle.properties`:
```properties
org.gradle.parallel=true
org.gradle.caching=true
```

### 配置国内镜像（中国用户）

`settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}
```

---

## 📊 构建时间分析

```bash
# 查看构建时间详情
./gradlew assembleDebug --profile

# 打开 build/reports/profile/profile-*.html 查看分析
```

---

## 🔐 签名配置（发布用）

### 1. 生成签名密钥

```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
```

### 2. 配置签名

`app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("/path/to/my-release-key.jks")
            storePassword = "your-keystore-password"
            keyAlias = "my-alias"
            keyPassword = "your-key-password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 3. 构建 Release APK

```bash
./gradlew assembleRelease
```

---

## 📈 APK 大小优化

### 1. 启用资源压缩

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
    }
}
```

### 2. 按 ABI 分包

```kotlin
splits {
    abi {
        isEnable = true
        reset()
        include("armeabi-v7a", "arm64-v8a")
        isUniversalApk = false
    }
}
```

---

## 📝 相关文档

- [README.md](./README.md) - 项目介绍
- [DEVELOPMENT.md](./DEVELOPMENT.md) - 开发指南
- [specs/ai-voice-changer/requirements.md](../.monkeycode/specs/ai-voice-changer/requirements.md) - 需求文档
- [specs/ai-voice-changer/design.md](../.monkeycode/specs/ai-voice-changer/design.md) - 设计文档

---

**最后更新**: 2026-06-07  
**文档版本**: 1.0.0
