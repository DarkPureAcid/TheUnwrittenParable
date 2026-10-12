@echo off
echo Setting up Forge Gradle for The Unwritten Parable...
echo.

if not exist "gradlew.bat" (
    echo Downloading Forge MDK...
    echo Please download the Forge MDK from https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html
    echo Extract it and copy the following files to this directory:
    echo - gradlew.bat
    echo - gradlew
    echo - gradle/ folder
    echo.
    echo Then run this script again.
    pause
    exit /b
)

echo Running Gradle setup...
call gradlew.bat genEclipseRuns
call gradlew.bat genIntellijRuns

echo.
echo Setup complete! You can now import this project into your IDE.
pause