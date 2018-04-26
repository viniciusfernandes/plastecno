package br.com.svr.service.test;

import org.junit.Test;

import br.com.svr.service.RamoAtividadeService;
import br.com.svr.service.entity.RamoAtividade;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.ServiceBuilder;

public class JPATeste extends AbstractTest {
	private RamoAtividadeService ramoAtividadeService;

	public JPATeste() {
		ramoAtividadeService = ServiceBuilder.buildService(RamoAtividadeService.class);
	}

	@Test
	public void testInicial() {
		RamoAtividade r = new RamoAtividade();
		r.setSigla("XXX");
		r.setDescricao("TEsteee");

		try {
			r = ramoAtividadeService.inserir(r);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		r = new RamoAtividade();
		r.setSigla("zzz");
		r.setDescricao("zzzTEsteee");
		try {
			r = ramoAtividadeService.inserir(r);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		System.out.println("inicio..." + r.getId());
	}
}
