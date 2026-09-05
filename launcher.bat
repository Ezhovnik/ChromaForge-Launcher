@echo off
chcp 65001 >nul 2>&1
java -Dfile.encoding=UTF-8 -jar "%~dp0target\launcher-0.1.0.jar" %*
