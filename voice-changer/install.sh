#!/bin/bash
# Voice Changer 安装脚本 (Linux/macOS)
# ========================================

set -e

echo "============================================"
echo "Voice Changer - 安装程序"
echo "============================================"
echo ""

# 检查 Python
echo "[1/5] 检查 Python..."
if command -v python3 &> /dev/null; then
    PYTHON=python3
elif command -v python &> /dev/null; then
    PYTHON=python
else
    echo "错误：未找到 Python"
    echo "请先安装 Python 3.8+"
    exit 1
fi

$PYTHON --version
echo "✓ Python 已安装"
echo ""

# 检查 pip
echo "[2/5] 检查 pip..."
$PYTHON -m pip --version > /dev/null || {
    echo "错误：pip 未安装"
    exit 1
}
echo "✓ pip 已安装"
echo ""

# 安装系统依赖 (Linux)
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "[3/5] 安装系统依赖 (Linux)..."
    
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y portaudio19-dev python3-pyaudio
    elif command -v dnf &> /dev/null; then
        sudo dnf install -y portaudio-devel python3-devel
    elif command -v pacman &> /dev/null; then
        sudo pacman -S --noconfirm portaudio
    else
        echo "警告：未知的 Linux 发行版，可能缺少系统依赖"
    fi
    echo "✓ 系统依赖已安装"
else
    echo "[3/5] 跳过系统依赖安装 (非 Linux)"
fi
echo ""

# 安装 Python 依赖
echo "[4/5] 安装 Python 依赖..."
$PYTHON -m pip install -r requirements.txt
echo "✓ Python 依赖已安装"
echo ""

# 安装 PyTorch (如果需要)
echo "[5/5] 检查 PyTorch..."
if ! $PYTHON -c "import torch" 2>/dev/null; then
    echo "安装 PyTorch..."
    
    # 检测平台
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        $PYTHON -m pip install torch torchvision torchaudio
    else
        # Linux/Windows
        $PYTHON -m pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
    fi
    
    echo "✓ PyTorch 已安装"
else
    echo "✓ PyTorch 已安装"
fi
echo ""

echo "============================================"
echo "安装完成!"
echo "============================================"
echo ""
echo "下一步:"
echo "1. 下载模型: $PYTHON tools/download_models.py --all"
echo "2. 运行测试：$PYTHON tools/test_system.py"
echo "3. 启动程序：$PYTHON main.py"
echo ""
