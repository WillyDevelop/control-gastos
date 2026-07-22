# Walkthrough: Sistema de Verificación y Recuperación (Tokens Temporales)

Se han implementado con éxito los flujos de seguridad de **Verificación de Correo Electrónico al Registrarse** y **Recuperación de Contraseña ("Olvidé mi contraseña")** utilizando Spring Boot 3.x y Angular 17+.

---

## Cambios Realizados

### 1. Backend (Spring Boot 3.x)

- **Limpieza de Código Antiguo**: Se eliminaron las antiguas clases redundantes `VerificationToken.java`, `PasswordResetToken.java`, `VerificationTokenRepository.java` y `PasswordResetTokenRepository.java`.
- **Nuevas Entidades JPA**:
  - `TokenVerificacion.java` en `com.app.controlgastos.model` para guardar tokens de registro vinculados `@OneToOne` a `Usuario` (expira en 24 horas).
  - `TokenRecuperacion.java` en `com.app.controlgastos.model` para guardar tokens de restablecimiento vinculados `@OneToOne` a `Usuario` (expira en 15 minutos).
- **Nuevos Repositorios**:
  - `TokenVerificacionRepository.java` y `TokenRecuperacionRepository.java` en `com.app.controlgastos.repository`.
- **DTO de Restablecimiento**:
  - `RestablecerPasswordRequest.java` que recibe `token`, `nuevaPassword` y `confirmarPassword`.
- **Lógica de AuthService.java**:
  - `registrar`: Crea el usuario con `activo = false`, crea el token de verificación de 24 horas y envía un correo con el formato HTML neo-brutalista apuntando a `http://localhost:4200/verificar-cuenta?token=XYZ`.
  - `verificar`: Valida la vigencia del token de verificación, marca al usuario como `activo = true` y elimina el token.
  - `forgotPassword`: Genera un token de recuperación para restablecer la contraseña si el email existe (expiración estricta de 15 minutos) y envía el correo con formato HTML neo-brutalista apuntando a `http://localhost:4200/restablecer-password?token=XYZ`.
  - `resetPassword`: Verifica que las contraseñas coincidan y que el token sea válido/no haya expirado. Encripta la nueva clave mediante `PasswordEncoder`, la persiste y elimina el token de recuperación.
- **Endpoints de AuthController.java**:
  - `POST /api/v1/auth/registrar`
  - `GET /api/v1/auth/confirmar?token=XYZ`
  - `POST /api/v1/auth/olvido-password`
  - `POST /api/v1/auth/restablecer`

---

### 2. Frontend (Angular 17+)

- **Servicio de Autenticación (`auth.service.ts`)**: Se actualizaron y mapearon correctamente todas las llamadas HTTP correspondientes a los nuevos endpoints. El método de confirmación realiza ahora una llamada `GET`.
- **Rutas (`app.routes.ts`)**:
  - Se configuró la ruta `/verificar-cuenta` cargando de forma diferida (lazy load) el componente `ConfirmarCuentaComponent`.
  - Se configuró la ruta `/restablecer-password` cargando de forma diferida el componente `ResetPasswordComponent`.
- **ConfirmarCuentaComponent** (`/verificar-cuenta`):
  - Captura reactivamente el `token` del query parameter.
  - Muestra un estado de carga neo-brutalista.
  - Si el backend confirma la verificación con éxito, renderiza una tarjeta con bordes negros gruesos (`border-2 border-brand-dark`), sombras marcadas y un botón llamativo para ir al `/login` que dice *"¡Correo verificado, bienvenido! Tu cuenta está activa."*.
  - En caso de fallo o token expirado, muestra una caja de error con la misma estética.
- **ResetPasswordComponent** (`/restablecer-password`):
  - Captura el token del query param.
  - Formulario reactivo con validación de coincidencia de contraseñas en tiempo real (`passwordMatchValidator`).
  - Envía la información al backend y redirige al `/login` con un temporizador tras confirmación exitosa.

---

## Verificación de Compilación

1. **Compilación del Backend (Maven)**:
   Se compiló con éxito el backend utilizando la instalación de Apache Maven local en el workspace:
   `BUILD SUCCESS` obtenido.
2. **Compilación del Frontend (Angular CLI)**:
   Se ejecutó `ng build` exitosamente en la aplicación de frontend:
   `Application bundle generation complete` obtenido, con los archivos compilados con éxito y guardados en `dist/frontend`.
