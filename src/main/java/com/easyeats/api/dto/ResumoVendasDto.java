package com.easyeats.api.dto;

import java.math.BigDecimal;
import java.util.List;

/** Faturamento de um período: vendas finalizadas (por data de fechamento) e canceladas. */
public record ResumoVendasDto(
        BigDecimal totalFaturado,
        long quantidadeVendas,
        BigDecimal ticketMedio,
        long quantidadeCanceladas,
        List<TotalFormaPagamentoDto> porFormaPagamento,
        List<TotalDiaDto> porDia
) {
}