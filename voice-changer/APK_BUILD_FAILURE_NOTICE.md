# 🚨 本地构建失败 - 请使用 GitHub Actions

## 原因
当前环境存在网络限制，无法下载 Android 构建所需的依赖包。

## ✅ 推荐方案：GitHub Actions 自动构建

这是**最可靠**的方式，GitHub 服务器有完美的网络环境和完整的构建工具链。

### 步骤 1：访问 Actions 页面

打开：
```
https://github.com/AoNuLL/My-Laptop-Git/actions
```

### 步骤 2：启用 GitHub Actions（如果是第一次）

如果看到提示，点击：
```
"I understand, enable GitHub Actions"
```

### 步骤 3：选择构建工作流

左侧点击：
```
Build Android APK
```

### 步骤 4：触发构建

点击右侧的：
```
Run workflow 按钮
```

保持默认设置（main 分支），点击绿色 "Run workflow"

### 步骤 5：等待构建完成

- 构建状态：🟡 黄色（running）
- 等待时间：**20-40 分钟**
- 完成后：🟢 绿色（success）

### 步骤 6: 下载 APK

1. 点击绿色的成功标记 ✓
2. 滚动到页面底部
3. 在 **Artifacts** 区域下载
4. 文件名：`VoiceChanger-2.1.4.apk`

---

## 📲 安装到手机

### 方法 1: 直接下载（推荐）
1. 在手机浏览器打开 GitHub
2. 进入 Actions → 最新构建 → Artifacts
3. 下载 APK
4. 点击安装

### 方法 2: 电脑传输
1. 电脑下载 APK
2. USB 传到手机
3. 文件管理器中点击安装

### 方法 3: ADB 安装
```bash
adb install VoiceChanger-2.1.4.apk
```

---

## ⚙️ 使用教程

### 首次启动

1. **授予权限**
   - 设置 → 应用 → Voice Changer → 权限
   - 开启"麦克风"权限（必需）

2. **下载模型**
   - 应用内点击"下载模型"
   - 选择喜欢的音色（推荐 Male 或 Female）
   - 等待下载完成（约 50-100MB）

3. **开始变声**
   - 选择音色模型
   - 调节音调（-24 到 +24）
   - 按住"录音"按钮说话
   - 松手听变声效果

---

## ✅ APK 规格

| 项目 | 配置 |
|------|------|
| 应用名称 | Voice Changer |
| 版本 | 2.1.4 |
| 最低 Android | 5.0 (API 21) |
| 目标版本 | Android 11 (API 30) |
| CPU 架构 | ARM64（主流手机） |
| APK 大小 | ~80-120MB |
| 构建类型 | Debug（可直接安装） |

---

## ❓ 故障排查

### Q1: APK 无法安装

**解决**:
- 设置 → 安全 → 允许未知来源
- 确保 500MB+ 存储空间

### Q2: 应用闪退

**原因**: 缺少麦克风权限

**解决**:
- 设置 → 应用 → Voice Changer → 权限
- 授予麦克风权限

### Q3: 录音无声

**原因**: 模型文件缺失

**解决**:
- 应用内下载模型
- 或手动放入 `models/` 目录

### Q4: GitHub Actions 构建失败

**原因**: 服务器临时问题

**解决**:
- 等待 5 分钟后重试
- 查看日志了解具体错误

---

## 🔗 相关链接

| 用途 | 链接 |
|------|------|
| **GitHub 仓库** | https://github.com/AoNuLL/My-Laptop-Git |
| **Actions 构建** | https://github.com/AoNuLL/My-Laptop-Git/actions |
| **问题反馈** | https://github.com/AoNuLL/My-Laptop-Git/issues |
| **使用文档** | `mobile/APK_DOWNLOAD.md` |

---

## 📝 备注

当前环境已配置好所有构建工具（Java JDK、Buildozer、Android SDK），但由于网络限制无法下载依赖包。

**强烈建议使用 GitHub Actions**，这是官方支持的构建方式，成功率高且无需本地环境。

---

**更新时间**: 2026-06-07
**构建状态**: 本地环境受限，请使用 GitHub Actions
