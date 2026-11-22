package com.easyeats.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TotalDiaDto(
        LocalDate dia,
        Long quantidade,
        BigDecimal total
) {
}