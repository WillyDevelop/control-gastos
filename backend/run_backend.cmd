@echo off
setlocal

:: Configurar la versión de Maven a descargar
set MAVEN_VERSION=3.9.6
set MAVEN_DIR=%~dp0apache-maven-%MAVEN_VERSION%

:: Verificar si Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: No se encontró Java instalado o no está en el PATH.
    echo Por favor, instala el JDK de Java y asegúrate de agregarlo a las variables de entorno.
    pause
    exit /b 1
)

:: Descargar y extraer Maven si no existe en la carpeta actual
if not exist "%MAVEN_DIR%" (
    echo [1/3] Descargando Apache Maven %MAVEN_VERSION%...
    powershell -Command "Invoke-WebRequest -Uri https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip -OutFile maven.zip"
    
    echo [2/3] Extrayendo Maven, esto puede tomar unos segundos...
    powershell -Command "Expand-Archive -Path maven.zip -DestinationPath '%~dp0' -Force"
    
    REM Limpiar el archivo zip
    del maven.zip
    echo Maven descargado y configurado correctamente.
) else (
    echo Usando Apache Maven %MAVEN_VERSION% local...
)

:: Agregar Maven temporalmente al PATH de esta consola
set PATH=%MAVEN_DIR%\bin;%PATH%

echo.
echo [3/3] Iniciando el servidor de Spring Boot...
echo.

:: Ejecutar el proyecto
call mvn spring-boot:run

pause
