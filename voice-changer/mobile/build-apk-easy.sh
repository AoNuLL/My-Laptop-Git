#!/bin/bash
# Voice Changer APK 一键构建脚本（简化版）

set -e

echo "========================================"
echo "Voice Changer APK 构建脚本"
echo "========================================"
echo ""

# 检查环境
check_env() {
    if ! command -v python3 &> /dev/null; then
        echo "❌ 错误：需要 Python 3"
        exit 1
    fi
    
    echo "✅ Python: $(python3 --version)"
}

# 安装依赖
install_deps() {
    echo ""
    echo "正在安装构建工具..."
    
    # 检测系统
    if [ -f /etc/debian_version ]; then
        sudo apt update
        sudo apt install -y openjdk-17-jdk wget unzip
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install openjdk@17
    fi
    
    pip3 install buildozer cython packaging
    
    echo "✅ 依赖安装完成"
}

# 构建 APK
build_apk() {
    echo ""
    echo "开始构建 APK（这可能需要 20-40 分钟）..."
    
    cd "$(dirname "$0")"
    
    # 运行 buildozer
    buildozer android debug
    
    echo ""
    echo "========================================"
    echo "✅ APK 构建成功!"
    echo "========================================"
    echo ""
    echo "APK 位置："
    ls -lh bin/*.apk
    echo ""
    echo "安装到手机:"
    echo "  adb install bin/VoiceChanger-2.1.4-debug.apk"
}

# 主流程
check_env

if command -v buildozer &> /dev/null; then
    echo "✅ Buildozer 已安装"
    build_apk
else
    install_deps
    build_apk
fi
