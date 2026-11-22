package com.easyeats.api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AberturaCaixaDto(
        @NotNull(message = "O fundo de caixa é obrigatório")
        @PositiveOrZero(message = "O fundo de caixa não pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Use no máximo 2 casas decimais")
        BigDecimal fundoCaixa,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao
) {
}
