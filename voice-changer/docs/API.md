# API 文档

本文档介绍 Voice Changer 的编程接口。

## 目录

1. [核心类](#核心类)
2. [音频处理](#音频处理)
3. [模型推理](#模型推理)
4. [工具函数](#工具函数)
5. [使用示例](#使用示例)

---

## 核心类

### AudioProcessor

音频处理器，负责录制和播放。

```python
from core.audio_processor import AudioProcessor, AudioConfig

# 配置
config = AudioConfig(
    sample_rate=48000,
    channels=1,
    chunk_size=512
)

# 创建处理器
processor = AudioProcessor(config)

# 开始录制
def audio_callback(audio_data):
    # 处理音频
    return processed_audio

processor.start_recording(audio_callback)
processor.start_playback()

# 停止
processor.stop()
```

### RVCInference

RVC 推理引擎。

```python
from core.rvc_inference import RVCInference

# 创建引擎
engine = RVCInference(
    model_path="models/female/yujie.pth",
    device="cuda"  # 或 "cpu"
)

# 加载模型
engine.load_model()

# 变声
import numpy as np
audio_data = np.random.randn(48000).astype(np.float32)
converted = engine.convert(audio_data, pitch_shift=10)
```

### AudioVisualizer

音频可视化器。

```python
from tools.audio_visualizer import AudioVisualizer

viz = AudioVisualizer(sample_rate=48000)

# 获取波形
waveform = viz.compute_waveform(audio_data, num_points=256)

# 获取频谱
frequencies, spectrum = viz.compute_spectrum(audio_data)

# 获取多种特征
features = viz.get_features(audio_data, [
    'waveform', 'spectrum', 'rms', 'pitch'
])
```

### ConfigManager

配置管理器。

```python
from tools.config_manager import ConfigManager

config = ConfigManager()

# 读取
sample_rate = config.get('audio.sample_rate')

# 写入
config.set('audio.sample_rate', 44100)

# 验证
if config.validate():
    print("配置有效")

# 保存
config.save()
```

---

## 音频处理

### 录制音频到文件

```python
from core.audio_processor import AudioProcessor

processor = AudioProcessor()
processor.record_to_file("output.wav", duration=5.0)
```

### 获取设备列表

```python
from core.audio_processor import AudioProcessor

processor = AudioProcessor()

# 输入设备
input_devices = processor.get_input_devices()
for device in input_devices:
    print(f"{device['index']}: {device['name']}")

# 输出设备
output_devices = processor.get_output_devices()
```

### 实时音量监控

```python
from core.audio_processor import AudioProcessor

processor = AudioProcessor()
processor.start_recording(lambda x: x)

# 获取音量
while True:
    volume = processor.get_volume()
    print(f"音量：{volume:.2f}")
    time.sleep(0.1)
```

---

## 模型推理

### 批量转换

```python
from core.rvc_inference import RVCInference

engine = RVCInference("model.pth")
engine.load_model()

# 批量转换文件
success_count = engine.convert_batch(
    audio_files=["a.wav", "b.wav"],
    output_dir="output/",
    pitch_shift=0
)

print(f"成功：{success_count}")
```

### 设置音高提取方法

```python
engine.set_f0_method("harvest")  # pm, harvest, crepe
```

### ONNX 导出

```python
# TODO: 待实现
engine.to_onnx("model.onnx")
```

---

## 工具函数

### 下载模型

```python
from tools.download_models import download_model

# 下载单个模型
download_model("female/yujie", output_dir="models")

# 下载所有
from tools.download_models import download_all_models
download_all_models()
```

### 模型融合

```python
from tools.merge_models import merge_models

merge_models(
    model_paths=["model1.pth", "model2.pth"],
    weights=[0.7, 0.3],
    output_path="merged.pth"
)
```

### 模型信息查看

```python
from tools.model_viewer import ModelViewer

viewer = ModelViewer("model.pth")
viewer.load()

# 分析
info = viewer.analyze()
print(info)

# 打印摘要
viewer.print_summary()
```

### 配置管理

```python
from tools.config_manager import get_config

config = get_config()

# 获取所有配置
all_config = config.get_all()

# 导出
config.export("backup.json")

# 导入
config.import_config("backup.json")
```

---

## 使用示例

### 示例 1: 简单变声

```python
import numpy as np
from core.audio_processor import AudioProcessor
from core.rvc_inference import RVCInference

# 初始化
processor = AudioProcessor()
engine = RVCInference("model.pth")
engine.load_model()

def process_and_play(audio_data):
    # 变声
    converted = engine.convert(audio_data, pitch_shift=10)
    return converted

# 开始
processor.start_recording(process_and_play)
processor.start_playback()

# 运行 10 秒
import time
time.sleep(10)

# 停止
processor.stop()
```

### 示例 2: 批量文件转换

```python
from pathlib import Path
from core.rvc_inference import RVCInference
import soundfile as sf
import numpy as np

# 加载模型
engine = RVCInference("model.pth")
engine.load_model()

# 批量处理
input_dir = Path("input/")
output_dir = Path("output/")
output_dir.mkdir()

for audio_file in input_dir.glob("*.wav"):
    # 加载
    audio, sr = sf.read(str(audio_file))
    
    # 重采样
    if sr != 22050:
        import librosa
        audio = librosa.resample(audio, orig_sr=sr, target_sr=22050)
    
    # 变声
    converted = engine.convert(audio, pitch_shift=5)
    
    # 保存
    output_file = output_dir / audio_file.name
    sf.write(str(output_file), converted, 22050)
    
    print(f"处理完成：{audio_file.name}")
```

### 示例 3: 实时监控

```python
import customtkinter as ctk
from core.audio_processor import AudioProcessor
from tools.audio_visualizer import AudioVisualizer
import numpy as np

class MonitorApp:
    def __init__(self):
        self.root = ctk.CTk()
        self.processor = AudioProcessor()
        self.visualizer = AudioVisualizer()
        
        # UI
        self.progress = ctk.CTkProgressBar()
        self.progress.pack()
        
        # 开始
        self.processor.start_recording(self.on_audio)
        
        self.root.mainloop()
    
    def on_audio(self, audio_data):
        # 可视化
        waveform = self.visualizer.compute_waveform(audio_data)
        volume = np.sqrt(np.mean(audio_data ** 2))
        
        # 更新 UI
        self.progress.set(volume)
        
        return audio_data

app = MonitorApp()
```

### 示例 4: 自定义训练

```python
from tools.train_model import VoiceChangerTrainer

trainer = VoiceChangerTrainer(
    experiment_name="my_voice",
    features_dir="features/my_voice/",
    batch_size=16,
    learning_rate=0.0001,
    num_epochs=100
)

trainer.train()
```

### 示例 5: 模型分析和比较

```python
from tools.model_viewer import ModelViewer, compare_multiple_models

# 分析单个模型
viewer = ModelViewer("model1.pth")
viewer.load()
info = viewer.analyze()
viewer.print_summary()

# 比较多个模型
results = compare_multiple_models([
    "model1.pth",
    "model2.pth",
    "model3.pth"
])

print(f"比较了 {results['count']} 个模型")
```

---

## 环境变量

### 配置方式

```bash
# GPU 选择
export CUDA_VISIBLE_DEVICES=0

# 日志级别
export VOICE_CHANGER_LOG_LEVEL=DEBUG

# 模型目录
export VOICE_CHANGER_MODELS_DIR=/path/to/models
```

### 在代码中使用

```python
import os

models_dir = os.environ.get(
    "VOICE_CHANGER_MODELS_DIR",
    "models"
)
```

---

## 错误处理

```python
from core.rvc_inference import RVCInference

try:
    engine = RVCInference("model.pth")
    engine.load_model()
except FileNotFoundError:
    print("模型文件不存在")
except RuntimeError as e:
    print(f"加载失败：{e}")
except Exception as e:
    print(f"未知错误：{e}")
```

---

## 性能优化

### GPU 加速

```python
# 使用 GPU
engine = RVCInference(device="cuda")

# 混合精度推理
import torch
with torch.cuda.amp.autocast():
    converted = engine.convert(audio_data)
```

### 批量处理

```python
# 批量转换比逐个转换更快
engine.convert_batch(audio_files, output_dir)
```

### ONNX 推理（待实现）

```python
# ONNX Runtime 通常比 PyTorch 快
import onnxruntime as ort

session = ort.InferenceSession("model.onnx")
outputs = session.run(None, {"input": audio_data})
```

---

## 完整项目

查看项目代码获取更多示例：

- `gui/main_window.py` - GUI 实现
- `tools/batch_convert.py` - 批量转换
- `tools/train_model.py` - 模型训练

---

**版本**: v2.1.4  
**最后更新**: 2026-05-15
