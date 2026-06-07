"""
Voice Changer 移动端应用
=========================
基于 Kivy 的跨平台移动应用 (Android/iOS)
"""

from kivy.app import App
from kivy.properties import ObjectProperty, NumericProperty, BooleanProperty
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.spinner import Spinner
from kivy.clock import Clock
from kivy.core.audio import SoundRecorder, SoundLoader
from kivy.metrics import dp, sp
import numpy as np
import threading
import time
import os

# 导入核心模块
from core.audio_processor import AudioProcessor, AudioConfig
from core.rvc_inference import RVCInference


class MobileViewModel(BoxLayout):
    """移动端主视图"""
    
    is_recording = BooleanProperty(False)
    is_processing = BooleanProperty(False)
    volume_level = NumericProperty(0.0)
    selected_model = ObjectProperty(None)
    pitch_value = NumericProperty(0)
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        self.padding = dp(10)
        self.spacing = dp(10)
        
        # 音频处理器
        self.audio_processor = None
        self.rvc_engine = None
        
        # 移动端优化配置
        self.config = AudioConfig(
            sample_rate=22050,  # 移动端降低采样率
            channels=1,
            chunk_size=1024     # 增大缓冲区
        )
        
        # 初始化 UI
        self._setup_ui()
    
    def _setup_ui(self):
        """设置移动端 UI"""
        from kivy.uix.label import Label
        from kivy.uix.button import Button
        from kivy.uix.slider import Slider
        from kivy.uix.progressbar import ProgressBar
        
        # 标题
        title = Label(
            text='Voice Changer',
            size_hint_y=None,
            height=dp(50),
            font_size=sp(24)
        )
        self.add_widget(title)
        
        # 模型选择
        model_layout = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height=dp(100)
        )
        
        model_label = Label(text='选择音色:')
        self.model_spinner = Spinner(
            text='选择模型...',
            values=['女声 - 御姐', '女声 - 萝莉', '男声 - 大叔', '男声 - 青年'],
            size_hint_y=None,
            height=dp(44)
        )
        self.model_spinner.bind(text=self._on_model_select)
        
        model_layout.add_widget(model_label)
        model_layout.add_widget(self.model_spinner)
        self.add_widget(model_layout)
        
        # 音调滑块
        pitch_layout = BoxLayout(orientation='vertical')
        pitch_label = Label(text=f'音调：{self.pitch_value}')
        self.pitch_slider = Slider(
            min=-24, max=24, step=1,
            value=0,
            size_hint_y=None,
            height=dp(50)
        )
        self.pitch_slider.bind(value=self._on_pitch_change)
        
        pitch_layout.add_widget(pitch_label)
        pitch_layout.add_widget(self.pitch_slider)
        self.add_widget(pitch_layout)
        
        # 音量表
        self.volume_bar = ProgressBar(max=100)
        self.add_widget(self.volume_bar)
        
        # 录音按钮
        self.record_btn = Button(
            text='按住说话',
            size_hint_y=None,
            height=dp(80),
            font_size=sp(20)
        )
        self.record_btn.bind(
            on_press=self._start_recording,
            on_release=self._stop_recording
        )
        self.add_widget(self.record_btn)
        
        # 状态标签
        self.status_label = Label(text='就绪', size_hint_y=None, height=dp(30))
        self.add_widget(self.status_label)
    
    def _on_model_select(self, spinner, model_name):
        """模型选择回调"""
        self.selected_model = model_name
        self.status_label.text = f'已选择：{model_name}'
        
        # 加载模型
        self._load_model(model_name)
    
    def _load_model(self, model_name):
        """加载 RVC 模型"""
        try:
            # 映射模型名称到文件
            model_map = {
                '女声 - 御姐': 'models/female/yujie.pth',
                '女声 - 萝莉': 'models/female/luoli.pth',
                '男声 - 大叔': 'models/male/dashu.pth',
                '男声 - 青年': 'models/male/qingnian.pth'
            }
            
            model_path = model_map.get(model_name)
            if model_path and os.path.exists(model_path):
                if self.rvc_engine is None:
                    self.rvc_engine = RVCInference()
                self.rvc_engine.load_model(model_path)
                self.status_label.text = '模型加载成功'
            else:
                self.status_label.text = '模型文件不存在，请先下载'
                
        except Exception as e:
            self.status_label.text = f'模型加载失败：{str(e)}'
    
    def _on_pitch_change(self, slider, value):
        """音调变化回调"""
        self.pitch_value = int(value)
        # 更新标签文本
        for widget in self.children:
            if isinstance(widget, BoxLayout):
                for child in widget.children:
                    from kivy.uix.label import Label
                    if isinstance(child, Label) and '音调' in child.text:
                        child.text = f'音调：{int(value)}'
                        break
    
    def _start_recording(self, instance):
        """开始录音"""
        if not self.selected_model:
            self.status_label.text = '请先选择音色模型'
            return
        
        try:
            self.is_recording = True
            self.record_btn.text = '松开结束'
            self.status_label.text = '录音中...'
            
            # 初始化音频处理器
            if self.audio_processor is None:
                self.audio_processor = AudioProcessor(self.config)
            
            # 开始录音和变声
            self.audio_processor.start_recording(self._process_audio)
            self.audio_processor.start_playback()
            
        except Exception as e:
            self.status_label.text = f'录音失败：{str(e)}'
            self._stop_recording(None)
    
    def _stop_recording(self, instance):
        """停止录音"""
        self.is_recording = False
        self.record_btn.text = '按住说话'
        self.status_label.text = '就绪'
        
        if self.audio_processor:
            self.audio_processor.stop()
    
    def _process_audio(self, audio_data):
        """音频处理回调"""
        try:
            # 更新音量显示
            volume = np.sqrt(np.mean(audio_data ** 2)) * 100
            Clock.schedule_once(
                lambda dt: setattr(self, 'volume_level', min(volume, 100))
            )
            Clock.schedule_once(
                lambda dt: self.volume_bar.__setattr__('value', min(volume, 100))
            )
            
            # 使用 RVC 变声
            if self.rvc_engine:
                processed = self.rvc_engine.convert(
                    audio_data,
                    pitch_shift=self.pitch_value
                )
            else:
                processed = audio_data.copy()
            
            return processed.astype(np.float32)
            
        except Exception as e:
            return audio_data.copy()


