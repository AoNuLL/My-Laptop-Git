"""
模型融合工具
==============
融合多个 RVC 模型，创造独特音色
"""

import torch
import argparse
from pathlib import Path
from typing import List, Dict, Optional


def merge_models(
    model_paths: List[str],
    weights: List[float],
    output_path: str
) -> bool:
    """
    融合多个模型
    
    Args:
        model_paths: 模型文件路径列表
        weights: 对应模型的融合权重
        output_path: 输出路径
        
    Returns:
        是否成功
    """
    if len(model_paths) != len(weights):
        print("错误：模型数量必须与权重数量匹配")
        return False
    
    # 权重归一化
    total_weight = sum(weights)
    weights = [w / total_weight for w in weights]
    
    print(f"融合 {len(model_paths)} 个模型:")
    for path, weight in zip(model_paths, weights):
        print(f"  - {path}: {weight:.2%}")
    
    try:
        # 加载模型
        checkpoints = []
        for path in model_paths:
            checkpoint = torch.load(path, map_location="cpu")
            checkpoints.append(checkpoint)
        
        # 融合模型权重
        merged_state_dict = {}
        
        # 获取第一个模型的键
        keys = checkpoints[0]["state_dict"].keys()
        
        for key in keys:
            # 收集所有模型中对应位置的权重
            tensors = [cp["state_dict"][key] for cp in checkpoints]
            
            # 加权平均
            merged = sum(t * w for t, w in zip(tensors, weights))
            merged_state_dict[key] = merged
        
        # 创建合并后的检查点
        merged_checkpoint = {
            "state_dict": merged_state_dict,
            "config": checkpoints[0]["config"],
            "info": {
                "merged_models": model_paths,
                "weights": weights,
                "merge_type": "weighted_average"
            }
        }
        
        # 保存
        torch.save(merged_checkpoint, output_path)
        print(f"\n✓ 模型融合完成：{output_path}")
        
        return True
        
    except Exception as e:
        print(f"\n✗ 融合失败：{str(e)}")
        return False


def merge_two_models(
    model1_path: str,
    model2_path: str,
    alpha: float,
    output_path: str
) -> bool:
    """
    融合两个模型（简化版本）
    
    Args:
        model1_path: 第一个模型路径
        model2_path: 第二个模型路径
        alpha: 融合比例 (0=完全模型 1, 1=完全模型 2)
        output_path: 输出路径
        
    Returns:
        是否成功
    """
    return merge_models(
        [model1_path, model2_path],
        [1 - alpha, alpha],
        output_path
    )


def interpolate_models(
    model_paths: List[str],
    output_dir: str,
    num_steps: int = 5
):
    """
    在多个模型之间插值，生成一系列融合模型
    
    Args:
        model_paths: 模型路径列表
        output_dir: 输出目录
        num_steps: 插值步数
    """
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)
    
    if len(model_paths) != 2:
        print("错误：目前只支持两个模型之间的插值")
        return
    
    path1, path2 = model_paths
    
    print(f"在以下模型之间插值:")
    print(f"  模型 1: {path1}")
    print(f"  模型 2: {path2}")
    print(f"  步数：{num_steps}")
    print()
    
    for i in range(num_steps):
        alpha = i / (num_steps - 1)
        
        output_file = output_path / f"interpolated_{alpha:.2f}.pth"
        
        success = merge_two_models(
            str(path1),
            str(path2),
            alpha,
            str(output_file)
        )
        
        if success:
            print(f"✓ 生成：{output_file} (alpha={alpha:.2f})")
        else:
            print(f"✗ 失败：{i+1}/{num_steps}")
    
    print(f"\n完成：生成 {num_steps} 个插值模型")


