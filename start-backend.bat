@echo off
echo ========================================================
echo Starting TaskPulse Backend (Spring Boot 3.3.3 + H2 Dev)
echo ========================================================
cd /d "%~dp0backend"
call mvn spring-boot:run -Dspring-boot.run.profiles=dev
pause
