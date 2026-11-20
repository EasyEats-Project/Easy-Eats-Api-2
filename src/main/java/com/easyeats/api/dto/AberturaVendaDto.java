package com.easyeats.api.dto;

import jakarta.validation.constraints.Size;

public record AberturaVendaDto(
        Long mesaId,                // opcional: venda sem mesa (delivery, retirada)

        @Size(max = 255, message = "O nome do cliente deve ter no máximo 255 caracteres")
        String nomeCliente,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao
) {
}