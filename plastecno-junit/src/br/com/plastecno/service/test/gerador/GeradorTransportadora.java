package br.com.plastecno.service.test.gerador;

import org.junit.Assert;

import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.test.builder.EntidadeBuilder;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class GeradorTransportadora {

	private static GeradorTransportadora gerador;

	public static GeradorTransportadora getInstance() {
		if (gerador == null) {
			gerador = new GeradorTransportadora();
		}
		return gerador;
	}

	private EntidadeBuilder eBuilder = EntidadeBuilder.getInstance();

	private TransportadoraService transportadoraService;

	private GeradorTransportadora() {
		transportadoraService = ServiceBuilder.buildService(TransportadoraService.class);
	}

	public Transportadora gerarTransportadora() {
		Integer idTransportadora = null;
		try {
			idTransportadora = transportadoraService.inserir(eBuilder.buildTransportadora());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return transportadoraService.pesquisarTransportadoraLogradouroById(idTransportadora);
	}

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
