"""
更新检查器
============
检查并提示新版本
"""

import requests
import json
from pathlib import Path
from typing import Optional, Dict
from datetime import datetime


class UpdateChecker:
    """
    更新检查器
    ============
    """
    
    # 默认配置
    DEFAULT_REPO = "yourusername/voice-changer"
    CURRENT_VERSION = "v2.1.4"
    
    def __init__(
        self,
        repo: str = DEFAULT_REPO,
        current_version: str = CURRENT_VERSION
    ):
        """
        初始化检查器
        
        Args:
            repo: GitHub 仓库
            current_version: 当前版本
        """
        self.repo = repo
        self.current_version = current_version
        self.api_url = f"https://api.github.com/repos/{repo}/releases"
    
    def get_latest_release(self) -> Optional[Dict]:
        """
        获取最新版本
        
        Returns:
            版本信息字典
        """
        try:
            response = requests.get(self.api_url, timeout=10)
            response.raise_for_status()
            
            releases = response.json()
            
            # 获取最新版本 (第一个)
            if releases:
                latest = releases[0]
                return {
                    "tag_name": latest["tag_name"],
                    "name": latest["name"],
                    "published_at": latest["published_at"],
                    "body": latest["body"],
                    "html_url": latest["html_url"],
                    "assets": latest.get("assets", [])
                }
        except Exception as e:
            print(f"检查更新失败：{str(e)}")
        
        return None
    
    def check_for_updates(self) -> Dict:
        """
        检查更新
        
        Returns:
            检查结果
        """
        latest = self.get_latest_release()
        
        if not latest:
            return {
                "has_update": False,
                "error": "无法获取版本信息"
            }
        
        # 版本比较
        is_newer = self._compare_versions(
            latest["tag_name"],
            self.current_version
        )
        
        return {
            "has_update": is_newer,
            "current_version": self.current_version,
            "latest_version": latest["tag_name"],
            "release_name": latest["name"],
            "published_at": latest["published_at"],
            "changelog": latest["body"],
            "download_url": latest["html_url"],
            "assets": latest["assets"]
        }
    
    def _compare_versions(self, v1: str, v2: str) -> bool:
        """
        比较版本号
        
        Args:
            v1: 版本 1
            v2: 版本 2
            
        Returns:
            v1 是否大于 v2
        """
        # 去除 'v' 前缀
        v1 = v1.lstrip('v')
        v2 = v2.lstrip('v')
        
        # 分割版本号
        parts1 = [int(x) for x in v1.split('.')]
        parts2 = [int(x) for x in v2.split('.')]
        
        # 补齐长度
        max_len = max(len(parts1), len(parts2))
        parts1.extend([0] * (max_len - len(parts1)))
        parts2.extend([0] * (max_len - len(parts2)))
        
        # 比较
        return parts1 > parts2
    
    def download_asset(
        self,
        asset_name: str,
        output_dir: str = "."
    ) -> Optional[str]:
        """
        下载更新文件
        
        Args:
            asset_name: 文件名
            output_dir: 输出目录
            
        Returns:
            下载的文件路径
        """
        latest = self.get_latest_release()
        
        if not latest:
            return None
        
        # 查找文件
        asset_url = None
        for asset in latest["assets"]:
            if asset["name"] == asset_name:
                asset_url = asset["browser_download_url"]
                break
        
        if not asset_url:
            print(f"未找到文件：{asset_name}")
            return None
        
        # 下载
        try:
            print(f"下载：{asset_name}")
            response = requests.get(asset_url, stream=True, timeout=30)
            response.raise_for_status()
            
            output_path = Path(output_dir) / asset_name
            output_path.parent.mkdir(parents=True, exist_ok=True)
            
            with open(output_path, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    if chunk:
                        f.write(chunk)
            
            print(f"✓ 下载完成：{output_path}")
            return str(output_path)
            
        except Exception as e:
            print(f"下载失败：{str(e)}")
            return None
    
    def print_update_info(self):
        """打印更新信息"""
        result = self.check_for_updates()
        
        print("\n" + "=" * 60)
        print("更新检查")
        print("=" * 60)
        
        if "error" in result:
            print(f"错误：{result['error']}")
        elif result["has_update"]:
            print(f"✓ 发现新版本!")
            print(f"  当前版本：{result['current_version']}")
            print(f"  最新版本：{result['latest_version']}")
            print(f"  发布日期：{result['published_at']}")
            print(f"\n 更新内容:")
            print(result["changelog"][:500] + "...")
            print(f"\n 下载地址：{result['download_url']}")
        else:
            print("✓ 已是最新版本")
            print(f"  当前版本：{result['current_version']}")
        
        print("=" * 60)


def check_updates_cli():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="检查更新"
    )
    
    parser.add_argument(
        "-c", "--check",
        action="store_true",
        help="检查更新"
    )
    
    parser.add_argument(
        "-d", "--download",
        type=str,
        help="下载指定文件"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        default=".",
        help="下载目录"
    )
    
    args = parser.parse_args()
    
    checker = UpdateChecker()
    
    if args.download:
        checker.download_asset(args.download, args.output)
    else:
        checker.print_update_info()


if __name__ == "__main__":
    check_updates_cli()
