package com.easyeats.api.dto;

import com.easyeats.api.entity.StatusCaixaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CaixaDto(
        Long id,
        StatusCaixaEnum status,
        BigDecimal fundoCaixa,
        String usuarioAbertura,
        LocalDateTime dataAbertura,
        String observacaoAbertura,
        String usuarioFechamento,
        LocalDateTime dataFechamento,
        BigDecimal valorEsperado,
        BigDecimal valorContado,
        BigDecimal diferenca,
        String observacaoFechamento,
        BigDecimal saldoAtual       // calculado pela API: fundo + suprimentos - sangrias
) {
}
