package com.easyeats.api.dto;

import com.easyeats.api.entity.StatusVendaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaResumoDto(
        Long id,
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