# Voice Changer Mobile

移动版变声器应用 - Android & iOS

## 快速开始

### 方法 1: Buildozer (推荐)

```bash
# 安装 Buildozer
pip install buildozer

# 构建 APK
cd mobile
buildozer android debug

# 安装到手机
buildozer android deploy run
```

### 方法 2: Termux (直接在手机上运行)

```bash
# 在 Termux 中
pkg install python
pip install kivy numpy
python mobile/mobile_app.py
```

## 功能特性

- ✅ 实时变声
- ✅ 多音色选择
- ✅ 音调调节
- ✅ 移动端优化 UI
- ✅ 低延迟处理
- ✅ 后台运行支持

## 下载

- **Android APK**: [下载链接](链接待发布)
- **iOS**: 开发中

## 系统要求

### Android
- Android 5.0+ (API 21)
- 2GB+ RAM
- 麦克风权限
- 存储空间：500MB+

### iOS (计划中)
- iOS 13.0+
- iPhone 6s 或更新机型

## 构建说明

### 构建前准备

```bash
# 1. 安装依赖
sudo apt-get install -y python3 python3-pip build-essential git
pip3 install buildozer cython

# 2. 安装 Android SDK/NDK
# Buildozer 会自动下载

# 3. 进入移动端目录
cd voice-changer/mobile
```

### 构建调试版

```bash
buildozer android debug
```

输出：`bin/VoiceChanger-2.1.4-debug.apk`

### 构建发布版

```bash
# 首次需要签名
keytool -genkey -v -keystore my-release-key.keystore -alias voicechanger -keyalg RSA -keysize 2048 -validity 10000

# 构建
buildozer android release
```

### 部署到手机

```bash
# 1. 启用手机 USB 调试
# 2. 连接电脑
# 3. 运行
buildozer android deploy run

# 或手动安装
adb install bin/VoiceChanger-2.1.4-debug.apk
```

## 移动端优化

### 性能优化
- 采样率：22050 Hz（而非 48000）
- 缓冲区：2048 samples
- 模型量化：FP16 半精度
- ARM NEON 优化

### 内存管理
- 按需加载模型
- 自动垃圾回收
- 后台自动暂停

### 电池优化
- 智能 WakeLock
- 空闲时降低频率
- 后台服务模式

## 权限说明

应用需要以下权限：

| 权限 | 用途 | 必需 |
|------|------|------|
| RECORD_AUDIO | 录音变声 | 是 |
| MODIFY_AUDIO_SETTINGS | 音频输出 | 是 |
| WRITE_EXTERNAL_STORAGE | 保存模型 | 否 |
| INTERNET | 下载模型 | 否 |
| WAKE_LOCK | 后台运行 | 否 |

## 常见问题

### Q: 为什么 APK 这么大？

A: 因为我们打包了 PyTorch 和多个模型。解决方法：
- 使用模型按需下载
- 构建分离 APK（按 CPU 架构）
- 使用 App Bundle

### Q: 手机上运行很卡

A: 尝试：
1. 使用量化模型（`_mobile.pth` 后缀）
2. 关闭后台其他应用
3. 连接充电器（某些手机会限制性能）

### Q: 无法录音

A: 检查：
1. 是否授予麦克风权限
2. 手机是否连接了蓝牙耳机
3. 重启应用

## 开发

### 目录结构

```
mobile/
├── mobile_app.py        # 主应用
├── mobile_audio.py      # 音频处理模块
├── tools.py             # 移动工具
├── buildozer.spec       # 构建配置
├── AndroidManifest.xml  # Android 清单
└── ANDROID_DEPLOY.md    # 部署文档
```

### 添加新功能

1. 在 `mobile_app.py` 中修改 UI
2. 在 `mobile_audio.py` 中修改音频逻辑
3. 运行 `buildozer android debug` 测试
4. 部署到手机验证

### 调试

```bash
# 查看日志
adb logcat | grep -i python

# 实时日志
buildozer android logcat

# 重启应用
adb shell am force-stop org.monkeycode.voicechanger
adb shell am start -n org.monkeycode.voicechanger/org.kivy.android.PythonActivity
```

## 发布

### Google Play

1. 准备素材（图标、截图、描述）
2. 构建 Release APK
3. 创建 Google Play Console 账号 ($25)
4. 上传并填写信息
5. 提交审核

### 国内商店

- 小米：https://dev.mi.com/
- 华为：https://developer.huawei.com/
- OPPO: https://open.oppomobile.com/
- vivo: https://dev.vivo.com.cn/
- 应用宝：https://open.qq.com/

## 隐私政策

见 [项目根目录/PRIVACY.md](../PRIVACY.md)

## 更新日志

### v2.1.4-mobile
- 首次移动端发布
- 支持 Android 5.0+
- 移动端优化
- 低功耗模式

## 计划

- [ ] iOS 版本
- [ ] 云端模型库
- [ ] 多人连麦
- [ ] 实时音效

## 支持

- **Issue**: https://github.com/yourusername/voice-changer/issues
- **QQ 群**: 123456789
- **Email**: support@voicechanger.dev

---

**版本**: v2.1.4-mobile  
**最后更新**: 2026-05-15
