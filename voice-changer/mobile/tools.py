"""
移动端工具脚本
=============
Android/iOS 移动端特定工具
"""

import sys
import os
from pathlib import Path


def check_android_environment():
    """检查 Android 开发环境"""
    print("检查 Android 开发环境...")
    
    checks = {
        'Python': sys.version_info >= (3, 8),
        'Buildozer': False,
        'Android SDK': False,
        'Android NDK': False,
    }
    
    # 检查 Buildozer
    try:
        import buildozer
        checks['Buildozer'] = True
    except ImportError:
        pass
    
    # 检查环境变量
    checks['Android SDK'] = 'ANDROID_HOME' in os.environ or 'ANDROID_SDK_ROOT' in os.environ
    checks['Android NDK'] = 'ANDROID_NDK_HOME' in os.environ
    
    # 输出结果
    print("\n环境检查结果:")
    print("=" * 40)
    for name, status in checks.items():
        symbol = "✓" if status else "✗"
        print(f"{symbol} {name}")
    
    all_ok = all(checks.values())
    print("=" * 40)
    
    if all_ok:
        print("✓ 环境检查通过，可以开始构建")
    else:
        print("\n需要安装以下内容:")
        if not checks['Buildozer']:
            print("  - pip install buildozer")
        if not checks['Android SDK']:
            print("  - 安装 Android SDK 并设置 ANDROID_HOME")
        if not checks['Android NDK']:
            print("  - 安装 Android NDK 并设置 ANDROID_NDK_HOME")
    
    return all_ok


def create_mobile_requirements():
    """创建移动端依赖文件"""
    mobile_requirements = """# Voice Changer Mobile Dependencies
# Android/iOS

# 核心
numpy>=1.23.0,<1.25.0
kivy>=2.3.0
pillow>=9.0.0

# 音频
soundfile>=0.12.1
librosa>=0.10.0
scipy>=1.10.0

# 工具
requests>=2.31.0
tqdm>=4.65.0

# 移动端优化
pyaudio>=0.2.13
sounddevice>=0.4.6

# PyTorch Mobile (可选，需要特殊构建)
# torch @ https://download.pytorch.org/whl/test/cpu/torch-2.0.0%2Bcpu-cp38-cp38-linux_x86_64.whl
"""
    
    req_path = Path("requirements_mobile.txt")
    req_path.write_text(mobile_requirements)
    
    print(f"✓ 已创建移动端依赖文件：{req_path}")
    return req_path


def optimize_model_for_mobile(model_path: str, output_path: str = None):
    """
    优化模型用于移动端
    
    Args:
        model_path: 原模型路径
        output_path: 输出路径（可选）
    """
    try:
        import torch
    except ImportError:
        print("错误：需要安装 PyTorch")
        return
    
    if output_path is None:
        output_path = Path(model_path).parent / f"{Path(model_path).stem}_mobile.pth"
    
    print(f"优化模型：{model_path}")
    print(f"输出：{output_path}")
    
    # 加载模型
    checkpoint = torch.load(model_path, map_location='cpu')
    
    # 量化权重
    if 'state_dict' in checkpoint:
        state_dict = checkpoint['state_dict']
        
        # 转换为半精度
        for key in state_dict:
            if state_dict[key].dtype == torch.float32:
                state_dict[key] = state_dict[key].half()
    
    # 保存
    torch.save(checkpoint, output_path)
    
    # 显示大小对比
    original_size = Path(model_path).stat().st_size
    mobile_size = Path(output_path).stat().st_size
    
    print(f"\n优化结果:")
    print(f"  原始大小：{original_size / 1024 / 1024:.1f} MB")
    print(f"  优化后：{mobile_size / 1024 / 1024:.1f} MB")
    print(f"  减少：{(1 - mobile_size / original_size) * 100:.1f}%")
    
    return output_path


def batch_optimize_models(input_dir: str, output_dir: str = None):
    """批量优化模型"""
    import torch
    
    input_path = Path(input_dir)
    if output_dir:
        output_path = Path(output_dir)
        output_path.mkdir(parents=True, exist_ok=True)
    else:
        output_path = input_path
    
    models = list(input_path.glob("*.pth")) + list(input_path.glob("*.pt"))
    
    if not models:
        print(f"在 {input_dir} 中未找到模型文件")
        return
    
    print(f"找到 {len(models)} 个模型")
    print("开始批量优化...")
    
    for model in models:
        try:
            optimize_model_for_mobile(
                str(model),
                str(output_path / f"{model.stem}_mobile.pth")
            )
        except Exception as e:
            print(f"优化失败 {model.name}: {str(e)}")
    
    print(f"\n完成：{len(models)} 个模型")


