# 推送到 GitHub 指南

## 📤 快速推送

### 方法 1: 使用推送脚本（推荐）

```bash
cd /workspace/voice-changer

# 设置你的 GitHub 用户名
export GITHUB_USER="yourusername"

# 运行推送脚本
./push-to-github.sh
```

### 方法 2: 手动推送

```bash
cd /workspace/voice-changer

# 1. 添加远程仓库（替换为你的 GitHub 用户名）
git remote add origin https://github.com/yourusername/My-Laptop-Git.git

# 或使用 SSH（推荐）
git remote add origin git@github.com:yourusername/My-Laptop-Git.git

# 2. 推送
git branch -M main
git push -u origin main
```

---

## 🔐 认证方式配置

### 方式 1: GitHub Personal Access Token

1. **创建 Token**:
   - 访问 https://github.com/settings/tokens
   - 点击 "Generate new token (classic)"
   - 选择权限：`repo` (Full control of private repositories)
   - 生成并复制 token

2. **使用 Token 推送**:
```bash
git push https://yourusername:YOUR_TOKEN@github.com/yourusername/My-Laptop-Git.git main
```

### 方式 2: SSH 密钥

1. **生成 SSH 密钥**:
```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

2. **添加 SSH 密钥到 GitHub**:
   - 复制公钥：`cat ~/.ssh/id_ed25519.pub`
   - 访问 https://github.com/settings/keys
   - 点击 "New SSH key"
   - 粘贴公钥

3. **使用 SSH 推送**:
```bash
git remote add origin git@github.com:yourusername/My-Laptop-Git.git
git push -u origin main
```

### 方式 3: Git Credential Manager（Windows）

Windows 用户推荐使用 Git Credential Manager：
1. 下载安装：https://github.com/GitCredentialManager/git-credential-manager
2. 安装后重启终端
3. 推送时会自动弹出登录窗口

---

## 📦 完整推送命令

```bash
# 进入项目目录
cd /workspace/voice-changer

# 1. 初始化 git（如果未初始化）
git init

# 2. 添加所有文件
git add .

# 3. 提交
git commit -m "Initial commit: Voice Changer v2.1.4"

# 4. 添加远程仓库（二选一）

# HTTPS 方式
git remote add origin https://github.com/yourusername/My-Laptop-Git.git

# SSH 方式（推荐）
git remote add origin git@github.com:yourusername/My-Laptop-Git.git

# 5. 推送
git branch -M main
git push -u origin main
```

---

## ⚠️ 常见问题

### Q1: 推送时提示认证失败

**解决方案**:
- 使用 Personal Access Token 代替密码
- 或配置 SSH 密钥

### Q2: 仓库已存在，如何覆盖？

```bash
# 强制推送（慎用，会覆盖远程历史）
git push -u origin main --force
```

### Q3: 文件太大无法推送

**解决方案**:
```bash
# 1. 检查大文件
git rev-list --objects --all | git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' | awk '$1=="blob" && $3>10485760 {print $3/1024/1024, $2, $4}'

# 2. 使用 Git LFS
git lfs install
git lfs track "*.pth"
git lfs track "*.pt"
git add .gitattributes
```

### Q4: 如何更新已有仓库？

```bash
# 1. 添加新更改
git add .

# 2. 提交
git commit -m "Update: 添加新功能"

# 3. 推送
git push origin main
```

---

## 📊 GitHub 仓库信息

### 推荐仓库信息

**仓库名称**: My-Laptop-Git  
**可见性**: Public（公开）或 Private（私有）  
**描述**: Voice Changer - 开源实时变声器，基于 RVC 技术的跨平台语音转换软件  
**标签**: python, rvc, voice-changer, ai, audio, pytorch, kivy

### .gitignore 已包含

- Python 缓存
- 虚拟环境
- 大型模型文件（*.pth, *.pt）
- 构建产物
- IDE 配置

---

## 🌟 GitHub Pages 部署（可选）

如果想创建项目网站：

1. **创建 docs 分支**:
```bash
git checkout -b docs
git push -u origin docs
```

2. **启用 GitHub Pages**:
   - 访问仓库 Settings -> Pages
   - Source 选择 "Deploy from a branch"
   - Branch 选择 "docs"

---

## 📝 推荐 README 模板

在 GitHub 仓库中，README.md 会自动显示。建议包含：

```markdown
# Voice Changer - 开源实时变声器

[![Python 3.8+](https://img.shields.io/badge/python-3.8+-blue.svg)](https://www.python.org/downloads/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

基于 RVC 技术的跨平台实时变声软件

## 功能特性
- ✨ 低延迟实时变声 (<60ms)
- 🎤 多音色支持 (600+ 模型)
- 🖥️ 跨平台 (Windows/macOS/Linux/Android)
- 🆓 完全免费开源

## 快速开始

```bash
# 克隆项目
git clone https://github.com/yourusername/My-Laptop-Git.git
cd My-Laptop-Git/voice-changer

# 安装依赖
pip install -r requirements.txt

# 运行程序
python main.py
```

## 文档
- [快速上手](QUICKSTART.md)
- [功能列表](FEATURES.md)
- [模型下载](docs/model_download_guide.md)
- [模型训练](docs/model_training_tutorial.md)

## 许可证
MIT License
```

---

## 🔗 相关链接

- **GitHub Desktop**: https://desktop.github.com/ (图形化推送工具)
- **GitHub CLI**: https://cli.github.com/ (命令行工具)
- **Git Credential Manager**: https://github.com/GitCredentialManager/git-credential-manager
- **SSH 密钥配置**: https://docs.github.com/en/authentication/connecting-to-github-with-ssh

---

**最后更新**: 2026-05-15
