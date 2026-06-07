"""
配置管理器
============
管理应用程序配置文件
"""

import json
import os
from pathlib import Path
from typing import Any, Dict, Optional
from datetime import datetime


class ConfigManager:
    """
    配置管理器
    ============
    管理应用程序的所有配置项
    """
    
    DEFAULT_CONFIG = {
        # 音频设置
        "audio": {
            "sample_rate": 48000,
            "channels": 1,
            "buffer_size": 512,
            "input_device": None,
            "output_device": None,
            "virtual_device": None
        },
        
        # 模型设置
        "model": {
            "models_dir": "models",
            "default_model": None,
            "auto_load_model": True,
            "use_gpu": True,
            "f0_method": "pm"
        },
        
        # 推理设置
        "inference": {
            "pitch_shift": 0,
            "volume_gain": 1.0,
            "noise_reduction": False,
            "optimization": "balanced"
        },
        
        # UI 设置
        "ui": {
            "theme": "dark",
            "language": "zh_CN",
            "refresh_rate": 100,
            "show_waveform": True,
            "show_spectrogram": False
        },
        
        # 其他设置
        "misc": {
            "auto_save_config": True,
            "log_level": "INFO",
            "check_updates": True
        }
    }
    
    def __init__(self, config_path: str = "config.json"):
        """
        初始化配置管理器
        
        Args:
            config_path: 配置文件路径
        """
        self.config_path = Path(config_path)
        self.config = self.DEFAULT_CONFIG.copy()
        
        # 加载现有配置
        if self.config_path.exists():
            self.load()
    
    def get(self, key: str, default: Any = None) -> Any:
        """
        获取配置值
        
        Args:
            key: 键 (支持点号分隔，如 "audio.sample_rate")
            default: 默认值
            
        Returns:
            配置值
        """
        keys = key.split(".")
        value = self.config
        
        try:
            for k in keys:
                value = value[k]
            return value
        except (KeyError, TypeError):
            return default
    
    def set(self, key: str, value: Any, save: bool = True):
        """
        设置配置值
        
        Args:
            key: 键 (支持点号分隔)
            value: 值
            save: 是否立即保存
        """
        keys = key.split(".")
        config = self.config
        
        # 导航到正确的层级
        for k in keys[:-1]:
            if k not in config:
                config[k] = {}
            config = config[k]
        
        # 设置值
        config[keys[-1]] = value
        
        # 自动保存
        if save:
            self.save()
    
    def load(self, path: Optional[str] = None) -> bool:
        """
        加载配置
        
        Args:
            path: 配置文件路径
            
        Returns:
            是否成功
        """
        path = path or self.config_path
        
        try:
            with open(path, 'r', encoding='utf-8') as f:
                loaded = json.load(f)
            
            # 合并配置
            self._merge_config(loaded)
            
            print(f"✓ 配置已加载：{path}")
            return True
            
        except Exception as e:
            print(f"✗ 加载配置失败：{str(e)}")
            return False
    
    def save(self, path: Optional[str] = None) -> bool:
        """
        保存配置
        
        Args:
            path: 配置文件路径
            
        Returns:
            是否成功
        """
        path = path or self.config_path
        
        try:
            # 创建目录
            path.parent.mkdir(parents=True, exist_ok=True)
            
            with open(path, 'w', encoding='utf-8') as f:
                json.dump(self.config, f, indent=2, ensure_ascii=False)
            
            print(f"✓ 配置已保存：{path}")
            return True
            
        except Exception as e:
            print(f"✗ 保存配置失败：{str(e)}")
            return False
    
    def reset(self):
        """重置为默认配置"""
        self.config = self.DEFAULT_CONFIG.copy()
        if self.config_path.exists():
            self.config_path.unlink()
        print("✓ 配置已重置为默认值")
    
    def _merge_config(self, loaded: Dict):
        """
        合并加载的配置
        
        Args:
            loaded: 加载的配置字典
        """
        for key, value in loaded.items():
            if isinstance(value, dict) and key in self.config:
                if isinstance(self.config[key], dict):
                    # 递归合并
                    self._merge_dict(self.config[key], value)
                else:
                    self.config[key] = value
            else:
                self.config[key] = value
    
    def _merge_dict(self, target: Dict, source: Dict):
        """递归合并字典"""
        for key, value in source.items():
            if isinstance(value, dict) and key in target:
                if isinstance(target[key], dict):
                    self._merge_dict(target[key], value)
                else:
                    target[key] = value
            else:
                target[key] = value
    
    def validate(self) -> bool:
        """
        验证配置
        
        Returns:
            是否有效
        """
        errors = []
        
        # 验证音频设置
        audio = self.config.get("audio", {})
        if audio.get("sample_rate") not in [22050, 44100, 48000, 96000]:
            errors.append("无效的采样率")
        
        if audio.get("buffer_size") not in [128, 256, 512, 1024, 2048, 4096]:
            errors.append("无效的缓冲区大小")
        
        # 验证模型设置
        model = self.config.get("model", {})
        f0_methods = ["pm", "harvest", "crepe"]
        if model.get("f0_method") not in f0_methods:
            errors.append(f"无效的音高提取方法，必须是 {f0_methods}")
        
        # 验证 UI 设置
        ui = self.config.get("ui", {})
        if ui.get("theme") not in ["dark", "light", "system"]:
            errors.append("无效的主题")
        
        if ui.get("language") not in ["zh_CN", "zh_TW", "en_US", "ja_JP"]:
            errors.append("无效的语言")
        
        if errors:
            for error in errors:
                print(f"✗ 配置验证错误：{error}")
            return False
        
        print("✓ 配置验证通过")
        return True
    
    def export(self, path: str) -> bool:
        """
        导出配置到文件
        
        Args:
            path: 导出路径
            
        Returns:
            是否成功
        """
        try:
            with open(path, 'w', encoding='utf-8') as f:
                json.dump(self.config, f, indent=2, ensure_ascii=False)
            return True
        except Exception as e:
            print(f"导出配置失败：{str(e)}")
            return False
    
    def import_config(self, path: str) -> bool:
        """
        从文件导入配置
        
        Args:
            path: 导入文件路径
            
        Returns:
            是否成功
        """
        try:
            with open(path, 'r', encoding='utf-8') as f:
                imported = json.load(f)
            
            self._merge_config(imported)
            self.save()
            print(f"✓ 配置已导入：{path}")
            return True
            
        except Exception as e:
            print(f"导入配置失败：{str(e)}")
            return False
    
    def get_all(self) -> Dict:
        """获取完整配置"""
        return self.config.copy()
    
    def set_all(self, config: Dict):
        """
        设置完整配置
        
        Args:
            config: 配置字典
        """
        self.config = config
        self.save()


# 全局配置实例
_global_config: Optional[ConfigManager] = None


def get_config() -> ConfigManager:
    """获取全局配置实例"""
    global _global_config
    if _global_config is None:
        _global_config = ConfigManager()
    return _global_config


if __name__ == "__main__":
    # 测试配置管理器
    config = ConfigManager()
    
    # 测试读写
    print(f"采样率：{config.get('audio.sample_rate')}")
    config.set('audio.sample_rate', 44100)
    print(f"新采样率：{config.get('audio.sample_rate')}")
    
    # 验证
    config.validate()
    
    # 保存
    config.save()
