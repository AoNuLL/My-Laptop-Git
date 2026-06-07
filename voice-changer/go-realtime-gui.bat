@echo off
REM Voice Changer - 实时变声器启动脚本
REM ======================================

echo ================================================
echo    Voice Changer - 开源实时变声器 v2.1.4
echo ================================================
echo.

REM 检查 Python 环境
echo [1/3] 检查 Python 环境...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Python 环境，请先安装 Python 3.8+
    pause
    exit /b 1
)
echo [成功] Python 环境检查通过
echo.

REM 检查依赖
echo [2/3] 检查依赖包...
python -c "import customtkinter" >nul 2>&1
if %errorlevel% neq 0 (
    echo [提示] 正在安装依赖包，请稍候...
    pip install -r requirements.txt
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)
echo [成功] 依赖检查完成
echo.

REM 启动程序
echo [3/3] 启动变声器...
echo.

python main.py
if %errorlevel% neq 0 (
    echo.
    echo [错误] 程序运行出错
    pause
)
