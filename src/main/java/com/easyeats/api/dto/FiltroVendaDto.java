package com.easyeats.api.dto;

import com.easyeats.api.entity.StatusVendaEnum;

import java.time.LocalDate;

/** Filtros opcionais da listagem de vendas. Campos nulos não filtram. */
public record FiltroVendaDto(
        StatusVendaEnum status,
        Long mesaId,
        String usuario,             // quem abriu ou finalizou
        LocalDate inicio,           // data de abertura, inclusivo
        LocalDate fim               // data de abertura, inclusivo
) {
}