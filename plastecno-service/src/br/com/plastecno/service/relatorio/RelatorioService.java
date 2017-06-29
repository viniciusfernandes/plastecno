package br.com.plastecno.service.relatorio;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.ComissaoVendaWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.ReceitaWrapper;
import br.com.plastecno.service.wrapper.RelatorioVendaVendedorByRepresentada;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;

@Local
public interface RelatorioService {

	byte[] gerarPlanilhaClienteVendedor(Integer idVendedor, boolean clienteInativo) throws BusinessException;

	ReceitaWrapper gerarReceitaEstimada(Periodo periodo);

	RelatorioWrapper<String, Cliente> gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade) throws BusinessException;

	List<Cliente> gerarRelatorioClienteVendedor(Integer idVendedor, boolean clienteInativo) throws BusinessException;

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioComissaoVendedor(Integer idVendedor, Periodo periodo);

	RelatorioWrapper<Integer, ComissaoVendaWrapper> gerarRelatorioComissaoVendedores(Periodo periodo);

	List<Pedido> gerarRelatorioCompra(Periodo periodo) throws InformacaoInvalidaException;

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioCompraAguardandoRecebimento(Integer idRepresentada,
			Periodo periodo);

	RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicata(Periodo periodo) throws BusinessException;

	RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicataByIdPedido(Integer idPedido) throws BusinessException;

	RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicataByNumeroNFe(Integer numeroNFe) throws BusinessException;

	List<Pedido> gerarRelatorioEntrega(Periodo periodo) throws InformacaoInvalidaException;

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemAguardandoCompra(Integer idCliente, Periodo periodo);

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemAguardandoMaterial(Integer idRepresentada, Periodo periodo);

	RelatorioWrapper<Pedido, ItemPedido> gerarRelatorioItemPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente,
			Integer idVendedor, Integer idFornecedor, boolean isOrcamento, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros, ItemPedido itemVendido);

	RelatorioWrapper<Integer, NFeItemFracionado> gerarRelatorioPedidoFracionado();

	RelatorioWrapper<Integer, ItemPedido> gerarRelatorioRevendaEmpacotamento(Integer idCliente);

	RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoCompraPeriodo(Periodo periodo)
			throws BusinessException;

	RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoVendaPeriodo(Periodo periodo)
			throws BusinessException;

	List<Pedido> gerarRelatorioVenda(Periodo periodo) throws InformacaoInvalidaException;

	RelatorioWrapper<String, TotalizacaoPedidoWrapper> gerarRelatorioVendaCliente(boolean orcamento, Periodo periodo,
			Integer idCliente) throws BusinessException;

	RelatorioVendaVendedorByRepresentada gerarRelatorioVendaVendedor(boolean orcamento, Periodo periodo,
			Integer idVendedor) throws BusinessException;

}
