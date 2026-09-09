@echo off
echo ========================================================
echo Starting TaskPulse Frontend (Vite + React 18 + TS)
echo ========================================================
cd /d "%~dp0frontend"
call npm run dev
pause
