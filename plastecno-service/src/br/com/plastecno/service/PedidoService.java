package br.com.plastecno.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;

@Local
public interface PedidoService {

	void alterarItemAguardandoCompraByIdPedido(Integer idPedido);

	void alterarItemAguardandoMaterialByIdPedido(Integer idPedido);

	void alterarQuantidadeRecepcionada(Integer idItemPedido, Integer quantidadeRecepcionada) throws BusinessException;

	void alterarQuantidadeReservadaByIdItemPedido(Integer idItemPedido);

	void alterarRevendaAguardandoMaterialByIdItem(Integer idItemPedido);

	void alterarSituacaoPedidoByIdItemPedido(Integer idItemPedido, SituacaoPedido situacaoPedido);

	void alterarSituacaoPedidoByIdPedido(Integer idPedido, SituacaoPedido situacaoPedido);

	Double calcularValorPedido(Integer idPedido) throws BusinessException;

	Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException;

	void cancelarPedido(Integer idPedido) throws BusinessException;

	Integer comprarItemPedido(Integer idComprador, Integer idFornecedor, Set<Integer> listaIdItemPedido)
			throws BusinessException;

	boolean contemItemPedido(Integer idPedido);

	boolean contemQuantidadeNaoRecepcionadaItemPedido(Integer idItemPedido);

	boolean empacotarItemAguardandoCompra(Integer idPedido) throws BusinessException;

	boolean empacotarItemAguardandoMaterial(Integer idPedido) throws BusinessException;

	void enviarPedido(Integer idPedido, byte[] arquivoAnexado) throws BusinessException;

	boolean enviarRevendaAguardandoEncomendaEmpacotamento(Integer idPedido) throws BusinessException;

	Pedido inserir(Pedido pedido) throws BusinessException;

	Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException;

	Integer inserirItemPedido(ItemPedido itemPedido) throws BusinessException;

	boolean isCalculoIPIHabilitado(Integer idPedido);

	boolean isPedidoEnviado(Integer idPedido);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	double pesquisarAliquotaICMSRepresentadaByIdItemPedido(Integer idItemPedido);

	double pesquisarAliquotaIPIByIdItemPedido(Integer idItemPedido);

	List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByIdCliente(Integer idCliente);

	List<Pedido> pesquisarByIdCliente(Integer idCliente, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByIdCliente(Integer idCliente, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarCompraAguardandoRecebimento(Integer idRepresentada, Periodo periodo);

	Pedido pesquisarCompraById(Integer id);

	List<Pedido> pesquisarCompraByPeriodoEComprador(Periodo periodo, Integer idComprador) throws BusinessException;

	Date pesquisarDataEnvio(Integer idPedido);

	Date pesquisarDataInclusao(Integer idPedido);

	List<Pedido> pesquisarEntregaVendaByPeriodo(Periodo periodo);

	List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada);

	List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException;

	List<Integer> pesquisarIdItemPedidoByIdPedido(Integer idPedido);

	List<Integer> pesquisarIdPedidoAguardandoCompra();

	List<Integer> pesquisarIdPedidoAguardandoEmpacotamento();

	List<Integer> pesquisarIdPedidoAguardandoMaterial();

	Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido);

	List<Integer> pesquisarIdPedidoByIdItemPedido(List<Integer> listaIdItemPedido);

	List<Integer> pesquisarIdPedidoItemAguardandoCompra();

	Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido);

	Integer pesquisarIdVendedorByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarItemAguardandoCompra(Integer idCliente, Periodo periodo);

	List<ItemPedido> pesquisarItemAguardandoMaterial(Integer idRepresentada, Periodo periodo);

	ItemPedido pesquisarItemPedido(Integer idItemPedido);

	List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento();

	List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento(Integer idCliente);

	List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarItemPedidoCompradoResumidoByPeriodo(Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoEncomendado();

	List<ItemPedido> pesquisarItemPedidoEncomendado(Integer idCliente, Date dataInicial, Date dataFinal);

	List<ItemPedido> pesquisarItemPedidoRepresentacaoByPeriodo(Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoRevendaByPeriodo(Periodo periodo);

	List<ItemPedido> pesquisarItemPedidoVendaByPeriodo(Periodo periodo, Integer idVendedor);

	List<ItemPedido> pesquisarItemPedidoVendaResumidaByPeriodo(Periodo periodo);

	List<Logradouro> pesquisarLogradouro(Integer idPedido);

	Pedido pesquisarPedidoById(Integer id);

	List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarPedidoCompraByPeriodo(Periodo periodo);

	List<Pedido> pesquisarPedidoVendaByPeriodo(Periodo periodo);

	Usuario pesquisarProprietario(Integer idPedido);

	int pesquisarQuantidadeItemPedido(Integer idItemPedido);

	int pesquisarQuantidadeNaoRecepcionadaItemPedido(Integer idItemPedido);

	int pesquisarQuantidadeRecepcionadaItemPedido(Integer idItemPedido);

	Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido);

	List<SituacaoPedido> pesquisarSituacaoCompraEfetivada();

	SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido);

	SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido);

	List<SituacaoPedido> pesquisarSituacaoRevendaEfetivada();

	List<SituacaoPedido> pesquisarSituacaoVendaEfetivada();

	List<TotalizacaoPedidoWrapper> pesquisarTotalCompraResumidaByPeriodo(Periodo periodo);

	long pesquisarTotalItemCompradoNaoRecebido(Integer idPedido);

	Long pesquisarTotalItemPedido(Integer idPedido);

	Long pesquisarTotalPedidoByIdCliente(Integer idCliente, Integer idVendedor, boolean isCompra);

	Long pesquisarTotalPedidoVendaByIdCliente(Integer idCliente);

	List<TotalizacaoPedidoWrapper> pesquisarTotalPedidoVendaResumidaByPeriodo(Periodo periodo);

	Double pesquisarValorPedido(Integer idPedido);

	Double[] pesquisarValorPedidoByItemPedido(Integer idItemPedido);

	Double pesquisarValorPedidoIPI(Integer idPedido);

	List<TotalizacaoPedidoWrapper> pesquisarValorVendaClienteByPeriodo(Periodo periodo, Integer idCliente,
			boolean isOrcamento);

	Pedido pesquisarVendaById(Integer id);

	List<Pedido> pesquisarVendaByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
			throws BusinessException;

	Usuario pesquisarVendedorByIdItemPedido(Integer idItemPedido);

	void reencomendarItemPedido(Integer idItemPedido) throws BusinessException;

	Integer refazerPedido(Integer idPedido) throws BusinessException;

	Pedido removerItemPedido(Integer idItemPedido) throws BusinessException;

}