def analyze_model(model_path: str) -> Dict:
    """
    分析模型信息
    
    Args:
        model_path: 模型路径
        
    Returns:
        模型信息字典
    """
    checkpoint = torch.load(model_path, map_location="cpu")
    
    info = {}
    
    # 基本信息
    if "config" in checkpoint:
        info["config"] = checkpoint["config"]
    
    if "info" in checkpoint:
        info["info"] = checkpoint["info"]
    
    # 统计参数量
    if "state_dict" in checkpoint:
        num_params = sum(
            p.numel() for p in checkpoint["state_dict"].values()
        )
        info["num_parameters"] = num_params
        info["num_parameters_mb"] = f"{num_params / 1e6:.2f}M"
    
    return info


def compare_models(model1_path: str, model2_path: str):
    """
    比较两个模型
    
    Args:
        model1_path: 模型 1 路径
        model2_path: 模型 2 路径
    """
    info1 = analyze_model(model1_path)
    info2 = analyze_model(model2_path)
    
    print("模型比较:")
    print("=" * 60)
    print(f"{'属性':<20} {'模型 1':<20} {'模型 2':<20}")
    print("=" * 60)
    
    # 比较参数量
    if "num_parameters" in info1 and "num_parameters" in info2:
        print(f"{'参数量':<20} {info1['num_parameters_mb']:<20} {info2['num_parameters_mb']:<20}")
    
    # 比较其他属性
    all_keys = set(info1.keys()) | set(info2.keys())
    for key in all_keys:
        if key not in ["num_parameters", "num_parameters_mb"]:
            val1 = str(info1.get(key, "N/A"))[:20]
            val2 = str(info2.get(key, "N/A"))[:20]
            print(f"{key:<20} {val1:<20} {val2:<20}")
    
    print("=" * 60)


def main():
    """命令行入口"""
    parser = argparse.ArgumentParser(
        description="RVC 模型融合工具"
    )
    
    subparsers = parser.add_subparsers(dest="command", help="命令")
    
    # 融合命令
    merge_parser = subparsers.add_parser("merge", help="融合多个模型")
    merge_parser.add_argument(
        "-m", "--models",
        type=str,
        nargs="+",
        required=True,
        help="模型文件路径列表"
    )
    merge_parser.add_argument(
        "-w", "--weights",
        type=float,
        nargs="+",
        required=True,
        help="融合权重（与 models 数量相同）"
    )
    merge_parser.add_argument(
        "-o", "--output",
        type=str,
        required=True,
        help="输出文件路径"
    )
    
    # 插值命令
    interp_parser = subparsers.add_parser("interpolate", help="模型插值")
    interp_parser.add_argument(
        "-m", "--models",
        type=str,
        nargs=2,
        required=True,
        help="两个模型路径"
    )
    interp_parser.add_argument(
        "-o", "--output",
        type=str,
        required=True,
        help="输出目录"
    )
    interp_parser.add_argument(
        "-n", "--steps",
        type=int,
        default=5,
        help="插值步数"
    )
    
    # 分析命令
    analyze_parser = subparsers.add_parser("analyze", help="分析模型")
    analyze_parser.add_argument(
        "model",
        type=str,
        help="模型文件路径"
    )
    
    # 比较命令
    compare_parser = subparsers.add_parser("compare", help="比较模型")
    compare_parser.add_argument(
        "model1",
        type=str,
        help="模型 1 路径"
    )
    compare_parser.add_argument(
        "model2",
        type=str,
        help="模型 2 路径"
    )
    
    args = parser.parse_args()
    
    if args.command == "merge":
        merge_models(args.models, args.weights, args.output)
    
    elif args.command == "interpolate":
        interpolate_models(args.models, args.output, args.steps)
    
    elif args.command == "analyze":
        info = analyze_model(args.model)
        print(f"模型信息:")
        print(f"  参数量：{info.get('num_parameters_mb', 'N/A')}")
        for key, value in info.items():
            if key not in ["num_parameters", "num_parameters_mb"]:
                print(f"  {key}: {value}")
    
    elif args.command == "compare":
        compare_models(args.model1, args.model2)
    
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
