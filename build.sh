#!/bin/bash

# AI Voice Changer - APK 构建脚本
# 使用前请确保已安装 JDK 17+ 和 Android SDK

set -e

echo "========================================"
echo "AI 变声器 - APK 构建脚本"
echo "========================================"
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未找到 Java"
    echo "请安装 JDK 17 或更高版本"
    echo ""
    echo "Ubuntu/Debian:"
    echo "  sudo apt install openjdk-17-jdk"
    echo ""
    echo "macOS (Homebrew):"
    echo "  brew install openjdk@17"
    echo ""
    exit 1
fi

echo "✅ Java 版本:"
java -version
echo ""

# 检查 ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️  警告：ANDROID_HOME 未设置"
    echo "请设置 Android SDK 路径:"
    echo "  export ANDROID_HOME=/path/to/android-sdk"
    echo ""
fi

# 检查 gradlew
if [ ! -f "./gradlew" ]; then
    echo "❌ 错误：未找到 gradlew"
    echo "请确保在项目根目录运行此脚本"
    exit 1
fi

# 清理并构建
echo "========================================"
echo "开始构建..."
echo "========================================"
echo ""

./gradlew clean

echo ""
echo "编译 Debug 版本..."
./gradlew assembleDebug --no-daemon

# 检查构建结果
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo ""
    echo "========================================"
    echo "✅ 构建成功!"
    echo "========================================"
    echo ""
    echo "APK 位置：app/build/outputs/apk/debug/app-debug.apk"
    echo "APK 大小：$(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)"
    echo ""
else
    echo ""
    echo "========================================"
    echo "❌ 构建失败"
    echo "========================================"
    echo ""
    echo "请查看上方的错误信息"
    exit 1
fi
