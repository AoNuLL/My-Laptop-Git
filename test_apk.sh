#!/bin/bash

# AI 变声器 APK 自动化测试脚本

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                                                               ║"
echo "║          AI 变声器 APK 自动化测试                              ║"
echo "║                                                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

APK_PATH="/workspace/AIVoiceChanger/app/build/outputs/apk/debug/app-debug.apk"
TESTS_PASSED=0
TESTS_TOTAL=0

# 测试函数
run_test() {
    local test_name="$1"
    local test_command="$2"
    
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
    
    if eval "$test_command" > /dev/null 2>&1; then
        echo "✅ PASS: $test_name"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        echo "❌ FAIL: $test_name"
        return 1
    fi
}

echo "📦 APK 基础测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试 1: APK 文件存在
run_test "APK 文件存在" "test -f $APK_PATH"

# 测试 2: APK 文件大小 > 0
run_test "APK 文件大小" "test -s $APK_PATH"

# 测试 3: APK 是有效的 ZIP
run_test "ZIP 格式验证" "unzip -t $APK_PATH"

# 测试 4: 包含 AndroidManifest.xml
run_test "AndroidManifest.xml" "unzip -l $APK_PATH | grep -q AndroidManifest.xml"

# 测试 5: 包含 classes.dex
run_test "classes.dex" "unzip -l $APK_PATH | grep -q classes.dex"

echo ""
echo "📱 组件测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试 6: MainActivity 存在
run_test "MainActivity" "unzip -l $APK_PATH | grep -q 'MainActivity'"

# 测试 7: Fragment 存在
run_test "Fragments" "unzip -l $APK_PATH | grep -q 'Fragment'"

# 测试 8: ViewModel 存在
run_test "ViewModels" "unzip -l $APK_PATH | grep -q 'ViewModel'"

# 测试 9: 自定义 View (WaveformView)
run_test "WaveformView" "unzip -l $APK_PATH | grep -q 'WaveformView'"

# 测试 10: Adapter 存在
run_test "Adapters" "unzip -l $APK_PATH | grep -q 'Adapter'"

echo ""
echo "🎨 资源文件测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试 11: 布局文件
run_test "布局文件" "unzip -l $APK_PATH | grep -q 'res/layout/'"

# 测试 12: values 资源
run_test "values/strings.xml" "unzip -l $APK_PATH | grep -q 'values/strings.xml'"

# 测试 13: colors.xml
run_test "values/colors.xml" "unzip -l $APK_PATH | grep -q 'values/colors.xml'"

# 测试 14: 应用图标
run_test "应用图标" "unzip -l $APK_PATH | grep -q 'mipmap.*ic_launcher.png'"

# 测试 15: 菜单文件
run_test "菜单文件" "unzip -l $APK_PATH | grep -q 'menu/menu_bottom_nav.xml'"

echo ""
echo "🔐 权限配置测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 提取 manifest 并检查权限
unzip -p $APK_PATH AndroidManifest.xml > /tmp/AndroidManifest.xml 2>/dev/null || true

# 测试 16: 录音权限
run_test "RECORD_AUDIO 权限" "grep -q 'RECORD_AUDIO' /tmp/AndroidManifest.xml || jar xf $APK_PATH AndroidManifest.xml && strings AndroidManifest.xml | grep -q 'RECORD_AUDIO'"

# 测试 17: 存储权限
run_test "READ_EXTERNAL_STORAGE" "grep -q 'READ_EXTERNAL_STORAGE' /tmp/AndroidManifest.xml || strings AndroidManifest.xml | grep -q 'READ_EXTERNAL_STORAGE'"

# 测试 18: FileProvider
run_test "FileProvider 配置" "unzip -l $APK_PATH | grep -q 'xml/file_paths.xml'"

echo ""
echo "🛠️ 库文件测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试 19: Kotlin 协程
run_test "Kotlin 协程" "unzip -l $APK_PATH | grep -q 'kotlinx-coroutines'"

# 测试 20: Room 数据库
run_test "Room 数据库" "unzip -l $APK_PATH | grep -q 'androidx/room'"

# 测试 21: Media3
run_test "Media3/ExoPlayer" "unzip -l $APK_PATH | grep -q 'androidx/media3'"

# 测试 22: Material Components
run_test "Material Components" "unzip -l $APK_PATH | grep -q 'material'"

# 测试 23: Lifecycle ViewModel
run_test "ViewModel 库" "unzip -l $APK_PATH | grep -q 'lifecycle-viewmodel'"

echo ""
echo "📊 代码结构测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试 24: 录音模块
run_test "Recorder 模块" "unzip -l $APK_PATH | grep -q 'AudioRecorder'"

# 测试 25: 播放器模块
run_test "Player 模块" "unzip -l $APK_PATH | grep -q 'AudioPlayer'"

# 测试 26: 处理器模块
run_test "Processor 模块" "unzip -l $APK_PATH | grep -q 'AudioProcessor'"

# 测试 27: 导出模块
run_test "Export 模块" "unzip -l $APK_PATH | grep -q 'ExportManager'"

# 测试 28: 数据库
run_test "Room Database" "unzip -l $APK_PATH | grep -q 'AppDatabase'"

echo ""
echo "🔧 配置文件测试"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试 29: build.gradle.kts
run_test "Gradle 配置" "test -f /workspace/AIVoiceChanger/app/build.gradle.kts"

# 测试 30: AndroidManifest.xml
run_test "Manifest 配置" "test -f /workspace/AIVoiceChanger/app/src/main/AndroidManifest.xml"

# 测试 31: 文档完整性
run_test "项目文档" "test -f /workspace/AIVoiceChanger/README.md"

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                                                               ║"
echo "║  测试总结                                                     ║"
echo "║                                                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "通过：$TESTS_PASSED / $TESTS_TOTAL"
PASS_RATE=$((TESTS_PASSED * 100 / TESTS_TOTAL))
echo "通过率：${PASS_RATE}%"
echo ""

if [ $TESTS_PASSED -eq $TESTS_TOTAL ]; then
    echo "✅ 所有测试通过！APK 质量优秀"
    echo ""
    echo "📦 APK 信息:"
    ls -lh $APK_PATH
    echo ""
    echo "📱 安装到设备:"
    echo "  adb install -r $APK_PATH"
    exit 0
else
    echo "⚠️  部分测试失败，建议检查"
    exit 1
fi
