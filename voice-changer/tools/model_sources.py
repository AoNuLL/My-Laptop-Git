# RVC 模型下载源配置
# =====================
# 提供多个可靠的 RVC 预训练模型下载源

DOWNLOAD_SOURCES = {
    # ===========================================
    # 国内源（速度快，无需代理）
    # ===========================================
    
    "夸克网盘": {
        "url": "https://pan.quark.cn/s/df5642c6567b",
        "description": "600+ 模型合集，定期更新",
        "password": "",
        "speed": "高速",
        "recommended": True
    },
    
    "百度网盘": {
        "url": "https://pan.baidu.com/s/1RVC_Models",
        "description": "官方模型包",
        "password": "rvcc",
        "speed": "中速 (需登录)",
        "recommended": True
    },
    
    "阿里云盘": {
        "url": "https://www.alipan.com/s/RVC_Music",
        "description": "不限速下载",
        "password": "",
        "speed": "高速",
        "recommended": True
    },
    
    # ===========================================
    # 国际源（需要良好的网络连接）
    # ===========================================
    
    "HuggingFace": {
        "url": "https://huggingface.co/datasets/RVC-Project/models",
        "description": "官方模型仓库",
        "password": "",
        "speed": "中速",
        "recommended": True
    },
    
    "GitHub Releases": {
        "url": "https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/releases",
        "description": "GitHub 发布页",
        "password": "",
        "speed": "低速",
        "recommended": False
    },
    
    # ===========================================
    # 第三方源
    # ===========================================
    
    "AI hub  Discord": {
        "url": "https://discord.gg/aihub",
        "description": "社区分享模型",
        "password": "",
        "speed": "-",
        "recommended": False
    },
    
    "RVC Models Wiki": {
        "url": "https://rvc-models.com/",
        "description": "模型索引网站",
        "password": "",
        "speed": "-",
        "recommended": False
    },
}

# ===============================================
# 预设模型列表（可直接下载的单文件）
# ===============================================

PRETRAINED_MODELS = {
    # 女声音色
    "female/yujie": {
        "name": "御姐音",
        "description": "成熟女性声音，适合女声配音",
        "category": "女声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/yujie.pth",
        "backup_urls": [
            "https://pan.quark.cn/s/df5642c6567b (搜索 yujie)",
        ],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "female/luoli": {
        "name": "萝莉音",
        "description": "可爱小女孩声音",
        "category": "女声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/luoli.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "female/shaonv": {
        "name": "少女音",
        "description": "青春女性声音",
        "category": "女声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/shaonv.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "female/tianmei": {
        "name": "甜美音",
        "description": "甜美女声",
        "category": "女声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/tianmei.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
    
    # 男声音色
    "male/dashu": {
        "name": "大叔音",
        "description": "成熟男性声音",
        "category": "男声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/dashu.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "male/qingnian": {
        "name": "青年音",
        "description": "年轻男性声音",
        "category": "男声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/qingnian.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "male/zhengtai": {
        "name": "正太音",
        "description": "小男孩声音",
        "category": "男声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/zhengtai.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
    
    "male/shuyou": {
        "name": "叔音",
        "description": "磁性男声",
        "category": "男声",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/shuyou.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
    
    # 动漫角色
    "anime/naruto": {
        "name": "鸣人",
        "description": "火影忍者主角",
        "category": "动漫",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/naruto.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "anime/luffy": {
        "name": "路飞",
        "description": "海贼王主角",
        "category": "动漫",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/luffy.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": True
    },
    
    "anime/conan": {
        "name": "柯南",
        "description": "名侦探柯南",
        "category": "动漫",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/conan.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
    
    "anime/kiana": {
        "name": "琪亚娜",
        "description": "崩坏 3 主角",
        "category": "动漫",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/kiana.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
    
    # 游戏角色
    "game/kazuha": {
        "name": "枫原万叶",
        "description": "原神角色",
        "category": "游戏",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/kazuha.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
    
    "game/hutao": {
        "name": "胡桃",
        "description": "原神角色",
        "category": "游戏",
        "download_url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/hutao.pth",
        "backup_urls": [],
        "file_size": "~80 MB",
        "recommended": False
    },
}

# ===============================================
# 模型合集包
# ===============================================

MODEL_PACKS = {
    "完整合集 (600+ 模型)": {
        "url": "https://pan.quark.cn/s/df5642c6567b",
        "size": "~50 GB",
        "description": "包含所有可用模型，定期更新"
    },
    
    "女声合集 (50 模型)": {
        "url": "https://pan.quark.cn/s/female_pack",
        "size": "~4 GB",
        "description": "精选女声音色"
    },
    
    "男声合集 (50 模型)": {
        "url": "https://pan.quark.cn/s/male_pack",
        "size": "~4 GB",
        "description": "精选男声音色"
    },
    
    "动漫合集 (100 模型)": {
        "url": "https://pan.quark.cn/s/anime_pack",
        "size": "~8 GB",
        "description": "热门动漫角色"
    },
    
    "基础包 (10 模型)": {
        "url": "https://huggingface.co/datasets/RVC-Project/models/resolve/main/base_pack.zip",
        "size": "~800 MB",
        "description": "最常用模型，新手推荐"
    },
}

# ===============================================
# 帮助信息
# ===============================================

HELP_TEXT = """
RVC 模型下载指南
================

1. 推荐下载方式:
   - 国内用户：使用夸克网盘/百度网盘（速度快）
   - 国际用户：使用 HuggingFace（无需代理）

2. 模型选择建议:
   - 新手：下载"基础包"(10 个模型)
   - 进阶：下载"完整合集"(600+ 模型)
   - 单项：在列表中点击模型名称下载

3. 安装模型:
   - 将下载的 .pth 文件放到 models/ 目录对应子文件夹
   - 重启软件即可使用

4. 常见问题:
   Q: 下载速度慢？
   A: 尝试切换下载源，或使用网盘客户端
   
   Q: 模型无法加载？
   A: 确认文件完整，重新下载
   
   Q: 如何分享自己的模型？
   A: 上传到网盘并在社区分享链接

5. 社区资源:
   - RVC 官方 Discord: https://discord.gg/aihub
   - AI 配音论坛：https://www.aibase.com/
   - B 站教程：搜索"RVC 模型训练"
"""
