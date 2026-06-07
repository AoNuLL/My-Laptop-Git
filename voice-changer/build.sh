#!/bin/bash
# 打包脚本 - 创建可执行文件
# =============================

set -e

echo "============================================"
echo "Voice Changer - 打包脚本"
echo "============================================"
echo ""

# 检查 PyInstaller
if ! python3 -m pip show PyInstaller > /dev/null 2>&1; then
    echo "安装 PyInstaller..."
    python3 -m pip install PyInstaller
fi

# 创建.spec 文件
cat > voice-changer.spec << 'EOF'
# -*- mode: python ; coding: utf-8 -*-

block_cipher = None

a = Analysis(
    ['main.py'],
    pathex=[],
    binaries=[
        ('models', 'models'),
    ],
    datas=[
        ('gui', 'gui'),
        ('core', 'core'),
        ('tools', 'tools'),
    ],
    hiddenimports=[
        'customtkinter',
        'pyaudio',
        'librosa',
        'torch',
        'numpy',
    ],
    runtime_hooks=[],
    excludes=[],
    win_no_prefer_redirects=False,
    win_private_assemblies=False,
    cipher=block_cipher,
    noarchive=False,
)

pyz = PYZ(a.pure, a.zipped_data, cipher=block_cipher)

exe = EXE(
    pyz,
    a.scripts,
    a.binaries,
    a.zipfiles,
    a.datas,
    [],
    name='VoiceChanger',
    debug=False,
    bootloader_ignore_signals=False,
    strip=False,
    upx=True,
    upx_exclude=[],
    runtime_tmpdir=None,
    console=False,
    disable_windowed_traceback=False,
    argv_emulation=False,
    target_arch=None,
    codesign_identity=None,
    entitlements_file=None,
    icon='icon.ico',  # 可选：添加图标
)
EOF

# 运行 PyInstaller
echo "开始打包..."
pyinstaller voice-changer.spec

echo ""
echo "============================================"
echo "打包完成!"
echo "============================================"
echo ""
echo "可执行文件位置：dist/VoiceChanger"
echo ""

# 清理
rm -f voice-changer.spec
echo "✓ 已清理临时文件"
