"""
Voice Changer - 主程序入口
===========================
开源实时变声器软件
"""

import sys
import os

# 添加项目根目录到路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from gui.main_window import VoiceChangerApp


def main():
    """程序入口函数"""
    app = VoiceChangerApp()
    app.run()


if __name__ == "__main__":
    main()
