"""
增强版模型下载工具
==================
支持多个下载源和断点续传
"""

import requests
from pathlib import Path
from typing import Optional, List, Dict
import os
import sys

# 导入模型源配置
from tools.model_sources import DOWNLOAD_SOURCES, PRETRAINED_MODELS, MODEL_PACKS, HELP_TEXT


class ModelDownloader:
    """
    模型下载器
    ==========
    支持多源下载和断点续传
    """
    
    def __init__(self, output_dir: str = "models"):
        """
        初始化下载器
        
        Args:
            output_dir: 输出目录
        """
        self.output_dir = Path(output_dir)
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        })
    
    def download_from_url(
        self,
        url: str,
        dest_path: str,
        show_progress: bool = True
    ) -> bool:
        """
        从 URL 下载文件
        
        Args:
            url: 下载链接
            dest_path: 目标路径
            show_progress: 是否显示进度条
            
        Returns:
            是否成功
        """
        dest = Path(dest_path)
        dest.parent.mkdir(parents=True, exist_ok=True)
        
        try:
            # 检查已存在的部分下载文件
            resume_header = {}
            start_pos = 0
            
            if dest.exists():
                start_pos = dest.stat().st_size
                resume_header = {'Range': f'bytes={start_pos}-'}
                print(f"发现未完成的文件，从 {start_pos} 字节继续下载")
            
            response = self.session.get(
                url,
                headers=resume_header,
                stream=True
            )
            response.raise_for_status()
            
            # 获取文件大小
            total_size = int(response.headers.get('content-length', 0))
            
            if total_size == 0:
                total_size = start_pos + 1024 * 1024 * 80  # 估计大小
            
            # 写入文件
            with open(dest, 'ab') as f:
                if show_progress:
                    from tqdm import tqdm
                    with tqdm(
                        total=total_size,
                        initial=start_pos,
                        unit='B',
                        unit_scale=True,
                        unit_divisor=1024,
                        desc=dest.name,
                        ncols=100
                    ) as pbar:
                        for chunk in response.iter_content(chunk_size=8192):
                            if chunk:
                                f.write(chunk)
                                pbar.update(len(chunk))
                else:
                    for chunk in response.iter_content(chunk_size=8192):
                        if chunk:
                            f.write(chunk)
            
            print(f"\n✓ 下载完成：{dest}")
            return True
            
        except Exception as e:
            print(f"\n✗ 下载失败：{str(e)}")
            return False
    
    def download_preset_model(
        self,
        model_name: str,
        use_mirror: bool = True
    ) -> bool:
        """
        下载预定义模型
        
        Args:
            model_name: 模型名称（如 "female/yujie"）
            use_mirror: 是否使用国内镜像
            
        Returns:
            是否成功
        """
        if model_name not in PRETRAINED_MODELS:
            print(f"错误：未找到模型 '{model_name}'")
            self.list_models()
            return False
        
        model_info = PRETRAINED_MODELS[model_name]
        
        # 选择下载源
        url = model_info["download_url"]
        if use_mirror and "huggingface" in url:
            # 使用镜像加速
            mirror_url = url.replace(
                "huggingface.co/datasets",
                "hf-mirror.com/datasets"
            )
            print(f"使用镜像源：{mirror_url}")
            url = mirror_url
        
        # 目标路径
        dest_file = self.output_dir / f"{model_name}.pth"
        
        print(f"\n下载模型：{model_info['name']}")
        print(f"描述：{model_info['description']}")
        print(f"大小：{model_info.get('file_size', '未知')}")
        print(f"目标：{dest_file}")
        
        success = self.download_from_url(url, str(dest_file))
        
        if not success and model_info.get("backup_urls"):
            print(f"\n主下载源失败，尝试备用源...")
            for backup in model_info["backup_urls"]:
                if isinstance(backup, str) and backup.startswith("http"):
                    print(f"备用源：{backup}")
                    if self.download_from_url(backup, str(dest_file), show_progress=True):
                        return True
        
        return success
    
    def download_model_pack(
        self,
        pack_name: str
    ) -> bool:
        """
        下载模型合集包
        
        Args:
            pack_name: 合集包名称
            
        Returns:
            是否成功
        """
        if pack_name not in MODEL_PACKS:
            print(f"错误：未找到合集包 '{pack_name}'")
            self.list_packs()
            return False
        
        pack_info = MODEL_PACKS[pack_name]
        
        # 下载 ZIP 包
        print(f"\n下载合集包：{pack_name}")
        print(f"大小：{pack_info['size']}")
        print(f"描述：{pack_info['description']}")
        
        zip_path = self.output_dir / f"{pack_name.replace(' ', '_')}.zip"
        success = self.download_from_url(pack_info["url"], str(zip_path))
        
        if success:
            print(f"\n✓ 合集包下载完成：{zip_path}")
            print(f"请手动解压到 models/ 目录")
        
        return success
    
    def list_models(self, category: Optional[str] = None):
        """列出可用模型"""
        print("\n" + "=" * 70)
        print("可用预训练模型列表")
        print("=" * 70)
        
        if category:
            models = {
                k: v for k, v in PRETRAINED_MODELS.items()
                if v["category"] == category
            }
        else:
            models = PRETRAINED_MODELS
        
        current_category = None
        for model_name, info in models.items():
            if info["category"] != current_category:
                current_category = info["category"]
                print(f"\n【{current_category}】")
            
            flag = "⭐" if info.get("recommended") else "  "
            print(
                f"{flag} {model_name:25} - {info['name']:15} "
                f"({info.get('file_size', '?')})"
            )
            print(f"      {info['description']}")
        
        print()
    
    def list_packs(self):
        """列出合集包"""
        print("\n" + "=" * 70)
        print("模型合集包")
        print("=" * 70)
        
        for pack_name, info in MODEL_PACKS.items():
            print(f"\n{pack_name}")
            print(f"  大小：{info['size']}")
            print(f"  描述：{info['description']}")
            print(f"  链接：{info['url']}")
        
        print()
    
    def list_sources(self):
        """列出下载源"""
        print("\n" + "=" * 70)
        print("可用下载源")
        print("=" * 70)
        
        for source_name, info in DOWNLOAD_SOURCES.items():
            flag = "⭐" if info.get("recommended") else "  "
            print(
                f"{flag} {source_name:20} - {info['speed']:8} "
                f"({info['description']})"
            )
            print(f"      {info['url']}")
            if info.get('password'):
                print(f"      提取码：{info['password']}")
        
        print()
    
    def show_help(self):
        """显示帮助信息"""
        print(HELP_TEXT)


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="增强版模型下载工具",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python download_models.py --list                      # 列出所有模型
  python download_models.py -m female/yujie            # 下载单个模型
  python download_models.py --list-packs               # 列出合集包
  python download_models.py -p 基础包                   # 下载合集包
  python download_models.py --sources                  # 列出下载源
        """
    )
    
    parser.add_argument(
        "-m", "--model",
        type=str,
        help="要下载的模型名称"
    )
    
    parser.add_argument(
        "-p", "--pack",
        type=str,
        help="要下载的合集包名称"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        default="models",
        help="输出目录（默认：models）"
    )
    
    parser.add_argument(
        "--list",
        action="store_true",
        help="列出所有可用模型"
    )
    
    parser.add_argument(
        "--list-packs",
        action="store_true",
        help="列出合集包"
    )
    
    parser.add_argument(
        "--list-sources",
        action="store_true",
        help="列出下载源"
    )
    
    parser.add_argument(
        "--help-info",
        action="store_true",
        help="显示帮助信息"
    )
    
    parser.add_argument(
        "--no-mirror",
        action="store_true",
        help="不使用国内镜像加速"
    )
    
    parser.add_argument(
        "-a", "--all",
        action="store_true",
        help="下载所有推荐模型"
    )
    
    args = parser.parse_args()
    
    # 创建下载器
    downloader = ModelDownloader(args.output)
    
    # 列出模型
    if args.list:
        downloader.list_models()
        return
    
    # 列出合集包
    if args.list_packs:
        downloader.list_packs()
        return
    
    # 列出下载源
    if args.list_sources:
        downloader.list_sources()
        return
    
    # 显示帮助
    if args.help_info:
        downloader.show_help()
        return
    
    # 下载合集包
    if args.pack:
        downloader.download_model_pack(args.pack)
        return
    
    # 下载单个模型
    if args.model:
        downloader.download_preset_model(args.model, use_mirror=not args.no_mirror)
        return
    
    # 下载所有推荐模型
    if args.all:
        recommended = [
            k for k, v in PRETRAINED_MODELS.items()
            if v.get("recommended")
        ]
        print(f"将下载 {len(recommended)} 个推荐模型:")
        for model in recommended:
            print(f"  - {model}")
        print()
        
        success_count = 0
        for model in recommended:
            if downloader.download_preset_model(model, use_mirror=not args.no_mirror):
                success_count += 1
        
        print(f"\n完成：{success_count}/{len(recommended)} 个模型成功")
        return
    
    # 没有参数时显示帮助
    parser.print_help()


if __name__ == "__main__":
    main()
