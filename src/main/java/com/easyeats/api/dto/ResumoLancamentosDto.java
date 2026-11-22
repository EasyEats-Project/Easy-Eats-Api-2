package com.easyeats.api.dto;

import java.math.BigDecimal;

public record ResumoLancamentosDto(
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldo
) {
}
