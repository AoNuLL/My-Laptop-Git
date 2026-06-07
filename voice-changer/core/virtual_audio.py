"""
虚拟音频驱动模块
==================
负责安装和管理虚拟音频设备
"""

import os
import sys
import subprocess
from pathlib import Path


class VirtualAudioDriver:
    """
    虚拟音频驱动管理器
    ===================
    
    支持多个平台的虚拟音频设备:
    - Windows: VB-Audio Virtual Cable
    - macOS: BlackHole
    - Linux: PulseAudio 虚拟设备
    """
    
    def __init__(self):
        """初始化驱动管理器"""
        self.platform = sys.platform
        self.is_installed = self.check_installation()
    
    def check_installation(self) -> bool:
        """
        检查虚拟音频驱动是否已安装
        
        Returns:
            是否已安装
        """
        if self.platform == "win32":
            # Windows: 检查 VB-Audio 驱动
            return self._check_windows_driver()
        
        elif self.platform == "darwin":
            # macOS: 检查 BlackHole
            return self._check_macos_driver()
        
        else:
            # Linux: 检查 PulseAudio 虚拟设备
            return self._check_linux_driver()
    
    def _check_windows_driver(self) -> bool:
        """
        检查 Windows VB-Audio 驱动
        
        Returns:
            是否已安装
        """
        try:
            import pyaudio
            pa = pyaudio.PyAudio()
            
            # 遍历所有设备，查找 VB-Audio
            for i in range(pa.get_device_count()):
                device_info = pa.get_device_info_by_index(i)
                device_name = device_info.get('name', '').lower()
                
                if 'vb-audio' in device_name or 'cable' in device_name:
                    pa.terminate()
                    return True
            
            pa.terminate()
            return False
            
        except Exception:
            return False
    
    def _check_macos_driver(self) -> bool:
        """
        检查 macOS BlackHole 驱动
        
        Returns:
            是否已安装
        """
        try:
            # 检查 BlackHole 内核扩展
            result = subprocess.run(
                ['kextstat', '-b', 'audio.blackhole'],
                capture_output=True,
                text=True
            )
            return result.returncode == 0
            
        except Exception:
            return False
    
    def _check_linux_driver(self) -> bool:
        """
        检查 Linux PulseAudio 虚拟设备
        
        Returns:
            是否已安装
        """
        try:
            # 检查 PulseAudio 模块
            result = subprocess.run(
                ['pactl', 'list', 'modules'],
                capture_output=True,
                text=True
            )
            return 'module-null-sink' in result.stdout
            
        except Exception:
            return False
    
    def install(self) -> bool:
        """
        安装虚拟音频驱动
        
        Returns:
            是否成功安装
        """
        if self.platform == "win32":
            return self._install_windows()
        
        elif self.platform == "darwin":
            return self._install_macos()
        
        else:
            return self._install_linux()
    
    def _install_windows(self) -> bool:
        """
        安装 Windows VB-Audio 驱动
        
        Returns:
            是否成功
        """
        print("Windows 平台虚拟音频驱动安装指引")
        print("=" * 60)
        print("请手动下载并安装 VB-Audio Virtual Cable:")
        print("1. 访问：https://vb-audio.com/Cable/")
        print("2. 下载 'VB-Audio Virtual Cable' 安装包")
        print("3. 以管理员身份运行安装程序")
        print("4. 按照安装向导完成安装")
        print("5. 重启电脑")
        print("=" * 60)
        
        # 自动打开下载页面
        import webbrowser
        webbrowser.open("https://vb-audio.com/Cable/")
        
        return True
    
    def _install_macos(self) -> bool:
        """
        安装 macOS BlackHole 驱动
        
        Returns:
            是否成功
        """
        print("macOS 平台虚拟音频驱动安装指引")
        print("=" * 60)
        print("请安装 BlackHole:")
        print("1. 访问：https://existential.audio/blackhole/")
        print("2. 下载 BlackHole 安装包 (推荐 2ch 版本)")
        print("3. 打开 .dmg 文件并运行安装器")
        print("4. 授予必要的权限")
        print("5. 重启电脑")
        print("=" * 60)
        
        import webbrowser
        webbrowser.open("https://existential.audio/blackhole/")
        
        return True
    
    def _install_linux(self) -> bool:
        """
        安装 Linux PulseAudio 虚拟设备
        
        Returns:
            是否成功
        """
        print("Linux 平台：尝试自动安装 PulseAudio 虚拟设备...")
        
        try:
            # 创建 PulseAudio 虚拟设备
            subprocess.run(
                ['pactl', 'load-module', 'module-null-sink', 'sink_name=vc_output'],
                check=True,
                capture_output=True
            )
            
            print("\n✓ 虚拟音频设备创建成功")
            print("\n配置说明:")
            print("  - 输入设备：Monitor of vc_output")
            print("  - 输出设备：vc_output")
            print("\n注意：此设置在重启后失效")
            print("如需永久生效，请将以下内容添加到 ~/.pulse/default.pa:")
            print("  load-module module-null-sink sink_name=vc_output")
            
            return True
            
        except subprocess.CalledProcessError as e:
            print(f"\n✗ 安装失败：{str(e)}")
            print("\n请手动配置 PulseAudio:")
            print("1. 编辑 ~/.pulse/default.pa")
            print("2. 添加：load-module module-null-sink sink_name=vc_output")
            print("3. 重启 PulseAudio: pulseaudio -k && pulseaudio --start")
            
            return False
    
    def uninstall(self) -> bool:
        """
        卸载虚拟音频驱动
        
        Returns:
            是否成功卸载
        """
        if self.platform == "linux":
            try:
                # 卸载 PulseAudio 虚拟设备
                subprocess.run(
                    ['pactl', 'unload-module', 'module-null-sink'],
                    check=True,
                    capture_output=True
                )
                print("✓ 虚拟音频设备已卸载")
                return True
                
            except subprocess.CalledProcessError:
                print("✗ 卸载失败或设备不存在")
                return False
        
        else:
            print("Windows/macOS 平台请手动卸载虚拟音频驱动")
            return False
    
    def get_device_info(self) -> dict:
        """
        获取虚拟音频设备信息
        
        Returns:
            设备信息字典
        """
        import pyaudio
        
        pa = pyaudio.PyAudio()
        device_info = {
            'input': None,
            'output': None
        }
        
        try:
            for i in range(pa.get_device_count()):
                info = pa.get_device_info_by_index(i)
                name = info.get('name', '').lower()
                
                if self.platform == "win32":
                    if 'vb-audio' in name or 'cable' in name:
                        if info.get('maxInputChannels') > 0:
                            device_info['input'] = {
                                'index': i,
                                'name': info.get('name'),
                                'channels': info.get('maxInputChannels')
                            }
                        if info.get('maxOutputChannels') > 0:
                            device_info['output'] = {
                                'index': i,
                                'name': info.get('name'),
                                'channels': info.get('maxOutputChannels')
                            }
                
                elif self.platform == "darwin":
                    if 'blackhole' in name:
                        if info.get('maxInputChannels') > 0:
                            device_info['input'] = {
                                'index': i,
                                'name': info.get('name'),
                                'channels': info.get('maxInputChannels')
                            }
                        if info.get('maxOutputChannels') > 0:
                            device_info['output'] = {
                                'index': i,
                                'name': info.get('name'),
                                'channels': info.get('maxOutputChannels')
                            }
                
                else:  # Linux
                    if 'null' in name or 'vc_output' in name:
                        device_info['output'] = {
                            'index': i,
                            'name': info.get('name'),
                            'channels': info.get('maxOutputChannels')
                        }
            
        except Exception as e:
            print(f"获取设备信息失败：{str(e)}")
        
        finally:
            pa.terminate()
        
        return device_info
