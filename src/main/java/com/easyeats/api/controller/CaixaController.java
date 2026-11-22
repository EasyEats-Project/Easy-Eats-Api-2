package com.easyeats.api.controller;

import com.easyeats.api.dto.AberturaCaixaDto;
import com.easyeats.api.dto.CaixaDto;
import com.easyeats.api.dto.FechamentoCaixaDto;
import com.easyeats.api.dto.FiltroLancamentosDto;
import com.easyeats.api.dto.MovimentacaoCaixaDto;
import com.easyeats.api.dto.PaginaDto;
import com.easyeats.api.dto.RelatorioFechamentoDto;
import com.easyeats.api.dto.ResumoLancamentosDto;
import com.easyeats.api.entity.StatusCaixaEnum;
import com.easyeats.api.entity.TipoMovimentacaoCaixaEnum;
import com.easyeats.api.security.UsuarioAutenticado;
import com.easyeats.api.service.CaixaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/caixa")
@RequiredArgsConstructor
public class CaixaController {

    private final CaixaService caixaService;

    @PostMapping("/abrir")
    public ResponseEntity<CaixaDto> abrir(@Valid @RequestBody AberturaCaixaDto dto,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(caixaService.abrir(dto, UsuarioAutenticado.de(authentication)));
    }

    @GetMapping("/atual")
    public ResponseEntity<CaixaDto> buscarAtual() {
        return caixaService.buscarAtual()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping
    public ResponseEntity<PaginaDto<CaixaDto>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) StatusCaixaEnum status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            Authentication authentication) {
        return ResponseEntity.ok(caixaService.listar(inicio, fim, usuario, status, pagina, tamanho,
                UsuarioAutenticado.de(authentication)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaixaDto> buscarPorId(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(caixaService.buscarPorId(id, UsuarioAutenticado.de(authentication)));
    }

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<MovimentacaoCaixaDto> registrarMovimentacao(@PathVariable Long id,
            @Valid @RequestBody MovimentacaoCaixaDto dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(caixaService.registrarMovimentacao(id, dto, UsuarioAutenticado.de(authentication)));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<CaixaDto> fechar(@PathVariable Long id,
            @Valid @RequestBody FechamentoCaixaDto dto, Authentication authentication) {
        return ResponseEntity.ok(caixaService.fechar(id, dto, UsuarioAutenticado.de(authentication)));
    }

    @GetMapping("/{id}/relatorio-fechamento")
    public ResponseEntity<RelatorioFechamentoDto> relatorioFechamento(@PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(
                caixaService.gerarRelatorioFechamento(id, UsuarioAutenticado.de(authentication)));
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<PaginaDto<MovimentacaoCaixaDto>> listarMovimentacoes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) TipoMovimentacaoCaixaEnum tipo,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) Long caixaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            Authentication authentication) {
        FiltroLancamentosDto filtro = new FiltroLancamentosDto(inicio, fim, tipo, usuario, caixaId);
        return ResponseEntity.ok(caixaService.listarMovimentacoes(filtro, pagina, tamanho,
                UsuarioAutenticado.de(authentication)));
    }

    @GetMapping("/movimentacoes/resumo")
    public ResponseEntity<ResumoLancamentosDto> resumirMovimentacoes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) TipoMovimentacaoCaixaEnum tipo,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) Long caixaId,
            Authentication authentication) {
        FiltroLancamentosDto filtro = new FiltroLancamentosDto(inicio, fim, tipo, usuario, caixaId);
        return ResponseEntity.ok(caixaService.resumirMovimentacoes(filtro,
                UsuarioAutenticado.de(authentication)));
    }
}
