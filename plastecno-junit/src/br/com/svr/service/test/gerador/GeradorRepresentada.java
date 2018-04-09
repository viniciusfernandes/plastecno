package br.com.svr.service.test.gerador;

import java.util.List;

import org.junit.Assert;

import br.com.svr.service.RepresentadaService;
import br.com.svr.service.constante.TipoApresentacaoIPI;
import br.com.svr.service.constante.TipoRelacionamento;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.EntidadeBuilder;
import br.com.svr.service.test.builder.ServiceBuilder;

public class GeradorRepresentada {
	private static GeradorRepresentada gerador;

	public static GeradorRepresentada getInstance() {
		if (gerador == null) {
			gerador = new GeradorRepresentada();
		}
		return gerador;
	}

	private EntidadeBuilder eBuilder = EntidadeBuilder.getInstance();

	RepresentadaService representadaService;

	private GeradorRepresentada() {
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);
	}

	public Representada gerarFornecedor() {
		return gerarRepresentada(TipoRelacionamento.FORNECIMENTO);
	}

	public Representada gerarRepresentada(TipoRelacionamento tpRelac) {
		if (tpRelac == null) {
			throw new IllegalStateException("Nao eh possivel gerar uma representada com tipo de relacionamento nulo;");
		}
		List<Representada> lRep = null;
		Representada r = null;
		if (tpRelac.isFornecimento() && !(lRep = representadaService.pesquisarFornecedorAtivo()).isEmpty()) {
			return lRep.get(0);
		} else if (tpRelac.isRevenda() && (r = representadaService.pesquisarRevendedor()) != null) {
			return r;

		} else if (tpRelac.isRepresentacao() && !(lRep = representadaService.pesquisarRepresentadaAtiva()).isEmpty()) {
			return lRep.get(0);
		}

		r = eBuilder.buildRepresentada();
		r.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);
		r.setTipoRelacionamento(tpRelac);
		try { 
			r.setId(representadaService.inserir(r));
		} catch (BusinessException e3) {
			printMensagens(e3);
		}
		return r;
	}

	public Representada gerarRevendedor() {
		return gerarRepresentada(TipoRelacionamento.REVENDA);
	}

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}
}
