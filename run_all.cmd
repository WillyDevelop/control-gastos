@echo off
echo ==========================================
echo   Iniciando Control de Gastos
echo ==========================================
echo.

echo Iniciando Backend en una nueva ventana...
start "Spring Boot Backend" cmd /c "cd backend && call run_backend.cmd"

echo Iniciando Frontend en una nueva ventana...
start "Angular Frontend" cmd /c "cd frontend && call run_frontend.cmd"

echo.
echo Los servidores se estan iniciando. 
echo - El frontend estara en http://localhost:4200
echo - El backend estara en http://localhost:8080
echo.
echo Puedes cerrar esta ventana, los servidores seguiran corriendo en sus propias ventanas.
pause
