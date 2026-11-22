package com.easyeats.api.dto;

import com.easyeats.api.entity.FormaPagamentoEnum;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoVendaDto(
        Long id,

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamentoEnum forma,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Use no máximo 2 casas decimais")
        BigDecimal valor,

        // Só para dinheiro: quanto o cliente entregou. Se omitido, assume o próprio valor.
        @Positive(message = "O valor recebido deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Use no máximo 2 casas decimais")
        BigDecimal valorRecebido,

        BigDecimal troco,           // calculado pela API
        String usuario,             // definido pela API (usuário autenticado)
        LocalDateTime dataPagamento
) {
}