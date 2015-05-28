package br.com.plastecno.service.relatorio;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.ComissaoVendaWrapper;
import br.com.plastecno.service.wrapper.ReceitaWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioClienteRamoAtividade;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.service.wrapper.RelatorioVendaVendedorByRepresentada;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;

@Local
public interface RelatorioService {

	RelatorioClienteRamoAtividade gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade) throws BusinessException;

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioComissaoVendedor(Integer idVendedor, Periodo periodo);

	RelatorioWrapper<Integer, ComissaoVendaWrapper> gerarRelatorioComissaoVendedores(Periodo periodo);

	List<Pedido> gerarRelatorioCompra(Periodo periodo) throws InformacaoInvalidaException;

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioCompraAguardandoRecebimento(Integer idRepresentada,
			Periodo periodo);

	RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoCompraPeriodo(Periodo periodo) throws BusinessException;

	List<Pedido> gerarRelatorioEntrega(Periodo periodo) throws InformacaoInvalidaException;

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemEncomenda(Integer idCliente, Periodo periodo);

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioRevendaEmpacotamento(Integer idCliente, Periodo periodo);

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemAguardandoMaterial(Integer idRepresentada, Periodo periodo);

	List<Pedido> gerarRelatorioVenda(Periodo periodo) throws InformacaoInvalidaException;

	RelatorioWrapper<String, TotalizacaoPedidoWrapper> gerarRelatorioVendaCliente(boolean orcamento, Periodo periodo,
			Integer idCliente) throws BusinessException;

	RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoVendaPeriodo(Periodo periodo) throws BusinessException;

	RelatorioVendaVendedorByRepresentada gerarRelatorioVendaVendedor(boolean orcamento, Periodo periodo,
			Integer idVendedor) throws BusinessException;

	List<Cliente> pesquisarClienteByIdVendedor(Integer idVendedor);

	ReceitaWrapper gerarReceitaEstimada(Periodo periodo);
}
