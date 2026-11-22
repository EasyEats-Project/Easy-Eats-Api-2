package com.easyeats.api.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginaDto<T>(
        List<T> itens,
        long total,
        int pagina,
        int tamanho,
        int totalPaginas
) {
    public static <T> PaginaDto<T> de(Page<T> page) {
        return new PaginaDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages()
        );
    }
}
