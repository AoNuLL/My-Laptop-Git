# RVC 模型下载速查表

**版本**: v2.1.4  
**更新时间**: 2026-05-15

---

## ⚡ 快速下载命令

```bash
# 1. 下载基础包（新手推荐）
python tools/download_models.py -p "基础包"

# 2. 下载单个模型
python tools/download_models.py -m female/yujie

# 3. 下载所有热门模型
python tools/download_models.py -a

# 4. 下载完整合集
# 打开：https://pan.quark.cn/s/df5642c6567b
```

---

## 🎯 热门推荐模型

### 女声 TOP5

```
1. female/yujie    - 御姐音 (成熟女声)
2. female/luoli    - 萝莉音 (可爱女孩)
3. female/shaonv   - 少女音 (青春女声)
4. female/tianmei  - 甜美音 (甜美女声)
5. female/yujie2   - 御姐音 2 号
```

**快速下载**:
```bash
python tools/download_models.py -m female/yujie -m female/luoli -m female/shaonv
```

### 男声 TOP5

```
1. male/dashu      - 大叔音 (成熟男声)
2. male/qingnian   - 青年音 (年轻男声)
3. male/zhengtai   - 正太音 (小男孩)
4. male/shuyou     - 叔音 (磁性男声)
5. male/chengshu   - 成熟音
```

**快速下载**:
```bash
python tools/download_models.py -m male/dashu -m male/qingnian
```

### 动漫游戏 TOP5

```
1. anime/naruto    - 鸣人 (火影忍者)
2. anime/luffy     - 路飞 (海贼王)
3. anime/conan     - 柯南 (名侦探)
4. anime/kiana     - 琪亚娜 (崩坏 3)
5. game/kazuha     - 枫原万叶 (原神)
```

---

## 🌐 下载源速度对比

| 下载源 | 速度 | 稳定性 | 推荐度 |
|--------|------|--------|--------|
| 夸克网盘 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 阿里云盘 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 百度网盘 | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| HuggingFace | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| GitHub | ⭐⭐ | ⭐⭐⭐ | ⭐⭐ |

---

## 📦 合集包选择

### 新手入门

**基础包** (首选)
- 大小：~800MB
- 模型数：10 个
- 内容：最热门模型
- 下载：`python tools/download_models.py -p "基础包"`

### 进阶用户

| 合集包 | 大小 | 模型数 | 适合 |
|--------|------|--------|------|
| 女声合集 | ~4GB | 50+ | 女声配音 |
| 男声合集 | ~4GB | 50+ | 男声配音 |
| 动漫合集 | ~8GB | 100+ | 动漫角色 |
| 完整合集 | ~50GB | 600+ | 收藏党 |

---

## ❓ 故障排查

### 下载速度慢

```bash
# 使用国内镜像
python tools/download_models.py -m female/yujie --no-mirror

# 或切换下载源
# 夸克网盘 > 阿里云盘 > 百度网盘
```

### 模型无法加载

```bash
# 检查模型完整性
python tools/model_viewer.py models/female/yujie.pth

# 重新下载
python tools/download_models.py -m female/yujie
```

### 文件位置

```
models/
├── female/     # 女声模型
├── male/       # 男声模型
├── anime/      # 动漫模型
└── custom/     # 自定义模型
```

---

## 💻 图形界面下载

### 步骤

1. 打开软件
2. 点击 **"📥 下载更多模型"** (左侧模型面板)
3. 选择标签页:
   - **推荐下载源** - 查看网盘链接
   - **单模型下载** - 点击下载单个模型
   - **模型合集包** - 下载打包模型
   - **使用帮助** - 查看帮助信息

### 截图

```
┌────────────────────────────────────┐
│  📥 RVC 模型下载中心                │
├────────────────────────────────────┤
│ [推荐下载源] [单模型下载] [合集包]  │
│                                    │
│  【女声】                          │
│  ⭐ 御姐音 (female/yujie)          │
│     描述：成熟女性声音              │
│     大小：~80 MB                   │
│     [下载此模型]                   │
│                                    │
│  【男声】                          │
│  ⭐ 大叔音 (male/dashu)            │
│     描述：成熟男性声音              │
│     大小：~80 MB                   │
│     [下载此模型]                   │
└────────────────────────────────────┘
```

---

## 📞 获取帮助

### 文档

- **完整下载指南**: `docs/model_download_guide.md`
- **模型训练教程**: `docs/model_training_tutorial.md`
- **故障排查**: `docs/troubleshooting.md`

### 命令行帮助

```bash
python tools/download_models.py --help
python tools/download_models.py --list
python tools/download_models.py --sources
```

### 社区支持

- **QQ 群**: 123456789
- **GitHub Issues**: https://github.com/yourusername/voice-changer/issues
- **Discord**: https://discord.gg/aihub

---

## 🆕 持续更新

关注以下渠道获取最新模型信息：

- **HuggingFace**: https://huggingface.co/datasets/RVC-Project/models
- **RVC 官方 GitHub**: https://github.com/RVC-Project
- **AI Hub Discord**: https://discord.gg/aihub

---

**提示**: 本速查表会定期更新，建议保存为书签或打印出来参考。
