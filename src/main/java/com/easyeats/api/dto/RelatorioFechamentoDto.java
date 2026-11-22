package com.easyeats.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioFechamentoDto(
        CaixaDto caixa,
        BigDecimal totalSuprimentos,
        BigDecimal totalSangrias,
        BigDecimal totalVendas,
        List<MovimentacaoCaixaDto> movimentacoes
) {
}
