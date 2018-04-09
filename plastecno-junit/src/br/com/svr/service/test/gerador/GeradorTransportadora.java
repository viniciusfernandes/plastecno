package br.com.svr.service.test.gerador;

import java.util.List;

import org.junit.Assert;

import br.com.svr.service.TransportadoraService;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.EntidadeBuilder;
import br.com.svr.service.test.builder.ServiceBuilder;

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
		Integer idTrans = null;
		List<Transportadora> lTransp = transportadoraService.pesquisarTransportadoraAtiva();
		if (lTransp.size() >= 1) {
			return lTransp.get(0);
		}
		try {
			idTrans = transportadoraService.inserir(eBuilder.buildTransportadora());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return transportadoraService.pesquisarTransportadoraLogradouroById(idTrans);
	}

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
