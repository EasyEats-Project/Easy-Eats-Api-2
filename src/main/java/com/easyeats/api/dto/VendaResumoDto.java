package com.easyeats.api.dto;

import com.easyeats.api.entity.StatusVendaEnum;
import com.easyeats.api.entity.TipoVendaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaResumoDto(
        Long id,
        TipoVendaEnum tipo,
        Long mesaId,
        Integer numeroMesa,
        StatusVendaEnum status,
        String nomeCliente,
        String usuarioAbertura,
        LocalDateTime dataAbertura,
        String usuarioFechamento,
        LocalDateTime dataFechamento,
        BigDecimal valorTotal
) {
}