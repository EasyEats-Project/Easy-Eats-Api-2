package com.easyeats.api.dto;

import com.easyeats.api.entity.StatusVendaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaDto(
        Long id,
        Long mesaId,
        Integer numeroMesa,
        StatusVendaEnum status,
        String nomeCliente,
        String observacao,
        String usuarioAbertura,
        LocalDateTime dataAbertura,
        String usuarioFechamento,
        LocalDateTime dataFechamento,
        BigDecimal valorTotal,      // aberta: soma das comandas não canceladas; finalizada: valor gravado
        BigDecimal valorPago,
        BigDecimal valorRestante,
        Long caixaId,
        String motivoCancelamento,
        List<PedidoDto> comandas,
        List<PagamentoVendaDto> pagamentos
) {
}