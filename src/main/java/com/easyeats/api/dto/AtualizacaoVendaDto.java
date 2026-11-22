package com.easyeats.api.dto;

import jakarta.validation.constraints.Size;

public record AtualizacaoVendaDto(
        @Size(max = 255, message = "O nome do cliente deve ter no máximo 255 caracteres")
        String nomeCliente,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao
) {
}