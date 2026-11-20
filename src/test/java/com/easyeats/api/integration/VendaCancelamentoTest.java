package com.easyeats.api.integration;

import com.easyeats.api.entity.StatusPedidoEnum;
import com.easyeats.api.support.VendaTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cancelamento de venda (Administrador e Gerente, com motivo): cancela todas as comandas e libera a mesa.
 * O estoque só volta para comandas cujo preparo não começou (baixaram o estoque e voltaram a ABERTO);
 * comandas em preparo ou prontas já consumiram os ingredientes.
 */
@DisplayName("Venda: cancelamento e devolução de estoque")
class VendaCancelamentoTest extends VendaTestBase {

    private StatusPedidoEnum statusDa(Long comandaId) {
        return pedidoRepository.findById(comandaId).orElseThrow().getStatus();
    }

    @Test
    @DisplayName("cancelar: venda CANCELADA, comandas canceladas, motivo e usuário gravados, mesa livre")
    void cancelarVenda() throws Exception {
        Long comandaA = criarComanda(mesa, 1);
        Long comandaB = criarComanda(mesa, 2);
        Long vendaId = vendaAbertaDa(mesa);

        cancelar(vendaId, "Cliente desistiu")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"))
                .andExpect(jsonPath("$.motivoCancelamento").value("Cliente desistiu"))
                .andExpect(jsonPath("$.usuarioFechamento").value(USUARIO_PADRAO))
                .andExpect(jsonPath("$.dataFechamento").isNotEmpty())
                .andExpect(jsonPath("$.valorTotal").value(0.0))
                .andExpect(jsonPath("$.comandas[0].status").value("CANCELADO"))
                .andExpect(jsonPath("$.comandas[1].status").value("CANCELADO"));

        assertThat(statusDa(comandaA)).isEqualTo(StatusPedidoEnum.CANCELADO);
        assertThat(statusDa(comandaB)).isEqualTo(StatusPedidoEnum.CANCELADO);
        assertThat(ocupacaoDe(mesa).get("ocupada")).isEqualTo(false);
    }

