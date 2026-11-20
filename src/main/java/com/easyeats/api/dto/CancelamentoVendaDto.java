package com.easyeats.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelamentoVendaDto(
        @NotBlank(message = "Informe o motivo do cancelamento")
        @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres")
        String motivo
) {
}