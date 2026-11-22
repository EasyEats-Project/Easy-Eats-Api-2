package com.easyeats.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelamentoVendaDto(
        @NotBlank(message = "Informe o motivo do cancelamento")
        @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres")
        String motivo,

        // Devolve ao estoque os itens de comandas que baixaram o estoque mas ainda não foram usados
        // (ABERTO ou EM_PREPARO). Exige a senha do gerente (do usuário autenticado).
        Boolean devolverEstoque,

        String senhaGerente
) {
}