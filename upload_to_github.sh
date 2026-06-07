#!/bin/bash

# AI 变声器 GitHub 上传脚本
# 在本地电脑执行此脚本

set -e

REPO_OWNER="AoNuLL"
REPO_NAME="My-Laptop-Git"
APK_PATH="/workspace/AIVoiceChanger/app/build/outputs/apk/debug/app-debug.apk"
VERSION="1.0.0"
RELEASE_TAG="v1.0.0"

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                                                               ║"
echo "║     AI 变声器 - GitHub 上传脚本                                ║"
echo "║                                                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "仓库：${REPO_OWNER}/${REPO_NAME}"
echo "APK: ${APK_PATH}"
echo "版本：${RELEASE_TAG}"
echo ""

# 检查 gh 是否安装
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI 未安装"
    echo "请先安装：https://cli.github.com/"
    exit 1
fi

# 检查登录状态
echo "📋 检查 GitHub 登录状态..."
if ! gh auth status &> /dev/null; then
    echo "⚠️  未登录 GitHub，请先执行："
    echo "   gh auth login"
    echo ""
    read -p "按回车继续..."
    gh auth login
fi

echo "✅ GitHub 登录成功"
echo ""

# 检查 APK 文件
echo "📦 检查 APK 文件..."
if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK 文件不存在：$APK_PATH"
    exit 1
fi
APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
echo "   文件大小：$APK_SIZE"
echo ""

# 1. 克隆仓库
echo "📥 克隆仓库..."
cd ~
if [ -d "$REPO_NAME" ]; then
    echo "⚠️  仓库目录已存在，删除旧目录..."
    rm -rf "$REPO_NAME"
fi

git clone "https://github.com/${REPO_OWNER}/${REPO_NAME}.git"
cd "$REPO_NAME"

# 2. 删除仓库中的所有文件
echo "🗑️  删除仓库中的所有文件..."
find . -type f ! -name '.git' ! -name '.gitignore' -delete
find . -type d -empty ! -name '.git' -delete

# 创建 .gitignore（防止删除 .git 目录）
cat > .gitignore << 'EOF'
# Android
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
/.externalNativeBuild
/.cxx
/local.properties

# APK
*.apk
*.aab

# Logs
*.log
EOF

echo "   已清空仓库文件"
echo ""

# 3. 复制项目文件
echo "📁 复制 AI 变声器项目文件..."
cp -r /workspace/AIVoiceChanger/* .
rm -rf .gradle  # 不复制 gradle 缓存

echo "   已复制项目文件"
echo ""

# 4. 提交并推送
echo "📤 提交并推送代码..."
git add .
git commit -m "feat: AI 变声器 v${VERSION}

- 完整的 Android 变声应用
- 支持录音、播放、导出
- AI 模型管理功能
- APK 大小：${APK_SIZE}"
git push origin main

echo "✅ 代码推送成功"
echo ""

# 5. 创建 Release
echo "🚀 创建 GitHub Release..."
RELEASE_NOTES="## AI 变声器 v${VERSION}

### 📦 功能特性
- ✅ 专业级音频录制
- ✅ 实时波形显示
- ✅ AI 变声处理
- ✅ 音频导出功能
- ✅ 模型管理设置

### 📱 系统要求
- Android 7.0+ (API 24)
- 目标版本：Android 14 (API 34)

### 📥 安装说明
1. 下载 \`app-debug.apk\`
2. 在 Android 设备上安装
3. 首次运行需授予录音权限

### 🔗 AI 模型下载
应用内设置页面提供模型下载链接：
- HuggingFace
- GitHub Releases

### ⚠️ 注意事项
- Debug 版本，包含调试信息
- AI 模型文件需单独下载（约 50MB/个）
- 首次使用请下载模型到 \`assets/models/\` 目录

---
**构建时间**: $(date +%Y-%m-%d)
**APK 大小**: ${APK_SIZE}"

# 创建 Release 并上传 APK
gh release create "$RELEASE_TAG" \
    --title "AI 变声器 v${VERSION}" \
    --notes "$RELEASE_NOTES" \
    "$APK_PATH"

echo "✅ Release 创建成功"
echo ""

# 6. 显示结果
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                                                               ║"
echo "║  ✅ 上传完成！                                                ║"
echo "║                                                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "📦 仓库地址:"
echo "   https://github.com/${REPO_OWNER}/${REPO_NAME}"
echo ""
echo "🚀 Release 页面:"
echo "   https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/tag/${RELEASE_TAG}"
echo ""
echo "📱 下载 APK:"
echo "   https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/latest/download/app-debug.apk"
echo ""
