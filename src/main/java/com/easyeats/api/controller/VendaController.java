package com.easyeats.api.controller;

import com.easyeats.api.dto.AberturaVendaDto;
import com.easyeats.api.dto.AtualizacaoVendaDto;
import com.easyeats.api.dto.CancelamentoVendaDto;
import com.easyeats.api.dto.FiltroVendaDto;
import com.easyeats.api.dto.OcupacaoMesaDto;
import com.easyeats.api.dto.PagamentoVendaDto;
import com.easyeats.api.dto.PaginaDto;
import com.easyeats.api.dto.VendaDto;
import com.easyeats.api.dto.VendaResumoDto;
import com.easyeats.api.entity.StatusVendaEnum;
import com.easyeats.api.security.UsuarioAutenticado;
import com.easyeats.api.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaDto> abrir(@Valid @RequestBody AberturaVendaDto dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendaService.abrir(dto, UsuarioAutenticado.de(authentication)));
    }

    @GetMapping
    public ResponseEntity<PaginaDto<VendaResumoDto>> listar(
            @RequestParam(required = false) StatusVendaEnum status,
            @RequestParam(required = false) Long mesaId,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            Authentication authentication) {
        FiltroVendaDto filtro = new FiltroVendaDto(status, mesaId, usuario, inicio, fim);
        return ResponseEntity.ok(vendaService.listar(filtro, pagina, tamanho, UsuarioAutenticado.de(authentication)));
    }

    @GetMapping("/ocupacao-mesas")
    public ResponseEntity<List<OcupacaoMesaDto>> ocupacaoDasMesas() {
        return ResponseEntity.ok(vendaService.ocupacaoDasMesas());
    }

    @GetMapping("/mesa/{mesaId}/aberta")
    public ResponseEntity<VendaDto> buscarAbertaDaMesa(@PathVariable Long mesaId) {
        return vendaService.buscarAbertaDaMesa(mesaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendaDto> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizacaoVendaDto dto) {
        return ResponseEntity.ok(vendaService.atualizar(id, dto));
    }

    @PostMapping("/{id}/pagamentos")
    public ResponseEntity<VendaDto> registrarPagamento(@PathVariable Long id,
            @Valid @RequestBody PagamentoVendaDto dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendaService.registrarPagamento(id, dto, UsuarioAutenticado.de(authentication)));
    }

    @DeleteMapping("/{id}/pagamentos/{pagamentoId}")
    public ResponseEntity<VendaDto> removerPagamento(@PathVariable Long id, @PathVariable Long pagamentoId) {
        return ResponseEntity.ok(vendaService.removerPagamento(id, pagamentoId));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<VendaDto> finalizar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(vendaService.finalizar(id, UsuarioAutenticado.de(authentication)));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<VendaDto> cancelar(@PathVariable Long id,
            @Valid @RequestBody CancelamentoVendaDto dto, Authentication authentication) {
        return ResponseEntity.ok(vendaService.cancelar(id, dto, UsuarioAutenticado.de(authentication)));
    }
}