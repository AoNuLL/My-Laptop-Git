# AI 模型下载与安装指南

## 📥 官方下载源

### 1. HuggingFace（推荐）

**主模型库**：
```
https://huggingface.co/lj1995/VoiceConversionWebUI
```

**直接下载链接**：
- 基础模型：https://huggingface.co/lj1995/VoiceConversionWebUI/tree/main/pretrained_v2
- 音效模型：https://huggingface.co/models?search=rvc+voice

**下载步骤**：
1. 访问 HuggingFace 模型库
2. 下载 `.pth` 或 `.onnx` 格式的模型文件
3. 将模型文件转换为 TFLite 格式（如需要）
4. 放入 `app/src/main/assets/models/` 目录

### 2. GitHub 官方仓库

**项目地址**：
```
https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI
```

**模型下载地址**：
- Releases: https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/releases
- Pretrained Models: https://huggingface.co/lj1995/VoiceConversionWebUI

### 3. 国内镜像（中国用户）

**ModelScope 魔搭**：
```
https://modelscope.cn/models?name=rvc
```

**百度网盘**（社区整理）：
- 链接：需自行搜索"RVC 模型 百度网盘"
- 提取码：通常在分享页面

---

## 🎯 推荐模型清单

### 基础变声模型

| 音效 | 文件大小 | 推荐下载 |
|------|---------|---------|
| 男变女 | ~50 MB | ✅ 必需 |
| 女变男 | ~50 MB | ✅ 必需 |
| 儿童音 | ~40 MB | ✅ 推荐 |
| 老人音 | ~40 MB | ✅ 推荐 |

### 特殊音效模型

| 音效 | 文件大小 | 用途 |
|------|---------|------|
| 机器人音 | ~30 MB | 机械音效 |
| 卡通音 | ~35 MB | 动画角色 |
| 怪兽音 | ~45 MB | 游戏音效 |
| 电台主播 | ~50 MB | 广播电台效果 |

---

## 📁 模型文件结构

### 预期目录结构

```
app/src/main/assets/
└── models/
    ├── effect_male_to_female.tflite    (男变女)
    ├── effect_female_to_male.tflite    (女变男)
    ├── effect_child.tflite             (儿童音)
    ├── effect_elderly.tflite           (老人音)
    ├── effect_robot.tflite             (机器人音)
    ├── effect_cartoon.tflite           (卡通音)
    ├── effect_monster.tflite           (怪兽音)
    ├── effect_radio_host.tflite        (电台主播音)
    └── model_metadata.json             (模型元数据)
```

### 模型元数据示例

创建 `model_metadata.json`：

```json
{
  "version": "1.0.0",
  "models": [
    {
      "id": "male_to_female",
      "name": "男变女",
      "file": "effect_male_to_female.tflite",
      "category": "gender_change",
      "description": "将男声转换为女声"
    },
    {
      "id": "female_to_male",
      "name": "女变男",
      "file": "effect_female_to_male.tflite",
      "category": "gender_change",
      "description": "将女声转换为男声"
    }
  ]
}
```

---

## 🔧 模型转换（如需要）

### 从 PyTorch 转 TFLite

如果下载的是 `.pth` 格式，需要转换为 TFLite：

```bash
# 1. 安装依赖
pip install onnx onnxruntime tensorflow

# 2. PyTorch 转 ONNX
python export_onnx.py --input model.pth --output model.onnx

# 3. ONNX 转 TFLite
python -m tf2onnx.convert --onnx-model model.onnx \
  --output model.tflite \
  --opset 13
```

### 使用官方转换工具

RVC 项目提供了转换脚本：

```bash
cd Retrieval-based-Voice-Conversion-WebUI
python tools/convert_to_onnx.py --model your_model.pth
```

---

## 📱 在应用中使用模型

### 步骤 1：放置模型文件

将下载的模型文件放入：
```
app/src/main/assets/models/
```

### 步骤 2：重新构建 APK

```bash
cd /workspace/AIVoiceChanger
./gradlew assembleDebug
```

### 步骤 3：安装测试

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 步骤 4：验证模型

1. 打开应用
2. 进入"设置" → "AI 模型管理"
3. 检查模型是否被识别
4. 尝试使用变声功能

---

## ⚠️ 注意事项

### 模型兼容性

- **格式要求**：TFLite (`.tflite`)
- **输入格式**：PCM/WAV 音频
- **采样率**：44.1kHz 推荐
- **位深**：16-bit

### 文件大小限制

- **单个模型**：建议 < 100 MB
- **总大小**：APK 限制 200 MB（建议单独下载）
- **内存占用**：每个模型推理约需 200-500 MB RAM

### 性能考虑

| 设备等级 | 推荐模型大小 | 推理时间 |
|---------|-------------|---------|
| 高端设备 | < 100 MB | < 3 秒 |
| 中端设备 | < 50 MB | < 5 秒 |
| 低端设备 | < 30 MB | < 10 秒 |

---

## 🔍 故障排查

### 问题 1：模型文件找不到

**症状**：应用提示"模型文件不存在"

**解决**：
```bash
# 检查文件位置
ls -la app/src/main/assets/models/

# 确认文件名正确
# 应该是 .tflite 扩展名
```

### 问题 2：模型加载失败

**症状**：应用崩溃或提示"模型加载失败"

**可能原因**：
- 模型格式不正确
- 模型文件损坏
- 内存不足

**解决**：
1. 重新下载模型
2. 检查模型格式（应为 TFLite）
3. 重启应用

### 问题 3：变声效果差

**症状**：变声后音质差或有杂音

**解决**：
1. 使用高质量模型（> 50 MB）
2. 确保输入音频质量良好
3. 调整变声参数（音调、音色）

---

## 📚 相关资源

### 官方文档

- RVC 官方文档：https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/wiki
- HuggingFace 使用指南：https://huggingface.co/docs

### 社区资源

- RVC 中文社区：https://huggingface.co/spaces/RVC-Community
- Discord 讨论组：https://discord.gg/rvc (英文)

### 教程

- B 站教程：搜索"RVC 变声器 教程"
- YouTube 教程：搜索"RVC AI voice conversion tutorial"

---

## 💡 应用内访问

在 AI 变声器应用中：

1. 打开应用
2. 进入"设置"页面
3. 找到"AI 模型管理"分类
4. 点击对应链接即可在浏览器中打开下载页面

**提供的链接**：
- 下载 AI 模型 → HuggingFace 模型库
- HuggingFace 模型库 → https://huggingface.co/lj1995/VoiceConversionWebUI
- GitHub 项目 → https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI

---

**最后更新**: 2026-06-07  
**文档版本**: 1.0.0
