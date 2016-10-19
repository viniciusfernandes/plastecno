package br.com.plastecno.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;

@Local
public interface PedidoService {
	void aceitarOrcamento(Integer idOrcamento);

	void alterarItemAguardandoCompraByIdPedido(Integer idPedido);

	void alterarItemAguardandoMaterialByIdPedido(Integer idPedido);

	void alterarQuantidadeRecepcionada(Integer idItemPedido,
			Integer quantidadeRecepcionada) throws BusinessException;

	void alterarQuantidadeReservadaByIdItemPedido(Integer idItemPedido);

	void alterarRevendaAguardandoMaterialByIdItem(Integer idItemPedido);

	void alterarSituacaoPedidoByIdItemPedido(Integer idItemPedido,
			SituacaoPedido situacaoPedido);

	void alterarSituacaoPedidoByIdPedido(Integer idPedido,
			SituacaoPedido situacaoPedido);

	List<Date> calcularDataPagamento(Integer idPedido);

	Double calcularValorPedido(Integer idPedido) throws BusinessException;

	Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException;

	void cancelarPedido(Integer idPedido) throws BusinessException;

	Integer comprarItemPedido(Integer idComprador, Integer idFornecedor,
			Set<Integer> listaIdItemPedido) throws BusinessException;

	boolean contemItemPedido(Integer idPedido);

	boolean contemQuantidadeNaoRecepcionadaItemPedido(Integer idItemPedido);

	boolean empacotarItemAguardandoCompra(Integer idPedido)
			throws BusinessException;

	boolean empacotarItemAguardandoMaterial(Integer idPedido)
			throws BusinessException;

	boolean empacotarPedidoAguardandoCompra(Integer idPedido)
			throws BusinessException;

	void enviarPedido(Integer idPedido, byte[] arquivoAnexado)
			throws BusinessException;

	Pedido inserir(Pedido pedido) throws BusinessException;

