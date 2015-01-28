package br.com.plastecno.service.relatorio;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioClienteRamoAtividade;
import br.com.plastecno.service.wrapper.RelatorioCompraPendente;
import br.com.plastecno.service.wrapper.RelatorioVendaPeriodo;
import br.com.plastecno.service.wrapper.RelatorioVendaVendedorByRepresentada;

@Local
public interface RelatorioService {

    RelatorioClienteRamoAtividade gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade) throws BusinessException;

    RelatorioCompraPendente gerarRelatorioCompraPendente(Date dataInicial, Date dataFinal, Integer idRepresentada);

    RelatorioVendaPeriodo gerarRelatorioVendaPeriodo(Periodo periodo) throws BusinessException;

    RelatorioVendaVendedorByRepresentada gerarRelatorioVendaVendedor(boolean orcamento, Periodo periodo,
            Integer idVendedor) throws BusinessException;

    List<Cliente> pesquisarClienteByIdVendedor(Integer idVendedor);

		List<Pedido> pesquisarEntregas(Periodo periodo) throws InformacaoInvalidaException;
}
