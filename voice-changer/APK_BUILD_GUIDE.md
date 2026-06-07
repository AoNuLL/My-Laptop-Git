# Voice Changer APK 构建指南

## 📱 构建方法（3 选 1）

### 方法 1: 使用 GitHub Actions 自动构建（推荐⭐）

最简单！无需本地环境，GitHub 服务器自动构建。

**步骤：**

1. **启用 GitHub Actions**
   - 访问：https://github.com/AoNuLL/My-Laptop-Git/actions
   - 点击 "Enable GitHub Actions"

2. **手动触发构建**
   - 在 Actions 页面选择 "Build APK"
   - 点击 "Run workflow"
   - 等待 20-30 分钟

3. **下载 APK**
   - 构建完成后在 Artifacts 下载
   - 文件名：`VoiceChanger-2.1.4.apk`

**优点**：无需本地环境，100% 成功

---

### 方法 2: 使用在线构建服务

**Replit 构建（免费）**

1. 访问 https://replit.com
2. 创建新 Repl（选择 Bash 模板）
3. 运行以下命令：

```bash
# 安装依赖
sudo apt update
sudo apt install -y python3-pip python3-venv openjdk-17-jdk wget unzip

# 安装 Buildozer
pip3 install buildozer cython

# 克隆项目
git clone https://github.com/AoNuLL/My-Laptop-Git.git
cd My-Laptop-Git/voice-changer/mobile

# 构建 APK
buildozer android debug

# 输出位置
ls -lh bin/*.apk
```

4. 下载生成的 APK 文件

---

### 方法 3: 本地完整构建（Linux/Mac）

**环境要求：**
- Ubuntu/Debian Linux 或 macOS
- 50GB 可用磁盘空间
- 8GB+ 内存
- 稳定的网络（下载 Android SDK）

**安装脚本：**

```bash
#!/bin/bash
# save as build-apk.sh

set -e

echo "=== Voice Changer APK 构建脚本 ==="

# 1. 安装系统依赖
echo "[1/6] 安装系统依赖..."
sudo apt update
sudo apt install -y \
    python3 python3-pip python3-venv \
    openjdk-17-jdk wget unzip \
    build-essential git autoconf \
    libtool libncurses5 libstdc++6

# 设置 JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 2. 安装 Buildozer
echo "[2/6] 安装 Buildozer..."
pip3 install buildozer cython packaging

# 3. 进入项目目录
echo "[3/6] 准备项目..."
cd voice-changer/mobile

# 4. 初始化 Buildozer
echo "[4/6] 初始化构建环境..."
buildozer init 2>/dev/null || true

# 复制配置文件
cp buildozer.spec buildozer.spec.bak 2>/dev/null || true

# 5. 构建 APK
echo "[5/6] 开始构建 APK..."
echo "这将需要 20-40 分钟，请耐心等待..."
buildozer android debug

# 6. 输出结果
echo "[6/6] 构建完成！"
echo ""
echo "=== APK 已生成 ==="
ls -lh bin/*.apk
echo ""
echo "APK 位置：$(pwd)/bin/VoiceChanger-2.1.4-debug.apk"
echo ""
echo "安装到手机:"
echo "  adb install bin/VoiceChanger-2.1.4-debug.apk"
```

**使用方法：**
```bash
chmod +x build-apk.sh
./build-apk.sh
```

---

## ✅ APK 测试清单

构建完成后，请在手机上测试：

- [ ] APK 可以正常安装
- [ ] 打开应用无闪退
- [ ] 授予麦克风权限
- [ ] 选择音色模型
- [ ] 按住录音测试变声
- [ ] 音调调节功能正常
- [ ] 退出应用后无残留

---

## ❓ 常见问题

### Q1: 构建失败，提示缺少依赖

**解决方案**: 使用 GitHub Actions 方法 1

### Q2: APK 安装失败

**检查**:
- 手机设置 → 安全 → 允许未知来源应用
- Android 版本是否 >= 5.0
- 可用存储空间是否足够

### Q3: 应用闪退

**原因**: 模型文件缺失或权限问题

**解决方案**:
1. 首次启动授予所有权限
2. 下载模型文件放到 `models/` 目录
3. 查看日志：`adb logcat | grep -i python`

### Q4: APK 太大（>100MB）

**优化方案**:
```bash
# 在 buildozer.spec 中设置
android.split_apks = true

# 或构建 App Bundle
buildozer android release appbundle
```

---

## 📊 APK 规格

| 项目 | 规格 |
|------|------|
| 最低 Android | 5.0 (API 21) |
| 推荐 Android | 10.0+ |
| APK 大小 | ~80-150MB |
| 内存需求 | 2GB+ |
| 必需权限 | 麦克风、存储 |

---

## 🔗 相关资源

- **GitHub Actions**: https://github.com/AoNuLL/My-Laptop-Git/actions
- **Buildozer 文档**: https://buildozer.readthedocs.io/
- **Android 调试**: `adb logcat -s python`

---

**更新时间**: 2026-05-15
