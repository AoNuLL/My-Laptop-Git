[app]
title = Voice Changer
package.name = voicechanger
package.domain = org.monkeycode

version = 2.1.4
requirements = python3,kivy
source.dir = ../
source.include_exts = py,png,jpg,kv,atlas,json
source.exclude_exts = sh,pyc,so,o,h

orientation = portrait
android.api = 30
android.minapi = 21
android.ndk = 23b
android.permissions = RECORD_AUDIO,INTERNET
android.arch = arm64-v8a
