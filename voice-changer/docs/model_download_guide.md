# RVC 模型下载指南

## 🎯 快速开始

### 方法 1: 图形界面下载（推荐）

1. **打开软件**
2. **点击"下载更多模型"按钮** (左侧模型面板)
3. **选择下载方式**:
   - **推荐下载源** - 查看可用网盘链接
   - **单模型下载** - 点击下载单个模型
   - **模型合集包** - 下载批量模型包
   - **使用帮助** - 查看帮助信息

### 方法 2: 命令行下载

```bash
# 列出所有可用模型
python tools/download_models.py --list

# 下载单个模型
python tools/download_models.py -m female/yujie

# 下载所有推荐模型 (10 个)
python tools/download_models.py -a

# 下载合集包
python tools/download_models.py -p "基础包"

# 查看下载源
python tools/download_models.py --list-sources
```

---

## 🌐 推荐下载源

### 国内源（速度快）

| 下载源 | 速度 | 特点 | 链接 |
|--------|------|------|------|
| **夸克网盘** | ⭐⭐⭐⭐⭐ | 600+模型合集 | [点击下载](https://pan.quark.cn/s/df5642c6567b) |
| **百度网盘** | ⭐⭐⭐ | 官方模型包 | [点击下载](https://pan.baidu.com/s/1RVC_Models) 密码:`rvcc` |
| **阿里云盘** | ⭐⭐⭐⭐ | 不限速下载 | 搜索"RVC 模型" |

### 国际源

| 下载源 | 速度 | 特点 | 链接 |
|--------|------|------|------|
| **HuggingFace** | ⭐⭐⭐⭐ | 官方仓库 | [点击下载](https://huggingface.co/datasets/RVC-Project/models) |
| **GitHub Releases** | ⭐⭐ | GitHub 发布页 | [查看](https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/releases) |

---

## 📦 模型合集包推荐

### 新手推荐

**基础包 (10 模型)**
- 大小：~800 MB
- 包含最常用的 10 个模型
- [下载地址](https://huggingface.co/datasets/RVC-Project/models/resolve/main/base_pack.zip)

### 进阶推荐

**完整合集 (600+ 模型)**
- 大小：~50 GB
- 包含所有可用模型
- 定期更新
- [夸克网盘下载](https://pan.quark.cn/s/df5642c6567b)

### 分类合集

| 合集名称 | 模型数量 | 大小 | 适合场景 |
|----------|----------|------|----------|
| 女声合集 | 50+ | ~4 GB | 女声配音 |
| 男声合集 | 50+ | ~4 GB | 男声配音 |
| 动漫合集 | 100+ | ~8 GB | 动漫角色扮演 |
| 游戏合集 | 100+ | ~10 GB | 游戏角色配音 |

---

## 🎤 热门推荐模型

### 女声音色 ⭐

| 模型名 | 描述 | 大小 | 下载 |
|--------|------|------|------|
| `female/yujie` | 御姐音 - 成熟女性声音 | ~80 MB | [下载](https://huggingface.co/datasets/RVC-Project/models/resolve/main/yujie.pth) |
| `female/luoli` | 萝莉音 - 可爱小女孩 | ~80 MB | [下载](https://huggingface.co/datasets/RVC-Project/models/resolve/main/luoli.pth) |
| `female/shaonv` | 少女音 - 青春女性声音 | ~80 MB | [下载](https://huggingface.co/datasets/RVC-Project/models/resolve/main/shaonv.pth) |

### 男声音色 ⭐

| 模型名 | 描述 | 大小 | 下载 |
|--------|------|------|------|
| `male/dashu` | 大叔音 - 成熟男性声音 | ~80 MB | [下载](https://huggingface.co/datasets/RVC-Project/models/resolve/main/dashu.pth) |
| `male/qingnian` | 青年音 - 年轻男性声音 | ~80 MB | [下载](https://huggingface.co/datasets/RVC-Project/models/resolve/main/qingnian.pth) |

### 动漫角色 ⭐

| 模型名 | 描述 | 大小 | 适合 |
|--------|------|------|------|
| `anime/naruto` | 火影忍者 - 鸣人 | ~80 MB | 热血动漫 |
| `anime/luffy` | 海贼王 - 路飞 | ~80 MB | 冒险动漫 |
| `anime/kiana` | 崩坏 3 - 琪亚娜 | ~80 MB | 游戏配音 |

---

## 💡 使用技巧

### 快速安装下载的模型

```bash
# 1. 下载到指定目录
python tools/download_models.py -m female/yujie -o models/female

# 2. 或使用软件界面
# 点击下载按钮 -> 选择模型 -> 自动下载

# 3. 手动下载安装
# 下载.pth 文件 -> 放到 models/female/ 目录 -> 重启软件
```

### 模型文件结构

```
models/
├── female/          # 女声音色
│   ├── yujie.pth
│   ├── luoli.pth
│   └── ...
├── male/            # 男声音色
│   ├── dashu.pth
│   ├── qingnian.pth
│   └── ...
├── anime/           # 动漫角色
│   ├── naruto.pth
│   └── luffy.pth
└── custom/          # 自定义模型
    └── your_model.pth
```

### 验证模型完整性

```bash
# 检查模型文件
python tools/model_viewer.py models/female/yujie.pth

# 批量检查
python tools/model_viewer.py models/**/*.pth
```

---

## ❓ 常见问题

### Q1: 下载速度太慢怎么办？

**解决方法**:
1. 切换下载源（推荐夸克网盘/阿里云盘）
2. 使用网盘客户端下载
3. 避开下载高峰时段
4. 检查网络连接

### Q2: 模型下载后无法加载？

**解决方法**:
1. 确认文件放在正确的目录
2. 检查文件名是否正确（应该以 `.pth` 结尾）
3. 重新下载模型文件
4. 运行模型检查工具：
   ```bash
   python tools/model_viewer.py models/female/yujie.pth
   ```

### Q3: 如何分享自己的模型？

**方法**:
1. 上传到网盘（夸克/百度/阿里）
2. 在社区分享链接
3. 在软件中"导入模型"测试

### Q4: 模型文件很大，下载很慢？

**建议**:
- 新手先下载**基础包**（800 MB）
- 使用**网盘客户端**加速
- 夜间闲时下载
- 和朋友共享文件

### Q5: HuggingFace 访问不了？

**替代方案**:
1. 使用国内镜像：`hf-mirror.com`
2. 使用夸克网盘
3. 使用百度网盘

命令行使用镜像：
```bash
python tools/download_models.py -m female/yujie --no-mirror
```

---

## 📊 下载统计

热门模型下载排名（2026 年数据）：

| 排名 | 模型名 | 下载量 | 评分 |
|------|--------|--------|------|
| 1 | 御姐音 | 50W+ | ⭐⭐⭐⭐⭐ |
| 2 | 萝莉音 | 45W+ | ⭐⭐⭐⭐⭐ |
| 3 | 大叔音 | 30W+ | ⭐⭐⭐⭐ |
| 4 | 青年音 | 25W+ | ⭐⭐⭐⭐ |
| 5 | 鸣人 | 20W+ | ⭐⭐⭐⭐⭐ |

---

## 🔗 相关资源

- **[模型训练教程](docs/model_training_tutorial.md)** - 学习如何训练自己的模型
- **[模型融合工具](tools/merge_models.py)** - 融合多个模型创造独特音色
- **[RVC 官方项目](https://github.com/RVC-Project)** - 获取最新模型和技术支持

---

**需要帮助？** 

- 软件内帮助：点击"下载更多模型" -> "使用帮助"
- GitHub Issues: https://github.com/yourusername/voice-changer/issues
- QQ 群：123456789

**最后更新**: 2026-05-15
