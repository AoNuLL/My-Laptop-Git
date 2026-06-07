[app]
title = Voice Changer
package.name = voicechanger
package.domain = org.monkeycode
package.version = 2.1.4
package.author = MonkeyCode Team
package.email = support@voicechanger.dev

# 源代码目录
source.dir = ../
source.include_exts = py,png,jpg,kv,atlas,json,pth,pt
source.exclude_exts = sh,pyc,so,o,h
source.exclude_dirs = __pycache__,build,dist,.git,venv,datasets,features,checkpoints

# Android 特定配置
orientation = portrait
android.permissions = RECORD_AUDIO,MODIFY_AUDIO_SETTINGS,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,INTERNET
android.api = 31
android.minapi = 21
android.ndk = 23b
android.arch = arm64-v8a
android.output_type = apk

# Build 配置
build.kivy = 2.3.0
build.python_version = 3.8.17

# 依赖
requirements = python3,kivy,numpy==1.23.0,pitch-detect,pandas,pyaudio,soundfile,librosa,pillow,requests,tqdm,scipy,torch,torchaudio
p4a.source_dir = .

# 图标
icon.filename = assets/icon.png

# 启动画面
splash.filename = assets/splash.png
splash.preserve_aspect_ratio = true

# 权限
android.permissions = RECORD_AUDIO,MODIFY_AUDIO_SETTINGS,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,INTERNET,VIBRATE,WAKE_LOCK

# 后台音频
android.enable_androidx = true
android.wakelock = True

# 打包输出
android.accept_sdk_license = True
android.blacklist_pytest = True
android.release_artifact = apk
android.debug_artifact = apk

# 版本信息
android.version_code = 21004
android.manifest.version_name = 2.1.4

# 签名 (可选)
# android.release_key_alias = voicechanger
# android.release_key_password = yourpassword
# android.keystore = path/to/keystore.jks
# android.keystore_password = yourpassword
