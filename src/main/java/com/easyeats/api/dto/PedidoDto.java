package com.easyeats.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.easyeats.api.entity.TipoVendaEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PedidoDto(
        Long id,
        LocalDateTime dataCriacao,
        LocalDateTime dataAlteracao,
        Long mesaId,
        Long vendaId,               // na criação: opcional, para incluir a comanda numa retirada já aberta
        TipoVendaEnum tipoVenda,    // MESA (padrão), RETIRADA ou DELIVERY (ainda indisponível)
        String cliente,
        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao,
        String status,
        BigDecimal valorTotal,

        @NotEmpty(message = "O pedido deve ter ao menos um item")
        @Valid
        List<ItemPedidoDto> itens
) {
}
