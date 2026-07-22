package com.app.controlgastos.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SeguridadRequestDTO {
    @NotBlank(message = "La contraseña actual es obligatoria para confirmar los cambios")
    private String currentPassword;

    private String newEmail;
    private String newPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
