package br.com.plastecno.service.test.gerador;

import org.junit.Assert;

import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.test.builder.EntidadeBuilder;
import br.com.plastecno.service.test.builder.ServiceBuilder;

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

	public Representada gerarRepresentada(TipoRelacionamento tipoRelacionamento) {
		Representada representada = eBuilder.buildRepresentada();
		representada.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);
		representada.setTipoRelacionamento(tipoRelacionamento);
		try {
			representadaService.inserir(representada);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}
		return representada;
	}

	public Representada gerarRevendedor() {
		return gerarRepresentada(TipoRelacionamento.REVENDA);
	}

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}
}
