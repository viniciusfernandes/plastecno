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

@Local
public interface PedidoService {

	void alterarQuantidadeRecepcionada(Integer idItemPedido, Integer quantidadeRecepcionada) throws BusinessException;

	void alterarQuantidadeReservadaByIdItemPedido(Integer idItemPedido);

	void alterarSituacaoPedidoByIdItemPedido(Integer idItemPedido, SituacaoPedido situacaoPedido);

	void alterarSituacaoPedidoByIdPedido(Integer idPedido, SituacaoPedido situacaoPedido);

	void alterarSituacaoPedidoEncomendadoByIdItem(Integer idItemPedido);

	void alterarSituacaoPedidoEncomendadoByIdPedido(Integer idPedido);

	Double calcularValorPedido(Integer idPedido) throws BusinessException;

	Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException;

	void cancelarPedido(Integer idPedido) throws BusinessException;

	boolean contemItemPedido(Integer idPedido);

	Integer encomendarItemPedido(Integer idComprador, Integer idFornecedor, Set<Integer> listaIdItemPedido)
			throws BusinessException;

	void enviarPedido(Integer idPedido, byte[] arquivoAnexado) throws BusinessException;

	boolean enviarRevendaEncomendadaEmpacotamento(Integer idPedido) throws BusinessException;

	Pedido inserir(Pedido pedido) throws BusinessException;

	Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException;

	Integer inserirItemPedido(ItemPedido itemPedido) throws BusinessException;

	boolean isCalculoIPIHabilitado(Integer idPedido);

	boolean isPedidoEnviado(Integer idPedido);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByIdCliente(Integer idCliente);

	List<Pedido> pesquisarByIdCliente(Integer idCliente, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByIdCliente(Integer idCliente, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<ItemPedido> pesquisarCompraAguardandoRecebimento(Integer idRepresentada, Periodo periodo);

	Pedido pesquisarCompraById(Integer id);

	List<Pedido> pesquisarCompraByPeriodoEComprador(Periodo periodo, Integer idComprador) throws BusinessException;

	Date pesquisarDataEnvio(Integer idPedido);

	Date pesquisarDataInclusao(Integer idPedido);

	List<Pedido> pesquisarEntregaVendaByPeriodo(Periodo periodo);

	List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada);

	List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException;

	Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido);

	List<Integer> pesquisarIdPedidoRevendaAguardandoEncomenda();

	List<Integer> pesquisarIdPedidoRevendaEncomendada();

	Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido);

	Integer pesquisarIdVendedorByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarItemEncomenda(Integer idCliente, Periodo periodo);

	ItemPedido pesquisarItemPedido(Integer idItemPedido);

	List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarItemPedidoEncomendado();

	List<ItemPedido> pesquisarItemPedidoEncomendado(Integer idCliente, Date dataInicial, Date dataFinal);

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

	Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido);

	List<ItemPedido> pesquisarRevendaEmpacotamento();

	List<ItemPedido> pesquisarRevendaEmpacotamento(Integer idCliente, Periodo periodo);

	List<ItemPedido> pesquisarRevendaEncomendada(Integer idRepresentada, Periodo periodo);

	List<SituacaoPedido> pesquisarSituacaoCompraEfetivada();

	SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido);

	SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido);

	List<SituacaoPedido> pesquisarSituacaoVendaEfetivada();

	List<Object[]> pesquisarTotalCompraResumidaByPeriodo(Periodo periodo);

	long pesquisarTotalItemCompradoNaoRecebido(Integer idPedido);

	Long pesquisarTotalItemPedido(Integer idPedido);

	Long pesquisarTotalPedidoByIdCliente(Integer idCliente, Integer idVendedor, boolean isCompra);

	Long pesquisarTotalPedidoVendaByIdCliente(Integer idCliente);

	List<Object[]> pesquisarTotalVendaResumidaByPeriodo(Periodo periodo);

	Double pesquisarValorPedido(Integer idPedido);

	Double pesquisarValorPedidoIPI(Integer idPedido);

	Pedido pesquisarVendaById(Integer id);

	List<Pedido> pesquisarVendaByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
			throws BusinessException;

	void reencomendarItemPedido(Integer idItemPedido) throws BusinessException;

	Integer refazerPedido(Integer idPedido) throws BusinessException;

	Pedido removerItemPedido(Integer idItemPedido) throws BusinessException;

}
