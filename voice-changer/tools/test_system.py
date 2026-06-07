"""
系统测试脚本
==============
测试变声器各项功能是否正常工作
"""

import sys
import time
import numpy as np


def test_python_version():
    """测试 Python 版本"""
    print(f"[1/8] Python 版本：{sys.version}")
    
    # 检查版本
    if sys.version_info < (3, 8):
        print("  ✗ Python 版本过低，需要 3.8+")
        return False
    else:
        print("  ✓ Python 版本符合要求")
        return True


def test_dependencies():
    """测试依赖包"""
    print("\n[2/8] 检查依赖包...")
    
    required_packages = {
        "numpy": "数值计算",
        "torch": "深度学习",
        "pyaudio": "音频处理",
        "librosa": "音频分析",
        "customtkinter": "GUI 框架",
        "soundfile": "音频文件"
    }
    
    all_installed = True
    
    for package, description in required_packages.items():
        try:
            __import__(package)
            print(f"  ✓ {package:20} - {description}")
        except ImportError:
            print(f"  ✗ {package:20} - 未安装")
            all_installed = False
    
    return all_installed


def test_audio_device():
    """测试音频设备"""
    print("\n[3/8] 检查音频设备...")
    
    try:
        import pyaudio
        
        pa = pyaudio.PyAudio()
        
        # 统计输入输出设备
        input_devices = []
        output_devices = []
        
        for i in range(pa.get_device_count()):
            info = pa.get_device_info_by_index(i)
            if info.get('maxInputChannels') > 0:
                input_devices.append(info.get('name'))
            if info.get('maxOutputChannels') > 0:
                output_devices.append(info.get('name'))
        
        print(f"  ✓ 输入设备：{len(input_devices)} 个")
        print(f"  ✓ 输出设备：{len(output_devices)} 个")
        
        # 显示默认设备
        default_input = pa.get_default_input_device_info()
        default_output = pa.get_default_output_device_info()
        
        print(f"  默认输入：{default_input.get('name')}")
        print(f"  默认输出：{default_output.get('name')}")
        
        pa.terminate()
        
        if len(input_devices) == 0:
            print("  ⚠ 警告：未找到输入设备")
        
        return True
        
    except Exception as e:
        print(f"  ✗ 检查失败：{str(e)}")
        return False


def test_cuda():
    """测试 CUDA 支持"""
    print("\n[4/8] 检查 GPU 加速...")
    
    try:
        import torch
        
        if torch.cuda.is_available():
            device_count = torch.cuda.device_count()
            print(f"  ✓ CUDA 可用，{device_count} 个设备")
            
            for i in range(device_count):
                name = torch.cuda.get_device_name(i)
                print(f"    - GPU {i}: {name}")
            
            return True
        else:
            print("  ⊘ CUDA 不可用，使用 CPU")
            if torch.backends.mps.is_available():
                print("  ✓ Apple MPS 可用")
                return True
            return False
            
    except Exception as e:
        print(f"  ✗ 检查失败：{str(e)}")
        return False


def test_models():
    """测试模型文件"""
    print("\n[5/8] 检查模型文件...")
    
    from pathlib import Path
    
    models_dir = Path("models")
    
    if not models_dir.exists():
        print("  ✗ 模型目录不存在")
        return False
    
    # 查找模型文件
    pth_files = list(models_dir.rglob("*.pth"))
    onnx_files = list(models_dir.rglob("*.onnx"))
    
    if pth_files or onnx_files:
        print(f"  ✓ 找到 {len(pth_files)} 个 PTH 模型")
        print(f"  ✓ 找到 {len(onnx_files)} 个 ONNX 模型")
        return True
    else:
        print("  ⚠ 未找到模型文件，请先下载模型")
        print("  运行: python tools/download_models.py --all")
        return False


def test_audio_io():
    """测试音频输入输出"""
    print("\n[6/8] 测试音频录制和播放...")
    
    try:
        import pyaudio
        import numpy as np
        
        pa = pyaudio.PyAudio()
        
        # 创建测试音频（静音）
        test_audio = np.zeros(4800, dtype=np.float32)
        
        # 测试播放
        stream = pa.stream(
            format=pyaudio.paFloat32,
            channels=1,
            rate=48000,
            output=True,
            frames_per_buffer=4800
        )
        
        stream.write(test_audio.tobytes())
        stream.stop_stream()
        stream.close()
        
        pa.terminate()
        
        print("  ✓ 音频输出测试通过")
        return True
        
    except Exception as e:
        print(f"  ✗ 测试失败：{str(e)}")
        return False


def test_virtual_audio():
    """测试虚拟音频设备"""
    print("\n[7/8] 检查虚拟音频设备...")
    
    try:
        import pyaudio
        
        pa = pyaudio.PyAudio()
        
        virtual_found = False
        
        for i in range(pa.get_device_count()):
            info = pa.get_device_info_by_index(i)
            name = info.get('name', '').lower()
            
            # 检查是否是虚拟设备
            if any(virtual in name for virtual in ["vb-audio", "cable", "blackhole"]):
                print(f"  ✓ 虚拟音频设备：{name}")
                virtual_found = True
        
        pa.terminate()
        
        if virtual_found:
            print("  ✓ 虚拟音频设备已安装")
            return True
        else:
            print("  ⚠ 未检测到虚拟音频设备")
            print("  提示：在某些语音软件中需要使用虚拟声卡")
            return False
            
    except Exception as e:
        print(f"  ✗ 检查失败：{str(e)}")
        return False


def test_gui():
    """测试 GUI 组件"""
    print("\n[8/8] 测试 GUI 组件...")
    
    try:
        import customtkinter as ctk
        
        # 尝试创建窗口（不运行主循环）
        root = ctk.CTk()
        root.update()
        root.destroy()
        
        print("  ✓ GUI 组件加载正常")
        return True
        
    except Exception as e:
        print(f"  ✗ GUI 测试失败：{str(e)}")
        return False


def run_all_tests():
    """运行所有测试"""
    print("=" * 60)
    print("Voice Changer - 系统测试")
    print("=" * 60)
    
    results = []
    
    results.append(test_python_version())
    results.append(test_dependencies())
    results.append(test_audio_device())
    results.append(test_cuda())
    results.append(test_models())
    results.append(test_audio_io())
    results.append(test_virtual_audio())
    results.append(test_gui())
    
    print("\n" + "=" * 60)
    print("测试结果汇总")
    print("=" * 60)
    
    passed = sum(results)
    total = len(results)
    
    print(f"通过：{passed}/{total}")
    
    if passed == total:
        print("\n✓ 所有测试通过！系统已就绪")
        print("\n接下来请:")
        print("1. 下载模型文件 (如未下载)")
        print("2. 安装虚拟声卡驱动 (用于实时变声)")
        print("3. 运行：python main.py 启动程序")
    else:
        print("\n⚠ 部分测试未通过，请检查安装")
    
    return passed == total


if __name__ == "__main__":
    success = run_all_tests()
    sys.exit(0 if success else 1)
