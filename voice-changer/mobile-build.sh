#!/bin/bash
# 移动端快速构建脚本

set -e

echo "========================================="
echo "Voice Changer - 移动端构建"
echo "========================================="
echo ""

cd "$(dirname "$0")/mobile"

# 检查环境
echo "[1/5] 检查环境..."
if ! command -v python3 &> /dev/null; then
    echo "错误：需要安装 Python 3"
    exit 1
fi

echo "✓ Python: $(python3 --version)"

# 安装 Buildozer
echo ""
echo "[2/5] 安装 Buildozer..."
pip3 install -q buildozer cython
echo "✓ Buildozer 已安装"

# 初始化
echo ""
echo "[3/5] 初始化构建环境..."
if [ ! -f "buildozer.spec" ]; then
    buildozer init
fi
echo "✓ 初始化完成"

# 依赖
echo ""
echo "[4/5] 下载依赖..."
if [ ! -d ".buildozer" ]; then
    buildozer requirements
fi
echo "✓ 依赖已下载"

# 选择构建类型
echo ""
echo "[5/5] 选择构建类型:"
echo "1. 调试版 (Debug APK)"
echo "2. 发布版 (Release APK)"
echo "3. 清理并重新构建"
echo ""
read -p "请选择 [1-3]: " choice

case $choice in
    1)
        echo ""
        echo "构建调试版..."
        buildozer android debug
        echo ""
        echo "✓ 构建完成"
        echo "APK 位置：bin/VoiceChanger-*-debug.apk"
        ;;
    2)
        echo ""
        echo "构建发布版（需要签名）..."
        buildozer android release
        echo ""
        echo "✓ 构建完成"
        echo "APK 位置：bin/VoiceChanger-*-release.apk"
        ;;
    3)
        echo ""
        echo "清理并重新构建..."
        buildozer android clean
        buildozer android debug
        echo ""
        echo "✓ 构建完成"
        ;;
    *)
        echo "无效选择"
        exit 1
        ;;
esac

echo ""
echo "========================================="
echo "部署到手机:"
echo ""
echo "1. 连接手机（启用 USB 调试）"
echo "2. 运行：buildozer android deploy run"
echo "3. 或手动：adb install bin/VoiceChanger-*.apk"
echo "========================================="
