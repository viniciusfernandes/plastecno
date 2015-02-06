package br.com.plastecno.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.service.wrapper.Periodo;

@Local
public interface PedidoService {

	Double calcularValorPedido(Integer idPedido) throws BusinessException;

	Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException;

	void cancelar(Integer idPedido) throws BusinessException;

	boolean contemItemPedido(Integer idPedido);

	void enviar(Integer idPedido, byte[] arquivoAnexado) throws BusinessException;

	Pedido inserir(Pedido pedido) throws BusinessException;

	Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException;

	boolean isCalculoIPIHabilitado(Integer idPedido);

	boolean isClienteProspectado(Integer idPedido);

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

	List<Pedido> pesquisarByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
			throws BusinessException;

	Pedido pesquisarCompraById(Integer id);

	List<ItemPedido> pesquisarCompraPendenteRecebimento(Integer idRepresentada, Periodo periodo);

	Date pesquisarDataEnvio(Integer idPedido);

	Date pesquisarDataInclusao(Integer idPedido);

	List<Pedido> pesquisarEnviadosByPeriodo(Periodo periodo);

	List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada);

	List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException;

	Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido);

	Integer pesquisarIdVendedorByIdPedido(Integer idPedido);

	ItemPedido pesquisarItemPedido(Integer idItemPedido);

	List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido);

	List<Logradouro> pesquisarLogradouro(Integer idPedido);

	Pedido pesquisarPedidoById(Integer id);

	List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	Usuario pesquisarProprietario(Integer idPedido);

	Long pesquisarTotalItemPedido(Integer idPedido);

	long pesquisarTotalItemPendente(Integer idPedido);

	Long pesquisarTotalRegistros(Integer idCliente);

	Long pesquisarTotalRegistros(Integer idCliente, Integer idVendedor);

	Double pesquisarValorPedido(Integer idPedido);

	Double pesquisarValorPedidoIPI(Integer idPedido);

	Pedido pesquisarVendaById(Integer id);

	Integer refazerPedido(Integer idPedido) throws BusinessException;

	Pedido removerItemPedido(Integer idItemPedido) throws BusinessException;

}
