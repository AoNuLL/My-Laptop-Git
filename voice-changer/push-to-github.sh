#!/bin/bash
# Voice Changer 推送到 GitHub 脚本
# ======================================

set -e

echo "========================================="
echo "Voice Changer - GitHub 推送脚本"
echo "========================================="
echo ""

# 检查 git 是否已配置
if ! git config user.email > /dev/null 2>&1; then
    echo "错误：未配置 git 用户信息"
    echo "请运行以下命令:"
    echo "  git config --global user.email \"your_email@example.com\""
    echo "  git config --global user.name \"Your Name\""
    exit 1
fi

# 获取 GitHub 用户名
echo "请输入你的 GitHub 用户名:"
read GITHUB_USER

if [ -z "$GITHUB_USER" ]; then
    echo "错误：GitHub 用户名不能为空"
    exit 1
fi

REPO_NAME="My-Laptop-Git"
REPO_URL_HTTPS="https://github.com/${GITHUB_USER}/${REPO_NAME}.git"
REPO_URL_SSH="git@github.com:${GITHUB_USER}/${REPO_NAME}.git"

echo ""
echo "选择推送方式:"
echo "1. HTTPS (需要 Token 或密码)"
echo "2. SSH (需要配置 SSH 密钥，推荐)"
echo ""
read -p "请选择 [1-2]: " choice

case $choice in
    1)
        REMOTE_URL=$REPO_URL_HTTPS
        echo ""
        echo "提示：如果使用 HTTPS，建议使用 Personal Access Token"
        echo "创建地址：https://github.com/settings/tokens"
        ;;
    2)
        REMOTE_URL=$REPO_URL_SSH
        echo ""
        echo "提示：确保已配置 SSH 密钥"
        echo "查看配置：ssh -T git@github.com"
        ;;
    *)
        echo "无效选择"
        exit 1
        ;;
esac

echo ""
echo "远程仓库：${REMOTE_URL}"
echo ""

# 检查远程仓库是否已存在
if git remote -v | grep -q origin; then
    echo "发现现有远程仓库:"
    git remote -v
    echo ""
    read -p "是否覆盖？[y/N]: " overwrite
    if [ "$overwrite" = "y" ] || [ "$overwrite" = "Y" ]; then
        git remote remove origin
    else
        echo "取消操作"
        exit 0
    fi
fi

# 添加远程仓库
echo "添加远程仓库..."
git remote add origin $REMOTE_URL
echo "✓ 远程仓库已添加"

# 提交代码
echo ""
echo "检查代码变更..."
if git status --porcelain | grep -q .; then
    echo "发现未提交的变更，是否提交？[Y/n]: "
    read -r commit_choice
    if [ "$commit_choice" != "n" ] && [ "$commit_choice" != "N" ]; then
        git add .
        git commit -m "Initial commit: Voice Changer v2.1.4"
        echo "✓ 代码已提交"
    fi
else
    echo "✓ 没有未提交的变更"
fi

# 推送
echo ""
echo "准备推送到 GitHub..."
echo "仓库：${GITHUB_USER}/${REPO_NAME}"
echo ""
read -p "确认推送？[y/N]: " confirm

if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
    echo ""
    echo "开始推送..."
    
    # 重命名分支
    git branch -M main 2>/dev/null || true
    
    # 尝试推送
    if git push -u origin main; then
        echo ""
        echo "========================================="
        echo "✓ 推送成功!"
        echo "========================================="
        echo ""
        echo "GitHub 仓库地址:"
        echo "https://github.com/${GITHUB_USER}/${REPO_NAME}"
        echo ""
        echo "下一步:"
        echo "1. 在 GitHub 上查看项目"
        echo "2. 配置 GitHub Pages (可选)"
        echo "3. 邀请协作者 (可选)"
        echo ""
    else
        echo ""
        echo "========================================="
        echo "✗ 推送失败"
        echo "========================================="
        echo ""
        echo "可能的原因:"
        echo "1. 仓库不存在 - 请先在 GitHub 创建仓库"
        echo "2. 认证失败 - 请检查 Token/密码/SSH 密钥"
        echo "3. 权限不足 - 请检查仓库访问权限"
        echo ""
        echo "解决方法:"
        echo "详细指南：查看 PUSH_TO_GITHUB.md"
        echo ""
    fi
else
    echo "取消推送"
fi

echo ""
echo "========================================="
