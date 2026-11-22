package com.easyeats.api.dto;

import com.easyeats.api.entity.TipoVendaEnum;
import jakarta.validation.constraints.Size;

public record AberturaVendaDto(
        // Opcional: sem tipo, vale MESA quando há mesaId e RETIRADA quando não há
        TipoVendaEnum tipo,

        Long mesaId,                // obrigatório para MESA; não se usa em RETIRADA

        @Size(max = 255, message = "O nome do cliente deve ter no máximo 255 caracteres")
        String nomeCliente,         // obrigatório para RETIRADA

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao
) {
}