def create_android_icon():
    """创建 Android 应用图标"""
    from PIL import Image
    
    icon_sizes = {
        'ldpi': (36, 36),
        'mdpi': (48, 48),
        'hdpi': (72, 72),
        'xhdpi': (96, 96),
        'xxhdpi': (144, 144),
        'xxxhdpi': (192, 192),
    }
    
    # 创建基础图标（如果不存在）
    base_icon = Path("assets/icon_base.png")
    if not base_icon.exists():
        # 创建一个简单的图标
        img = Image.new('RGB', (512, 512), color='blue')
        # 这里应该添加实际的图标设计
        base_icon.parent.mkdir(parents=True, exist_ok=True)
        img.save(base_icon)
        print(f"✓ 创建基础图标：{base_icon}")
    
    # 生成各个尺寸
    for density, size in icon_sizes.items():
        img = Image.open(base_icon)
        img = img.resize(size, Image.LANCZOS)
        
        output_dir = Path(f"assets/icon-{density}")
        output_dir.mkdir(parents=True, exist_ok=True)
        img.save(output_dir / "icon.png")
        
        print(f"✓ {density}: {size[0]}x{size[1]}")
    
    print("\n图标已生成到 assets/ 目录")


def create_splash_screen():
    """创建启动画面"""
    from PIL import Image
    
    splash_size = (1080, 1920)  # Full HD
    
    img = Image.new('RGB', splash_size, color='#1a1a2e')
    
    # 这里添加启动画面设计
    output = Path("assets/splash.png")
    output.parent.mkdir(parents=True, exist_ok=True)
    img.save(output)
    
    print(f"✓ 启动画面：{output}")
    print(f"  尺寸：{splash_size[0]}x{splash_size[1]}")


def test_mobile_app():
    """测试移动端应用"""
    print("测试移动端应用...")
    
    # 检查 Kivy
    try:
        import kivy
        print(f"✓ Kivy 版本：{kivy.__version__}")
    except ImportError:
        print("✗ Kivy 未安装")
        return False
    
    # 检查音频
    try:
        import sounddevice
        devices = sounddevice.query_devices()
        print(f"✓ 找到 {len(devices)} 个音频设备")
    except Exception as e:
        print(f"⚠ 音频设备检查失败：{str(e)}")
    
    # 尝试启动应用
    try:
        from mobile.mobile_app import VoiceChangerMobileApp
        print("✓ 应用模块加载成功")
        return True
    except Exception as e:
        print(f"✗ 应用加载失败：{str(e)}")
        return False


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="移动端工具"
    )
    
    subparsers = parser.add_subparsers(dest="command", help="命令")
    
    # 检查环境
    check_parser = subparsers.add_parser("check", help="检查环境")
    check_parser.add_argument(
        "--full",
        action="store_true",
        help="执行完整检查"
    )
    
    # 优化模型
    optimize_parser = subparsers.add_parser("optimize", help="优化模型")
    optimize_parser.add_argument(
        "-i", "--input",
        type=str,
        required=True,
        help="模型文件或目录"
    )
    optimize_parser.add_argument(
        "-o", "--output",
        type=str,
        help="输出目录"
    )
    
    # 创建资源
    assets_parser = subparsers.add_parser("assets", help="创建应用资源")
    assets_parser.add_argument(
        "--icon",
        action="store_true",
        help="创建图标"
    )
    assets_parser.add_argument(
        "--splash",
        action="store_true",
        help="创建启动画面"
    )
    
    # 测试
    test_parser = subparsers.add_parser("test", help="测试应用")
    
    args = parser.parse_args()
    
    if args.command == "check":
        check_android_environment()
    
    elif args.command == "optimize":
        input_path = Path(args.input)
        if input_path.is_file():
            optimize_model_for_mobile(str(input_path), args.output)
        else:
            batch_optimize_models(str(input_path), args.output)
    
    elif args.command == "assets":
        if args.icon:
            create_android_icon()
        if args.splash:
            create_splash_screen()
    
    elif args.command == "test":
        test_mobile_app()
    
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
