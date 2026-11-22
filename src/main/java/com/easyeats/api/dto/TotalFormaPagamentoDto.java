package com.easyeats.api.dto;

import com.easyeats.api.entity.FormaPagamentoEnum;

import java.math.BigDecimal;

public record TotalFormaPagamentoDto(
        FormaPagamentoEnum forma,
        BigDecimal total
) {
}