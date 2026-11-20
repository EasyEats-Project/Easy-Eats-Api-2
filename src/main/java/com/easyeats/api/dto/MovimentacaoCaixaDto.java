package com.easyeats.api.dto;

import com.easyeats.api.entity.TipoMovimentacaoCaixaEnum;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoCaixaDto(
        Long id,
        Long caixaId,               // definido pela API, o que vier do cliente é ignorado

        @NotNull(message = "O tipo é obrigatório")
        TipoMovimentacaoCaixaEnum tipo,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Use no máximo 2 casas decimais")
        BigDecimal valor,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,

        String usuario,             // definido pela API (usuário autenticado)
        LocalDateTime dataMovimentacao
) {
}