class SettingsScreen(Screen):
    """设置界面"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.name = 'settings'
        
        from kivy.uix.scrollview import ScrollView
        from kivy.uix.label import Label
        
        scroll = ScrollView()
        content = BoxLayout(orientation='vertical', padding=dp(20), spacing=dp(10))
        
        content.add_widget(Label(text='设置', font_size=sp(24), size_hint_y=None, height=dp(50)))
        
        # 采样率设置
        content.add_widget(Label(text='采样率:', size_hint_y=None, height=dp(30)))
        sample_spinner = Spinner(
            text='22050 Hz',
            values=['22050 Hz', '44100 Hz', '48000 Hz'],
            size_hint_y=None,
            height=dp(44)
        )
        content.add_widget(sample_spinner)
        
        # 缓冲区设置
        content.add_widget(Label(text='缓冲区:', size_hint_y=None, height=dp(30)))
        buffer_spinner = Spinner(
            text='512',
            values=['256', '512', '1024', '2048'],
            size_hint_y=None,
            height=dp(44)
        )
        content.add_widget(buffer_spinner)
        
        # 关于
        content.add_widget(Label(text='', size_hint_y=None, height=dp(20)))
        content.add_widget(Label(text='关于 Voice Changer', font_size=sp(18)))
        content.add_widget(Label(text='版本：v2.1.4-mobile'))
        content.add_widget(Label(text='基于 RVC 技术'))
        
        scroll.add_widget(content)
        self.add_widget(scroll)


class MobileScreenManager(ScreenManager):
    """移动端屏幕管理器"""
    pass


class VoiceChangerMobileApp(App):
    """
    Voice Changer 移动端应用
    =========================
    """
    
    def build(self):
        """构建应用"""
        self.title = 'Voice Changer'
        
        # 设置窗口大小（移动端优化）
        from kivy.core.window import Window
        Window.clearcolor = (0.1, 0.1, 0.1, 1)
        
        # 创建屏幕管理器
        sm = MobileScreenManager()
        
        # 主屏幕
        main_screen = Screen(name='main')
        main_screen.add_widget(MobileViewModel())
        sm.add_widget(main_screen)
        
        # 设置屏幕
        sm.add_widget(SettingsScreen())
        
        return sm
    
    def on_pause(self):
        """应用进入后台（Android）"""
        # 停止音频处理
        return True
    
    def on_resume(self):
        """应用回到前台"""
        pass
    
    def on_stop(self):
        """应用停止"""
        # 清理资源
        pass


def main():
    """移动端入口"""
    VoiceChangerMobileApp().run()


if __name__ == '__main__':
    main()
