# 📱 APK 构建已完成准备！

## ⚠️ 重要提示

**GitHub Actions 需要在 GitHub 网站上手动启用一次！**

---

## ✅ 一键启用并构建（3 步完成）

### 步骤 1: 点击链接启用 Actions

打开以下链接：
```
https://github.com/AoNuLL/My-Laptop-Git/actions
```

你会看到提示：
> **Actions need to be enabled for this repository**

点击绿色按钮：
```
✅ I understand, enable GitHub Actions
```

---

### 步骤 2: 选择并运行构建

在左侧边栏选择:
```
📋 Build Android APK
```

点击右侧的:
```
🟢 Run workflow 按钮
```

在弹窗中:
- Branch: 选择 `main`
- 点击绿色 `Run workflow` 按钮

---

### 步骤 3: 等待并下载 APK

**等待 20-40 分钟**，构建完成后：

1. 点击绿色的成功标记 ✓
2. 滚动到页面底部
3. 在 **Artifacts** 部分下载
4. 文件名：`VoiceChanger-APK.zip`
5. 解压得到 `VoiceChanger-2.1.4-debug.apk`

---

## 📲 安装到手机

### Android 8.0+:
1. 设置 → 应用权限 → 安装未知应用
2. 允许你的浏览器或文件管理器
3. 下载并点击 APK 安装

### Android 7.0 及以下:
1. 设置 → 安全 → 未知来源
2. 勾选"允许"
3. 下载并安装 APK

---

## 🔧 技术细节

### 构建配置
| 项目 | 配置 |
|------|------|
| 最低 Android | 5.0 (API 21) |
| 目标版本 | Android 11 (API 30) |
| CPU 架构 | ARM64-v8a |
| 预期大小 | ~80-120MB |
| 构建时间 | 20-40 分钟 |

### 工作流文件
- `.github/workflows/build-apk-simple.yml`
- 自动安装所有依赖
- 自动下载 Android SDK/NDK
- 自动签名并生成 APK

---

## ❓ 常见问题

### Q: 为什么需要手动启用？

A: GitHub 安全策略要求仓库所有者首次手动启用 Actions。

### Q: 构建失败怎么办？

A: 
1. 点击失败的工作流查看日志
2. 检查是否是网络问题（重试即可）
3. 在 Issues 中提交问题

### Q: 可以自动构建吗？

A: 已配置推送自动触发，但需要先手动启用一次。

---

## 🎯 立即行动

**现在就点击这里去启用 Actions**:
👉 https://github.com/AoNuLL/My-Laptop-Git/actions

**整个启用过程只需 10 秒钟，之后就可以自动构建 APK 了！** 🚀

---

更新时间：2026-06-07
