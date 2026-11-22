package com.easyeats.api.dto;

import java.math.BigDecimal;

/** Situação de uma mesa: ocupada enquanto existir uma venda aberta nela. */
public record OcupacaoMesaDto(
        Long mesaId,
        Integer numero,
        boolean ocupada,
        Long vendaId,
        String nomeCliente,
        Long quantidadeComandas,
        BigDecimal valorParcial
) {
}