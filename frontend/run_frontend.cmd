@echo off
echo [1/2] Instalando/Verificando dependencias de Node...
call npm install

echo.
echo [2/2] Iniciando el servidor de Angular...
echo El servidor estara disponible en http://localhost:4200/
call npm start

pause
