package com.easyeats.api.repository;

import com.easyeats.api.dto.FiltroVendaDto;
import com.easyeats.api.entity.StatusVendaEnum;
import com.easyeats.api.entity.Venda;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long>, JpaSpecificationExecutor<Venda> {

    @EntityGraph(attributePaths = "mesa")
    Optional<Venda> findByMesaIdAndStatus(Long mesaId, StatusVendaEnum status);

    boolean existsByMesaIdAndStatus(Long mesaId, StatusVendaEnum status);

    // Mesa carregada junto (evita 1 consulta por venda ao montar a ocupação das mesas)
    @EntityGraph(attributePaths = "mesa")
    List<Venda> findByStatus(StatusVendaEnum status);

    // Bloqueia a linha da venda até o fim da transação (serializa pagamentos, finalização e cancelamento)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Venda v where v.id = :id")
    Optional<Venda> buscarPorIdParaAtualizar(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = "mesa")
    Page<Venda> findAll(Specification<Venda> spec, Pageable pageable);

    static Specification<Venda> comFiltros(FiltroVendaDto filtro) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (filtro.status() != null) {
                filtros.add(cb.equal(root.get("status"), filtro.status()));
            }
            if (filtro.mesaId() != null) {
                filtros.add(cb.equal(root.get("mesa").get("id"), filtro.mesaId()));
            }
            if (filtro.usuario() != null && !filtro.usuario().isBlank()) {
                filtros.add(cb.or(
                        cb.equal(root.get("usuarioAbertura"), filtro.usuario()),
                        cb.equal(root.get("usuarioFechamento"), filtro.usuario())));
            }
            if (filtro.inicio() != null) {
                filtros.add(cb.greaterThanOrEqualTo(
                        root.<LocalDateTime>get("dataAbertura"), filtro.inicio().atStartOfDay()));
            }
            if (filtro.fim() != null) {
                filtros.add(cb.lessThan(
                        root.<LocalDateTime>get("dataAbertura"), filtro.fim().plusDays(1).atStartOfDay()));
            }
            return cb.and(filtros.toArray(Predicate[]::new));
        };
    }
}