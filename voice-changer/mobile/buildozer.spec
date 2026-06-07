[app]
title = Voice Changer
package.name = voicechanger
package.domain = org.monkeycode
version = 2.1.4
requirements = python3==3.11.14,kivy==2.2.0,pygame_sdl2,jinja2,six,pyjnius,pil
source.dir = ../
source.include_exts = py,png,jpg,kv,atlas,json

orientation = portrait
android.api = 30
android.minapi = 21
android.ndk = 25
android.permissions = RECORD_AUDIO,INTERNET,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE
android.archs = arm64-v8a
