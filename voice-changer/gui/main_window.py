"""
主窗口 GUI
===========
使用 CustomTkinter 创建现代化用户界面
"""

import customtkinter as ctk
from tkinter import messagebox, filedialog
import threading
import numpy as np
import os
from pathlib import Path
from typing import Optional, Dict, List

from core.audio_processor import AudioProcessor, AudioConfig
from core.rvc_inference import RVCInference


class VoiceChangerApp:
    """
    变声器主应用程序
    =================
    
    Attributes:
        root: 主窗口
        audio_processor: 音频处理器
        rvc_engine: RVC 推理引擎
        is_running: 是否在运行中
    """
    
    def __init__(self):
        """初始化应用程序"""
        # 配置 CustomTkinter
        ctk.set_appearance_mode("dark")
        ctk.set_default_color_theme("blue")
        
        # 创建主窗口
        self.root = ctk.CTk()
        self.root.title("Voice Changer - 开源变声器")
        self.root.geometry("1000x700")
        self.root.minsize(800, 600)
        
        # 初始化组件
        self.audio_processor: Optional[AudioProcessor] = None
        self.rvc_engine: Optional[RVCInference] = None
        self.is_running = False
        self.current_model: Optional[str] = None
        self.pitch_shift = 0  # 音调偏移
        
        # 构建界面
        self._setup_ui()
        
        # 初始化音频设备
        self._init_audio()
    
    def _setup_ui(self):
        """构建用户界面"""
        # 主容器
        self.main_frame = ctk.CTkFrame(self.root)
        self.main_frame.pack(fill="both", expand=True, padx=10, pady=10)
        
        # 左侧面板 - 模型选择
        self._create_model_panel()
        
        # 中间面板 - 控制面板
        self._create_control_panel()
        
        # 右侧面板 - 波形显示和设置
        self._create_display_panel()
    
    def _create_model_panel(self):
        """创建左侧模型选择面板"""
        panel = ctk.CTkFrame(self.main_frame, width=250)
        panel.pack(side="left", fill="y", padx=(0, 10))
        
        # 标题
        title = ctk.CTkLabel(
            panel,
            text="音色模型",
            font=ctk.CTkFont(size=20, weight="bold")
        )
        title.pack(pady=(20, 10))
        
        # 模型分类选项卡
        tabview = ctk.CTkTabview(panel)
        tabview.pack(fill="both", expand=True, padx=10, pady=10)
        
        # 添加分类标签页
        self.model_tabs = {
            "女声": tabview.add("女声"),
            "男声": tabview.add("男声"),
            "动漫": tabview.add("动漫"),
            "自定义": tabview.add("自定义")
        }
        
        # 添加示例模型按钮
        sample_models = {
            "女声": ["御姐音", "萝莉音", "少女音", "甜美女声"],
            "男声": ["大叔音", "青年音", "正太音", "成熟男声"],
            "动漫": ["鸣人", "路飞", "柯南", "琪亚娜"],
            "自定义": []
        }
        
        for category, models in sample_models.items():
            frame = self.model_tabs[category]
            for model_name in models:
                btn = ctk.CTkButton(
                    frame,
                    text=model_name,
                    width=200,
                    height=40,
                    command=lambda name=model_name: self._select_model(name)
                )
                btn.pack(pady=5)
        
        # 导入自定义模型按钮
        import_btn = ctk.CTkButton(
            self.model_tabs["自定义"],
            text="导入模型 (.pth)",
            command=self._import_model,
            width=200
        )
        import_btn.pack(pady=10)
        
        # 下载模型按钮
        download_btn = ctk.CTkButton(
            panel,
            text="📥 下载更多模型",
            command=self._show_model_sources,
            fg_color="#2196F3",
            width=200,
            height=40
        )
        download_btn.pack(pady=10, padx=20)
    
    def _create_control_panel(self):
        """创建中间控制面板"""
        panel = ctk.CTkFrame(self.main_frame, width=450)
        panel.pack(side="left", fill="both", expand=True, padx=10)
        
        # 当前模型显示
        model_frame = ctk.CTkFrame(panel)
        model_frame.pack(fill="x", pady=10)
        
        ctk.CTkLabel(
            model_frame,
            text="当前模型:",
            font=ctk.CTkFont(size=14)
        ).pack(side="left", padx=10)
        
        self.model_label = ctk.CTkLabel(
            model_frame,
            text="未选择",
            font=ctk.CTkFont(size=14, weight="bold"),
            text_color="cyan"
        )
        self.model_label.pack(side="left", padx=10)
        
        # 控制按钮区域
        control_frame = ctk.CTkFrame(panel)
        control_frame.pack(fill="x", pady=20)
        
        # 开始/停止按钮
        self.start_button = ctk.CTkButton(
            control_frame,
            text="开始变声",
            font=ctk.CTkFont(size=18, weight="bold"),
            height=60,
            command=self._toggle_voice_change,
            fg_color="green"
        )
        self.start_button.pack(fill="x", padx=50, pady=10)
        
        # 音调调节
        pitch_frame = ctk.CTkFrame(panel)
        pitch_frame.pack(fill="x", pady=10)
        
        ctk.CTkLabel(
            pitch_frame,
            text="音调调节 (半音):",
            font=ctk.CTkFont(size=14)
        ).pack()
        
        self.pitch_slider = ctk.CTkSlider(
            pitch_frame,
            from_=-24,
            to=24,
            number_of_steps=48,
            command=self._on_pitch_change
        )
        self.pitch_slider.pack(fill="x", padx=20, pady=10)
        self.pitch_slider.set(0)
        
        self.pitch_value_label = ctk.CTkLabel(
            pitch_frame,
            text="+0",
            font=ctk.CTkFont(size=12)
        )
        self.pitch_value_label.pack()
        
        # 音量控制
        volume_frame = ctk.CTkFrame(panel)
        volume_frame.pack(fill="x", pady=10)
        
        ctk.CTkLabel(
            volume_frame,
            text="输出音量:",
            font=ctk.CTkFont(size=14)
        ).pack()
        
        self.volume_slider = ctk.CTkSlider(
            volume_frame,
            from_=0,
            to=2,
            number_of_steps=100,
            command=self._on_volume_change
        )
        self.volume_slider.pack(fill="x", padx=20, pady=10)
        self.volume_slider.set(1.0)
        
        self.volume_value_label = ctk.CTkLabel(
            volume_frame,
            text="100%",
            font=ctk.CTkFont(size=12)
        )
        self.volume_value_label.pack()
        
        # 延迟显示
        latency_frame = ctk.CTkFrame(panel)
        latency_frame.pack(fill="x", pady=20)
        
        ctk.CTkLabel(
            latency_frame,
            text="当前延迟:",
            font=ctk.CTkFont(size=14)
        ).pack()
        
        self.latency_label = ctk.CTkLabel(
            latency_frame,
            text="0 ms",
            font=ctk.CTkFont(size=18, weight="bold"),
            text_color="green"
        )
        self.latency_label.pack()
    
    def _create_display_panel(self):
        """创建右侧显示面板"""
        panel = ctk.CTkFrame(self.main_frame, width=300)
        panel.pack(side="left", fill="both", padx=(10, 0))
        
        # 音量表
        volume_frame = ctk.CTkFrame(panel)
        volume_frame.pack(fill="x", padx=10, pady=10)
        
        ctk.CTkLabel(
            volume_frame,
            text="输入音量",
            font=ctk.CTkFont(size=14)
        ).pack()
        
        self.input_volume_bar = ctk.CTkProgressBar(
            volume_frame,
            width=250,
            height=20
        )
        self.input_volume_bar.pack(pady=10)
        self.input_volume_bar.set(0)
        
        ctk.CTkLabel(
            volume_frame,
            text="输出音量",
            font=ctk.CTkFont(size=14)
        ).pack()
        
        self.output_volume_bar = ctk.CTkProgressBar(
            volume_frame,
            width=250,
            height=20
        )
        self.output_volume_bar.pack(pady=10)
        self.output_volume_bar.set(0)
        
        # 设置按钮
        settings_frame = ctk.CTkFrame(panel)
        settings_frame.pack(fill="x", padx=10, pady=10)
        
        ctk.CTkLabel(
            settings_frame,
            text="快捷操作",
            font=ctk.CTkFont(size=14, weight="bold")
        ).pack(pady=10)
        
        # 安装虚拟声卡按钮
        vcard_btn = ctk.CTkButton(
            settings_frame,
            text="安装虚拟声卡",
            command=self._install_virtual_audio,
            width=250
        )
        vcard_btn.pack(pady=5)
        
        # 设备选择按钮
        device_btn = ctk.CTkButton(
            settings_frame,
            text="选择音频设备",
            command=self._select_audio_device,
            width=250
        )
        device_btn.pack(pady=5)
        
        # 打开设置窗口按钮
        ctk.CTkButton(
            settings_frame,
            text="打开设置",
            command=self._open_settings,
            width=250,
            fg_color="gray"
        ).pack(pady=5)
    
    def _init_audio(self):
        """初始化音频处理器"""
        try:
            config = AudioConfig(
                sample_rate=48000,
                channels=1,
                chunk_size=512
            )
            self.audio_processor = AudioProcessor(config)
            self.audio_processor.error_callback = self._on_audio_error
            
        except Exception as e:
            messagebox.showerror("音频初始化失败", str(e))
    
    def _select_model(self, model_name: str):
        """
        选择音色模型
        
        Args:
            model_name: 模型名称
        """
        self.current_model = model_name
        self.model_label.configure(text=model_name)
        
        # 加载模型到 RVC 引擎
        self._load_rvc_model(model_name)
    
    def _load_rvc_model(self, model_name: str):
        """
        加载 RVC 模型
        
        Args:
            model_name: 模型名称
        """
        try:
            # 构建模型路径
            model_path = Path("models") / f"{model_name}.pth"
            
            if not model_path.exists():
                # 模型不存在，显示提示
                messagebox.showwarning(
                    "模型未找到",
                    f"模型 {model_name} 不存在，请先下载或导入模型。\n\n"
                    "你可以从以下地址下载模型:\n"
                    "https://pan.quark.cn/s/df5642c6567b"
                )
                return
            
            # 初始化 RVC 引擎
            if self.rvc_engine is None:
                self.rvc_engine = RVCInference()
            
            # 加载模型
            self.rvc_engine.load_model(str(model_path))
            
        except Exception as e:
            messagebox.showerror("模型加载失败", str(e))
    
    def _import_model(self):
        """导入自定义模型文件"""
        file_path = filedialog.askopenfilename(
            title="选择 RVC 模型文件",
            filetypes=[("RVC Model", "*.pth *.onnx"), ("All Files", "*.*")]
        )
        
        if file_path:
            try:
                # 复制模型到 models/custom 目录
                import shutil
                model_name = Path(file_path).stem
                target_path = Path("models/custom") / f"{model_name}.pth"
                shutil.copy(file_path, target_path)
                
                # 刷新模型列表（需要重新创建按钮）
                messagebox.showinfo("导入成功", f"模型已导入：\n{target_path}")
                
            except Exception as e:
                messagebox.showerror("导入失败", str(e))
    
    def _toggle_voice_change(self):
        """切换变声状态"""
        if self.is_running:
            self._stop_voice_change()
        else:
            self._start_voice_change()
    
    def _start_voice_change(self):
        """开始变声"""
        if not self.current_model:
            messagebox.showwarning("警告", "请先选择音色模型")
            return
        
        try:
            # 开始录制和处理
            if self.audio_processor:
                self.audio_processor.start_recording(
                    audio_callback=self._process_audio
                )
                self.audio_processor.start_playback()
            
            # 更新 UI
            self.start_button.configure(text="停止变声", fg_color="red")
            self.is_running = True
            
            # 启动音量监控
            self._update_volume_display()
            
        except Exception as e:
            messagebox.showerror("启动失败", str(e))
            self._stop_voice_change()
    
    def _stop_voice_change(self):
        """停止变声"""
        try:
            if self.audio_processor:
                self.audio_processor.stop()
            
            # 更新 UI
            self.start_button.configure(text="开始变声", fg_color="green")
            self.is_running = False
            
        except Exception as e:
            messagebox.showerror("停止失败", str(e))
    
    def _process_audio(self, audio_data: np.ndarray) -> np.ndarray:
        """
        处理音频数据（变声核心函数）
        
        Args:
            audio_data: 输入音频数据
            
        Returns:
            处理后的音频数据
        """
        try:
            # 1. 使用 RVC 进行变声
            if self.rvc_engine and self.current_model:
                processed = self.rvc_engine.convert(
                    audio_data,
                    pitch_shift=self.pitch_shift
                )
            else:
                processed = audio_data.copy()
            
            # 2. 应用音量调节
            volume = self.volume_slider.get()
            processed = processed * volume
            
            # 3. 限制幅度
            processed = np.clip(processed, -1.0, 1.0)
            
            return processed.astype(np.float32)
            
        except Exception as e:
            # 错误时返回原始音频
            print(f"音频处理错误：{str(e)}")
            return audio_data.copy()
    
    def _on_pitch_change(self, value: float):
        """
        音调调节回调
        
        Args:
            value: 音调值（半音）
        """
        self.pitch_shift = int(value)
        self.pitch_value_label.configure(text=f"+{int(value)}" if value >= 0 else f"{int(value)}")
    
    def _on_volume_change(self, value: float):
        """
        音量调节回调
        
        Args:
            value: 音量值（0-2）
        """
        percentage = int(value * 100)
        self.volume_value_label.configure(text=f"{percentage}%")
    
    def _update_volume_display(self):
        """更新音量显示（定时调用）"""
        if self.is_running and self.audio_processor:
            try:
                # 更新输入音量
                input_vol = self.audio_processor.get_volume()
                self.input_volume_bar.set(input_vol)
                
                # 更新输出音量（略低于输入）
                self.output_volume_bar.set(input_vol * 0.8)
                
                # 更新延迟显示（模拟值）
                estimated_latency = int(self.audio_processor.config.chunk_size * 1000 / 
                                      self.audio_processor.config.sample_rate)
                self.latency_label.configure(text=f"{estimated_latency} ms")
                
            except Exception:
                pass
        
        # 100ms 后再次调用
        if self.is_running:
            self.root.after(100, self._update_volume_display)
    
    def _install_virtual_audio(self):
        """安装虚拟声卡驱动"""
        import sys
        
        if sys.platform == "win32":
            result = messagebox.askyesno(
                "安装虚拟声卡",
                "是否下载并安装 VB-Audio Virtual Cable?\n\n"
                "安装后需要重启电脑才能生效。"
            )
            
            if result:
                import webbrowser
                webbrowser.open("https://vb-audio.com/Cable/")
        else:
            messagebox.showinfo(
                "提示",
                "macOS/Linux 系统需要手动安装虚拟音频设备:\n"
                "- macOS: BlackHole (https://existential.audio/blackhole/)\n"
                "- Linux: PulseAudio 虚拟设备"
            )
    
    def _select_audio_device(self):
        """选择音频输入输出设备"""
        if self.audio_processor:
            # 获取设备列表
            input_devices = self.audio_processor.get_input_devices()
            
            if not input_devices:
                messagebox.showerror("错误", "未找到可用的音频输入设备")
                return
            
            # 创建设备选择对话框
            device_window = ctk.CTkToplevel(self.root)
            device_window.title("选择音频设备")
            device_window.geometry("500x400")
            
            ctk.CTkLabel(
                device_window,
                text="选择输入设备:",
                font=ctk.CTkFont(size=14)
            ).pack(pady=10)
            
            # 设备列表
            device_listbox = ctk.CTkOptionMenu(
                device_window,
                values=[f"{d['index']}: {d['name']}" for d in input_devices]
            )
            device_listbox.pack(pady=10, padx=20)
            
            def save_selection():
                selected = device_listbox.get()
                device_index = int(selected.split(":")[0])
                
                if self.audio_processor:
                    self.audio_processor.config.device_index = device_index
                    messagebox.showinfo(
                        "成功",
                        f"已选择设备：{selected}\n\n"
                        "请重启变声功能以应用更改。"
                    )
                device_window.destroy()
            
            ctk.CTkButton(
                device_window,
                text="确定",
                command=save_selection
            ).pack(pady=20)
    
    def _open_settings(self):
        """打开设置窗口"""
        settings_window = ctk.CTkToplevel(self.root)
        settings_window.title("设置")
        settings_window.geometry("500x400")
        
        # 采样率设置
        settings_frame = ctk.CTkFrame(settings_window)
        settings_frame.pack(fill="both", expand=True, padx=20, pady=20)
        
        ctk.CTkLabel(
            settings_frame,
            text="音频设置",
            font=ctk.CTkFont(size=16, weight="bold")
        ).pack(pady=10)
        
        # 采样率选项
        sample_rates = ["22050", "44100", "48000", "96000"]
        ctk.CTkLabel(
            settings_frame,
            text="采样率 (Hz):"
        ).pack()
        
        ctk.CTkOptionMenu(
            settings_frame,
            values=sample_rates,
            command=lambda v: self._update_sample_rate(int(v))
        ).pack(pady=10)
        
        # 缓冲区大小
        ctk.CTkLabel(
            settings_frame,
            text="缓冲区大小 (samples):"
        ).pack()
        
        ctk.CTkSlider(
            settings_frame,
            from_=128,
            to=4096,
            number_of_steps=15,
            command=self._update_buffer_size
        ).pack(pady=10)
        
        # 关于
        ctk.CTkLabel(
            settings_frame,
            text="\n关于",
            font=ctk.CTkFont(size=16, weight="bold")
        ).pack(pady=(20, 10))
        
        ctk.CTkLabel(
            settings_frame,
            text="Voice Changer v2.1.4\n"
                 "开源免费实时变声器\n"
                 "基于 RVC 技术",
            justify="center"
        ).pack()
    
    def _show_model_sources(self):
        """显示模型下载源窗口"""
        from tools.model_sources import DOWNLOAD_SOURCES, PRETRAINED_MODELS, MODEL_PACKS
        
        sources_window = ctk.CTkToplevel(self.root)
        sources_window.title("RVC 模型下载中心")
        sources_window.geometry("800x600")
        
        # 标题
        ctk.CTkLabel(
            sources_window,
            text="📥 RVC 模型下载中心",
            font=ctk.CTkFont(size=20, weight="bold")
        ).pack(pady=15)
        
        # 创建选项卡
        tabview = ctk.CTkTabview(sources_window)
        tabview.pack(fill="both", expand=True, padx=20, pady=10)
        
        # 推荐下载源
        sources_tab = tabview.add("推荐下载源")
        self._create_sources_tab(sources_tab)
        
        # 单模型下载
        models_tab = tabview.add("单模型下载")
        self._create_models_tab(models_tab)
        
        # 合集包
        packs_tab = tabview.add("模型合集包")
        self._create_packs_tab(packs_tab)
        
        # 帮助
        help_tab = tabview.add("使用帮助")
        self._create_help_tab(help_tab)
    
    def _create_sources_tab(self, parent):
        """创建下载源标签页"""
        from tools.model_sources import DOWNLOAD_SOURCES
        
        scroll_frame = ctk.CTkScrollableFrame(parent)
        scroll_frame.pack(fill="both", expand=True)
        
        ctk.CTkLabel(
            scroll_frame,
            text="推荐下载源（点击链接打开浏览器）",
            font=ctk.CTkFont(size=14)
        ).pack(pady=10)
        
        for source_name, info in DOWNLOAD_SOURCES.items():
            frame = ctk.CTkFrame(scroll_frame)
            frame.pack(fill="x", padx=10, pady=5)
            
            flag = "⭐" if info.get("recommended") else ""
            
            ctk.CTkLabel(
                frame,
                text=f"{flag} {source_name}",
                font=ctk.CTkFont(size=14, weight="bold"),
                anchor="w"
            ).pack(fill="x", padx=10, pady=(10, 5))
            
            ctk.CTkLabel(
                frame,
                text=f"说明：{info['description']}",
                anchor="w"
            ).pack(fill="x", padx=10)
            
            ctk.CTkLabel(
                frame,
                text=f"速度：{info['speed']}",
                anchor="w"
            ).pack(fill="x", padx=10)
            
            url_link = ctk.CTkLabel(
                frame,
                text=info['url'],
                text_color="#2196F3",
                cursor="hand",
                anchor="w"
            )
            url_link.pack(fill="x", padx=10, pady=(0, 10))
            url_link.bind("<Button-1>", lambda e, url=info['url']: self._open_url(url))
            
            if info.get('password'):
                ctk.CTkLabel(
                    frame,
                    text=f"提取码：{info['password']}",
                    text_color="#FF9800",
                    anchor="w"
                ).pack(fill="x", padx=10, pady=(0, 10))
    
    def _create_models_tab(self, parent):
        """创建单模型下载标签页"""
        from tools.model_sources import PRETRAINED_MODELS
        
        scroll_frame = ctk.CTkScrollableFrame(parent)
        scroll_frame.pack(fill="both", expand=True)
        
        # 分类标签
        categories = {}
        for model_name, info in PRETRAINED_MODELS.items():
            cat = info["category"]
            if cat not in categories:
                categories[cat] = []
            categories[cat].append((model_name, info))
        
        for category, models in categories.items():
            ctk.CTkLabel(
                scroll_frame,
                text=f"【{category}】",
                font=ctk.CTkFont(size=16, weight="bold")
            ).pack(fill="x", padx=10, pady=(15, 10))
            
            for model_name, info in models:
                frame = ctk.CTkFrame(scroll_frame)
                frame.pack(fill="x", padx=10, pady=3)
                
                flag = "⭐" if info.get("recommended") else ""
                
                ctk.CTkLabel(
                    frame,
                    text=f"{flag} {info['name']} ({model_name})",
                    font=ctk.CTkFont(size=13),
                    anchor="w"
                ).pack(fill="x", padx=10, pady=5)
                
                ctk.CTkLabel(
                    frame,
                    text=f"描述：{info['description']}",
                    anchor="w"
                ).pack(fill="x", padx=10)
                
                ctk.CTkLabel(
                    frame,
                    text=f"大小：{info.get('file_size', '?')}",
                    anchor="w"
                ).pack(fill="x", padx=10)
                
                btn = ctk.CTkButton(
                    frame,
                    text="下载此模型",
                    width=100,
                    height=30,
                    command=lambda m=model_name: self._download_single_model(m)
                )
                btn.pack(anchor="e", padx=10, pady=5)
    
    def _create_packs_tab(self, parent):
        """创建合集包标签页"""
        from tools.model_sources import MODEL_PACKS
        
        scroll_frame = ctk.CTkScrollableFrame(parent)
        scroll_frame.pack(fill="both", expand=True)
        
        ctk.CTkLabel(
            scroll_frame,
            text="模型合集包（推荐新手下载基础包）",
            font=ctk.CTkFont(size=14)
        ).pack(pady=10)
        
        for pack_name, info in MODEL_PACKS.items():
            frame = ctk.CTkFrame(scroll_frame)
            frame.pack(fill="x", padx=10, pady=8)
            
            ctk.CTkLabel(
                frame,
                text=pack_name,
                font=ctk.CTkFont(size=15, weight="bold"),
                anchor="w"
            ).pack(fill="x", padx=10, pady=(10, 5))
            
            ctk.CTkLabel(
                frame,
                text=f"大小：{info['size']}",
                anchor="w"
            ).pack(fill="x", padx=10)
            
            ctk.CTkLabel(
                frame,
                text=f"说明：{info['description']}",
                anchor="w"
            ).pack(fill="x", padx=10)
            
            btn = ctk.CTkButton(
                frame,
                text="打开下载链接",
                width=120,
                height=30,
                command=lambda url=info['url']: self._open_url(url)
            )
            btn.pack(anchor="e", padx=10, pady=10)
    
    def _create_help_tab(self, parent):
        """创建帮助标签页"""
        from tools.model_sources import HELP_TEXT
        
        scroll_frame = ctk.CTkScrollableFrame(parent)
        scroll_frame.pack(fill="both", expand=True)
        
        text_widget = ctk.CTkTextbox(scroll_frame, wrap="word")
        text_widget.pack(fill="both", expand=True, padx=10, pady=10)
        text_widget.insert("0.0", HELP_TEXT)
        text_widget.configure(state="disabled")
    
    def _open_url(self, url: str):
        """打开 URL"""
        import webbrowser
        webbrowser.open(url)
    
    def _download_single_model(self, model_name: str):
        """下载单个模型"""
        from tools.download_models import ModelDownloader
        import threading
        
        def download_thread():
            downloader = ModelDownloader("models")
            success = downloader.download_preset_model(model_name)
            
            if success:
                messagebox.showinfo(
                    "下载成功",
                    f"模型已下载到:\nmodels/{model_name}.pth\n\n请在模型列表刷新后使用"
                )
            else:
                messagebox.showerror(
                    "下载失败",
                    "请检查网络连接或尝试其他下载源"
                )
        
        # 在新线程中下载
        threading.Thread(target=download_thread, daemon=True).start()
    
    def _update_sample_rate(self, rate: int):
        """更新采样率设置"""
        if self.audio_processor:
            self.audio_processor.config.sample_rate = rate
            messagebox.showinfo(
                "提示",
                f"采样率已更改为 {rate} Hz\n请重启变声功能以应用更改。"
            )
    
    def _update_buffer_size(self, size: float):
        """更新缓冲区大小"""
        if self.audio_processor:
            self.audio_processor.config.chunk_size = int(size)
            # 计算延迟
            latency = int(size * 1000 / self.audio_processor.config.sample_rate)
            messagebox.showinfo(
                "提示",
                f"缓冲区大小：{int(size)} samples\n"
                f"估计延迟：{latency} ms"
            )
    
    def _on_audio_error(self, error_msg: str):
        """
        音频错误回调
        
        Args:
            error_msg: 错误信息
        """
        print(f"音频错误：{error_msg}")
        # 可以在这里更新 UI 显示错误
    
    def run(self):
        """运行应用程序主循环"""
        self.root.mainloop()
    
    def destroy(self):
        """销毁应用程序"""
        if self.audio_processor:
            self.audio_processor.stop()
        if self.rvc_engine:
            del self.rvc_engine
        self.root.destroy()
