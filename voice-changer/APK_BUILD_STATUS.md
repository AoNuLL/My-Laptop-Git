# 📱 APK 构建已启动！

## ✅ 代码已推送

你的代码已成功推送到 GitHub：
- **仓库**: https://github.com/AoNuLL/My-Laptop-Git
- **分支**: main
- **最新提交**: 415968f

---

## 🚀 下一步：手动触发构建

由于 GitHub Actions 需要手动启用，请按以下步骤操作：

### 步骤 1: 访问 Actions 页面

打开链接：
**https://github.com/AoNuLL/My-Laptop-Git/actions**

### 步骤 2: 启用 GitHub Actions

如果看到提示 "Actions need to be enabled"，点击绿色按钮：
**"I understand, enable GitHub Actions"**

### 步骤 3: 选择构建工作流

在左侧点击：
**"Build Android APK"**

### 步骤 4: 运行工作流

点击右侧的：
**"Run workflow"** 按钮

保持默认设置（main 分支），点击绿色 **"Run workflow"** 按钮

### 步骤 5: 等待构建完成

- 构建状态会显示为 🟡 黄色（running）
- 等待 **20-40 分钟**
- 完成后显示 🟢 绿色（success）

### 步骤 6: 下载 APK

构建完成后：
1. 点击绿色的成功标记 ✓
2. 滚动到页面底部
3. 在 **"Artifacts"** 区域点击下载
4. 下载 `VoiceChanger-apk.zip`
5. 解压后得到 `VoiceChanger-2.1.4.apk`

---

## 📲 安装到手机

### 方法 1: 直接下载
1. 在手机浏览器打开 GitHub
2. 下载 APK 文件
3. 点击安装

### 方法 2: 电脑传输
1. 电脑下载 APK
2. 用数据线传到手机
3. 在手机文件管理器中点击安装

### 方法 3: ADB 安装（开发者）
```bash
adb install VoiceChanger-2.1.4.apk
```

---

## ⚠️ 重要提示

1. **首次安装需要授权**
   - 设置 → 安全 → 允许未知来源

2. **授予应用权限**
   - 打开应用后授予麦克风权限
   - 否则无法录音变声

3. **模型文件**
   - 首次使用需下载 RVC 模型（约 50-100MB）
   - 应用内提供下载功能

---

## 🔧 如果构建失败

### 查看错误日志
1. 在 Actions 页面点击失败的构建
2. 查看日志输出
3. 截图错误信息

### 常见问题
| 问题 | 解决方案 |
|------|----------|
| 构建超时 | 重试一次，服务器可能繁忙 |
| 依赖安装失败 | 检查网络连接 |
| 签名错误 | 使用 debug 版本不需要签名 |

---

## 📊 构建规格

| 项目 | 配置 |
|------|------|
| Android 最低版本 | 5.0 (API 21) |
| 目标版本 | Android 11 (API 30) |
| CPU 架构 | ARM64 (主流手机) |
| APK 大小 | ~80-120MB |
| 构建时间 | 20-40 分钟 |

---

## 🎯 立即行动

**现在就去触发构建**:

👉 https://github.com/AoNuLL/My-Laptop-Git/actions

点击 **"Run workflow"** 后，约 30 分钟即可下载 APK！

---

**更新时间**: 2026-06-07
**构建状态**: 等待用户触发