    @Test
    @DisplayName("gerente pode cancelar")
    void gerenteCancela() throws Exception {
        criarComanda(mesa, 1);
        Long vendaId = vendaAbertaDa(mesa);

        postComo(tokenDe("gerente.teste", "GERENTE"), "/vendas/%d/cancelar".formatted(vendaId),
                "{\"motivo\": \"Erro de lançamento\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioFechamento").value("gerente.teste"));
    }

    @Test
    @DisplayName("funcionário não pode cancelar: 403 e nada muda")
    void funcionarioNaoCancela() throws Exception {
        Long comandaId = criarComanda(mesa, 1);
        Long vendaId = vendaAbertaDa(mesa);

        postComo(tokenDe("func.teste", "FUNCIONARIO"), "/vendas/%d/cancelar".formatted(vendaId),
                "{\"motivo\": \"Teste\"}")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("Operação permitida apenas para Administrador ou Gerente"));

        assertThat(statusDa(comandaId)).isEqualTo(StatusPedidoEnum.ABERTO);
        assertThat(ocupacaoDe(mesa).get("ocupada")).isEqualTo(true);
    }

    @Test
    @DisplayName("cancelar sem motivo: 400 e a venda continua aberta")
    void cancelarSemMotivo() throws Exception {
        criarComanda(mesa, 1);
        Long vendaId = vendaAbertaDa(mesa);

        assertErroValidacao(post("/vendas/%d/cancelar".formatted(vendaId), "{}"),
                "motivo: Informe o motivo do cancelamento");
        assertErroValidacao(post("/vendas/%d/cancelar".formatted(vendaId), "{\"motivo\": \"   \"}"),
                "motivo: Informe o motivo do cancelamento");
        get("/vendas/" + vendaId).andExpect(jsonPath("$.status").value("ABERTA"));
    }

    // ------------------------------------------------------------------ estoque

    @Test
    @DisplayName("comanda em preparo já consumiu os ingredientes: cancelar a venda não devolve estoque")
    void emPreparoNaoDevolve() throws Exception {
        Long comandaId = criarComanda(mesa, 2);
        mudarStatus(comandaId, "EM_PREPARO").andExpect(status().isOk());
        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("98"));

        cancelar(vendaAbertaDa(mesa), "Cliente foi embora").andExpect(status().isOk());

        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("98"));
        assertThat(statusDa(comandaId)).isEqualTo(StatusPedidoEnum.CANCELADO);
    }

    @Test
    @DisplayName("comanda pronta também não devolve estoque")
    void prontaNaoDevolve() throws Exception {
        Long comandaId = criarComanda(mesa, 3);
        mudarStatus(comandaId, "EM_PREPARO").andExpect(status().isOk());
        mudarStatus(comandaId, "FINALIZADO").andExpect(status().isOk());

        cancelar(vendaAbertaDa(mesa), "Pedido errado").andExpect(status().isOk());

        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("97"));
    }

    @Test
    @DisplayName("comanda que baixou estoque e voltou a ABERTO (preparo não começou): o estoque é devolvido")
    void preparoNaoIniciadoDevolve() throws Exception {
        Long comandaId = criarComanda(mesa, 2);
        mudarStatus(comandaId, "EM_PREPARO").andExpect(status().isOk());
        mudarStatus(comandaId, "ABERTO").andExpect(status().isOk());
        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("98"));
        assertThat(pedidoRepository.findById(comandaId).orElseThrow().isEstoqueBaixado()).isTrue();

        cancelar(vendaAbertaDa(mesa), "Cliente desistiu").andExpect(status().isOk());

        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(pedidoRepository.findById(comandaId).orElseThrow().isEstoqueBaixado()).isFalse();
    }

    @Test
    @DisplayName("comanda que nunca baixou estoque: cancelar não mexe no estoque")
    void nuncaBaixouNaoMexe() throws Exception {
        criarComanda(mesa, 5);

        cancelar(vendaAbertaDa(mesa), "Cliente desistiu").andExpect(status().isOk());

        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    @DisplayName("venda com comandas em estados diferentes: devolve só a do preparo não iniciado")
    void estadosMistos() throws Exception {
        Long emPreparo = criarComanda(mesa, 1);        // consome 1
        Long voltouAberto = criarComanda(mesa, 2);     // consome 2 e volta (devolve 2)
        Long cancelada = criarComanda(mesa, 4);        // cancelada antes: nada baixado
        mudarStatus(emPreparo, "EM_PREPARO").andExpect(status().isOk());
        mudarStatus(voltouAberto, "EM_PREPARO").andExpect(status().isOk());
        mudarStatus(voltouAberto, "ABERTO").andExpect(status().isOk());
        mudarStatus(cancelada, "CANCELADO").andExpect(status().isOk());
        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("97"));

        cancelar(vendaAbertaDa(mesa), "Mesa desfeita").andExpect(status().isOk());

        assertThat(estoqueDoPao()).isEqualByComparingTo(new BigDecimal("99"));
    }

    // ------------------------------------------------------------------ regras de estado

    @Test
    @DisplayName("venda finalizada não pode ser cancelada: 422")
    void finalizadaNaoCancela() throws Exception {
        abrirCaixa("100");
        Long vendaId = vendaDaNovaComanda(mesa, 1);
        pagar(vendaId, "PIX", PRECO_TEXTO).andExpect(status().isCreated());
        finalizar(vendaId).andExpect(status().isOk());

        assertRegraNegocio(cancelar(vendaId, "Tarde demais"), "Só é possível cancelar uma venda aberta");
        get("/vendas/" + vendaId).andExpect(jsonPath("$.status").value("FINALIZADA"));
    }

    @Test
    @DisplayName("cancelar duas vezes: 422")
    void cancelarDuasVezes() throws Exception {
        criarComanda(mesa, 1);
        Long vendaId = vendaAbertaDa(mesa);
        cancelar(vendaId, "Primeira").andExpect(status().isOk());

        assertRegraNegocio(cancelar(vendaId, "Segunda"), "Só é possível cancelar uma venda aberta");
    }

    @Test
    @DisplayName("venda cancelada trava as comandas e não aceita pagamento nem finalização")
    void canceladaTravada() throws Exception {
        abrirCaixa("100");
        Long comandaId = criarComanda(mesa, 1);
        Long vendaId = vendaAbertaDa(mesa);
        cancelar(vendaId, "Cliente desistiu").andExpect(status().isOk());

        assertRegraNegocio(mudarStatus(comandaId, "EM_PREPARO"), "A venda desta comanda foi cancelada");
        assertRegraNegocio(pagar(vendaId, "PIX", "1.00"), "A venda não está aberta");
        assertRegraNegocio(finalizar(vendaId), "A venda não está aberta");
        assertRegraNegocio(delete("/pedido/" + comandaId),
                "A venda desta comanda já foi cancelada; não é possível alterar a comanda");
    }

    @Test
    @DisplayName("depois de cancelada, uma nova comanda na mesa abre outra venda")
    void novaComandaAposCancelar() throws Exception {
        criarComanda(mesa, 1);
        Long primeira = vendaAbertaDa(mesa);
        cancelar(primeira, "Cliente desistiu").andExpect(status().isOk());

        Long segunda = vendaDaNovaComanda(mesa, 1);

        assertThat(segunda).isNotEqualTo(primeira);
        assertThat(ocupacaoDe(mesa).get("ocupada")).isEqualTo(true);
    }

    @Test
    @DisplayName("venda cancelada não lança nada no caixa")
    void canceladaNaoMovimentaCaixa() throws Exception {
        Long caixaId = abrirCaixa("100");
        Long vendaId = vendaDaNovaComanda(mesa, 1);
        pagarEmDinheiro(vendaId, "10.00", "10.00").andExpect(status().isCreated());

        cancelar(vendaId, "Cliente desistiu").andExpect(status().isOk());

        assertThat(movimentacaoCaixaRepository.findByCaixaIdOrderByDataMovimentacaoAscIdAsc(caixaId)).hasSize(1);
        get("/caixa/atual").andExpect(jsonPath("$.saldoAtual").value(100.0));
    }

    @Test
    @DisplayName("cancelar venda inexistente: 404")
    void cancelarInexistente() throws Exception {
        assertNaoEncontrado(cancelar(ID_INEXISTENTE, "Teste"), "Venda não encontrado: " + ID_INEXISTENTE);
    }
}
