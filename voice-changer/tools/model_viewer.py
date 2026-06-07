"""
模型信息查看器
================
查看和分析 RVC 模型的详细信息
"""

import torch
import json
from pathlib import Path
from typing import Dict, Any, Optional
import numpy as np


class ModelViewer:
    """
    模型信息查看器
    ==============
    """
    
    def __init__(self, model_path: str):
        """
        初始化查看器
        
        Args:
            model_path: 模型文件路径
        """
        self.model_path = Path(model_path)
        self.checkpoint = None
        self.info = {}
    
    def load(self) -> bool:
        """
        加载模型
        
        Returns:
            是否成功
        """
        try:
            self.checkpoint = torch.load(
                str(self.model_path),
                map_location="cpu"
            )
            return True
        except Exception as e:
            print(f"加载模型失败：{str(e)}")
            return False
    
    def analyze(self) -> Dict[str, Any]:
        """
        分析模型
        
        Returns:
            模型信息字典
        """
        if self.checkpoint is None:
            if not self.load():
                return {}
        
        info = {
            "file": str(self.model_path),
            "file_size": self._get_file_size(),
            "type": self._detect_type(),
            "versions": self._get_versions(),
            "parameters": self._count_parameters(),
            "config": self._get_config(),
            "statistics": self._get_statistics()
        }
        
        self.info = info
        return info
    
    def _get_file_size(self) -> str:
        """获取文件大小"""
        size = self.model_path.stat().st_size
        if size < 1024:
            return f"{size} B"
        elif size < 1024**2:
            return f"{size/1024:.1f} KB"
        elif size < 1024**3:
            return f"{size/1024**2:.1f} MB"
        else:
            return f"{size/1024**3:.1f} GB"
    
    def _detect_type(self) -> str:
        """检测模型类型"""
        if "config" in self.checkpoint:
            return "RVC v1"
        elif "hubert" in self.checkpoint:
            return "RVC v2"
        else:
            return "Unknown"
    
    def _get_versions(self) -> Dict:
        """获取版本信息"""
        versions = {}
        
        if "config" in self.checkpoint:
            config = self.checkpoint["config"]
            versions["rvc"] = "v1"
            versions["python"] = config.get("version", "Unknown")
        
        if "info" in self.checkpoint:
            info = self.checkpoint["info"]
            if isinstance(info, dict):
                versions["name"] = info.get("name", "Unknown")
                versions["description"] = info.get("description", "")
        
        return versions
    
    def _count_parameters(self) -> Dict:
        """计算参数数量"""
        param_count = 0
        
        if "state_dict" in self.checkpoint:
            state_dict = self.checkpoint["state_dict"]
            for name, tensor in state_dict.items():
                param_count += tensor.numel()
        
        return {
            "total": param_count,
            "millions": f"{param_count / 1e6:.2f}M"
        }
    
    def _get_config(self) -> Dict:
        """获取配置信息"""
        config = {}
        
        if "config" in self.checkpoint:
            config = self.checkpoint["config"]
        
        return {
            "sample_rate": config.get("sample_rate", 22050),
            "emb_channels": config.get("emb_channels", 256),
            "spk_embed_dim": config.get("spk_embed_dim", 256)
        }
    
    def _get_statistics(self) -> Dict:
        """获取统计信息"""
        stats = {}
        
        if "state_dict" in self.checkpoint:
            state_dict = self.checkpoint["state_dict"]
            
            weight_values = []
            for name, tensor in state_dict.items():
                if tensor.dtype.is_floating_point:
                    weight_values.append({
                        "name": name,
                        "mean": tensor.mean().item(),
                        "std": tensor.std().item(),
                        "min": tensor.min().item(),
                        "max": tensor.max().item()
                    })
            
            stats["num_tensors"] = len(weight_values)
            
            if weight_values:
                all_means = [w["mean"] for w in weight_values[:10]]  # 前 10 个
                stats["sample_means"] = all_means
        
        return stats
    
    def print_summary(self):
        """打印模型摘要"""
        if not self.info:
            self.analyze()
        
        print("\n" + "=" * 60)
        print("RVC 模型信息")
        print("=" * 60)
        print(f"文件：{self.info['file']}")
        print(f"大小：{self.info['file_size']}")
        print(f"类型：{self.info['type']}")
        print(f"参数量：{self.info['parameters']['millions']}")
        
        if "versions" in self.info:
            print("\n版本信息:")
            for key, value in self.info["versions"].items():
                if value:
                    print(f"  {key}: {value}")
        
        if "config" in self.info:
            print("\n配置:")
            for key, value in self.info["config"].items():
                print(f"  {key}: {value}")
        
        print("=" * 60)
    
    def compare(self, other_model_path: str) -> Dict:
        """
        比较两个模型
        
        Args:
            other_model_path: 另一个模型路径
            
        Returns:
            比较结果
        """
        other_viewer = ModelViewer(other_model_path)
        other_viewer.load()
        other_info = other_viewer.analyze()
        
        comparison = {
            "model1": self.info,
            "model2": other_info,
            "differences": {}
        }
        
        # 比较参数
        diff_params = (
            self.info["parameters"]["total"] -
            other_info["parameters"]["total"]
        )
        comparison["differences"]["param_diff"] = diff_params
        
        return comparison
    
    def export_info(self, output_path: str) -> bool:
        """
        导出模型信息
        
        Args:
            output_path: 输出路径
            
        Returns:
            是否成功
        """
        try:
            with open(output_path, 'w', encoding='utf-8') as f:
                json.dump(self.info, f, indent=2, ensure_ascii=False)
            return True
        except Exception as e:
            print(f"导出失败：{str(e)}")
            return False


def compare_multiple_models(model_paths: list) -> dict:
    """
    比较多个模型
    
    Args:
        model_paths: 模型路径列表
        
    Returns:
        比较结果
    """
    results = []
    
    for path in model_paths:
        viewer = ModelViewer(path)
        viewer.load()
        info = viewer.analyze()
        results.append({
            "path": path,
            "size": info.get("file_size"),
            "type": info.get("type"),
            "params": info.get("parameters", {}).get("millions")
        })
    
    return {
        "models": results,
        "count": len(results)
    }


def main():
    """命令行入口"""
    import argparse
    
    parser = argparse.ArgumentParser(
        description="RVC 模型信息查看器"
    )
    
    parser.add_argument(
        "models",
        type=str,
        nargs="+",
        help="模型文件路径"
    )
    
    parser.add_argument(
        "-c", "--compare",
        action="store_true",
        help="比较多个模型"
    )
    
    parser.add_argument(
        "-o", "--output",
        type=str,
        help="导出信息到文件"
    )
    
    args = parser.parse_args()
    
    if args.compare and len(args.models) > 1:
        # 比较模式
        results = compare_multiple_models(args.models)
        print("\n模型比较:")
        print("=" * 80)
        print(f"{'文件':<40} {'大小':<10} {'类型':<10} {'参数':<10}")
        print("=" * 80)
        for model in results["models"]:
            print(f"{model['path'][:40]:<40} {model['size']:<10} "
                  f"{model['type']:<10} {model['params']:<10}")
    else:
        # 查看单个模型
        for model_path in args.models:
            viewer = ModelViewer(model_path)
            viewer.load()
            viewer.analyze()
            viewer.print_summary()
            
            if args.output:
                viewer.export_info(args.output)
                print(f"信息已导出：{args.output}")


if __name__ == "__main__":
    main()
