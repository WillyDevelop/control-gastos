@echo off
echo Iniciando Backend...
start cmd /k "cd backend && apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"

echo Iniciando Frontend...
start cmd /k "cd frontend && npm start"

echo Aplicacion iniciando... Abre http://localhost:4200 en tu navegador.