	void inserirDadosNotaFiscal(Pedido pedido);

	Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido)
			throws BusinessException;

	Integer inserirItemPedido(ItemPedido itemPedido) throws BusinessException;

	void inserirNcmItemAguardandoMaterialAssociadoByIdItemCompra(
			Integer idItemPedidoCompra, String ncm) throws BusinessException;

	Pedido inserirOrcamento(Pedido pedido) throws BusinessException;

	boolean isCalculoIPIHabilitado(Integer idPedido);

	boolean isPedidoEnviado(Integer idPedido);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente,
			Integer idVendedor, Integer idFornecedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	double pesquisarAliquotaIPIByIdItemPedido(Integer idItemPedido);

	List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByIdCliente(Integer idCliente);

	List<Pedido> pesquisarByIdCliente(Integer idCliente,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByIdClienteIdFornecedor(Integer idCliente,
			Integer idFornecedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	Cliente pesquisarClienteByIdPedido(Integer idPedido);

	Cliente pesquisarClienteResumidoByIdPedido(Integer idPedido);

	double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarCompraAguardandoRecebimento(
			Integer idRepresentada, Periodo periodo);

	Pedido pesquisarCompraById(Integer id);

	List<Pedido> pesquisarCompraByPeriodoEComprador(Periodo periodo,
			Integer idComprador) throws BusinessException;

	Pedido pesquisarDadosNotaFiscalByIdItemPedido(Integer idItemPedido);

	Date pesquisarDataEnvio(Integer idPedido);

	List<Pedido> pesquisarEntregaVendaByPeriodo(Periodo periodo);

	List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo,
			Integer idRepresentada);

	List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo,
			Integer idVendedor) throws BusinessException;

	List<Integer> pesquisarIdItemPedidoByIdPedido(Integer idPedido);

	Object[] pesquisarIdMaterialFormaMaterialItemPedido(Integer idItemPedido);

	List<Integer> pesquisarIdPedidoAguardandoCompra();

	List<Integer> pesquisarIdPedidoAguardandoEmpacotamento();

	List<Integer> pesquisarIdPedidoAguardandoMaterial();

	List<Integer> pesquisarIdPedidoAssociadoByIdPedidoOrigem(
			Integer idPedidoOrigem, boolean isCompra);

	Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido);

	List<Integer> pesquisarIdPedidoByIdItemPedido(
			List<Integer> listaIdItemPedido);

	List<Integer> pesquisarIdPedidoItemAguardandoCompra();

	Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido);

	Integer pesquisarIdVendedorByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarItemAguardandoCompra(Integer idCliente,
			Periodo periodo);

	List<ItemPedido> pesquisarItemAguardandoMaterial(Integer idRepresentada,
			Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento();

	List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento(
			Integer idCliente);

	ItemPedido pesquisarItemPedidoById(Integer idItemPedido);

	List<ItemPedido> pesquisarItemPedidoByIdClienteIdVendedorIdFornecedor(
			Integer idCliente, Integer idVendedor, Integer idFornecedor,
			boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros, ItemPedido itemVendido);

	List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarItemPedidoCompradoResumidoByPeriodo(
			Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoEncomendado();

	List<ItemPedido> pesquisarItemPedidoEncomendado(Integer idCliente,
			Date dataInicial, Date dataFinal);

	List<ItemPedido> pesquisarItemPedidoRepresentacaoByPeriodo(Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoRevendaByPeriodo(Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoVendaByPeriodo(Periodo periodo,
			Integer idVendedor);

	List<ItemPedido> pesquisarItemPedidoVendaResumidaByPeriodo(Periodo periodo);

	List<Logradouro> pesquisarLogradouro(Integer idPedido);

	Pedido pesquisarPedidoById(Integer id);

	Pedido pesquisarPedidoById(Integer idPedido, boolean isCompra);

	List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarPedidoByIdClienteIdVendedorIdFornecedor(
			Integer idCliente, Integer idVendedor, Integer idFornecedor,
			boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarPedidoCompraByPeriodo(Periodo periodo);

	List<Pedido> pesquisarPedidoVendaByPeriodo(Periodo periodo);

	Usuario pesquisarProprietario(Integer idPedido);

	int pesquisarQuantidadeItemPedido(Integer idItemPedido);

	int pesquisarQuantidadeNaoRecepcionadaItemPedido(Integer idItemPedido);

	int pesquisarQuantidadeRecepcionadaItemPedido(Integer idItemPedido);

	Representada pesquisarRepresentadaIdPedido(Integer idPedido);

	Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido);

	List<SituacaoPedido> pesquisarSituacaoCompraEfetivada();

	SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido);

	SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido);

	List<SituacaoPedido> pesquisarSituacaoRevendaEfetivada();

	List<SituacaoPedido> pesquisarSituacaoVendaEfetivada();

	Object[] pesquisarTelefoneContatoByIdPedido(Integer idPedido);

	List<TotalizacaoPedidoWrapper> pesquisarTotalCompraResumidaByPeriodo(
			Periodo periodo);

	long pesquisarTotalItemCompradoNaoRecebido(Integer idPedido);

	Long pesquisarTotalItemPedido(Integer idPedido);

	Long pesquisarTotalPedidoByIdClienteIdFornecedor(Integer idCliente,
			Integer idFornecedor, boolean isCompra);

	Long pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(
			Integer idCliente, Integer idVendedor, Integer idFornecedor,
			boolean isCompra, ItemPedido itemVendido);

	Long pesquisarTotalPedidoVendaByIdClienteIdVendedorIdFornecedor(
			Integer idCliente);

	List<TotalizacaoPedidoWrapper> pesquisarTotalPedidoVendaResumidaByPeriodo(
			Periodo periodo);

	Transportadora pesquisarTransportadoraByIdPedido(Integer idPedido);

	Double pesquisarValorPedido(Integer idPedido);

	Double[] pesquisarValorPedidoByItemPedido(Integer idItemPedido);

	Double pesquisarValorPedidoIPI(Integer idPedido);

	List<TotalizacaoPedidoWrapper> pesquisarValorVendaClienteByPeriodo(
			Periodo periodo, Integer idCliente, boolean isOrcamento);

	Pedido pesquisarVendaById(Integer id);

	List<Pedido> pesquisarVendaByPeriodoEVendedor(boolean orcamento,
			Periodo periodo, Integer idVendedor) throws BusinessException;

	Usuario pesquisarVendedorByIdItemPedido(Integer idItemPedido);

	void reencomendarItemPedido(Integer idItemPedido) throws BusinessException;

	Integer refazerPedido(Integer idPedido) throws BusinessException;

	Pedido removerItemPedido(Integer idItemPedido) throws BusinessException;

	boolean isPedidoVendaExistente(Integer idPedido);

}
