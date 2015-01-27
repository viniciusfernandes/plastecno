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

	Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido, Double aliquotaIPI) throws BusinessException;

	boolean isClienteProspectado(Integer idPedido);

	boolean isPedidoEnviado(Integer idPedido);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	Pedido pesquisarById(Integer id);

	List<Pedido> pesquisarByIdCliente(Integer idCliente);

	List<Pedido> pesquisarByIdCliente(Integer idCliente, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	List<Pedido> pesquisarByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
			throws BusinessException;

	Date pesquisarDataEnvio(Integer idPedido);

	Date pesquisarDataInclusao(Integer idPedido);

	List<Pedido> pesquisarEnviadosByPeriodo(Periodo periodo);

	List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada);

	List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException;

	Integer pesquisarIdVendedorByIdPedido(Integer idPedido);

	ItemPedido pesquisarItemPedido(Integer idItemPedido);

	List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido);

	List<Logradouro> pesquisarLogradouro(Integer idPedido);

	List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	Long pesquisarTotalItemPedido(Integer idPedido);

	Long pesquisarTotalRegistros(Integer idCliente);

	Long pesquisarTotalRegistros(Integer idCliente, Integer idVendedor);

	Double pesquisarValorPedido(Integer idPedido);

	Double pesquisarValorPedidoIPI(Integer idPedido);

	Usuario pesquisarVendedor(Integer idPedido);

	Integer refazerPedido(Integer idPedido) throws BusinessException;

	Pedido removerItemPedido(Integer idItemPedido) throws BusinessException;

}
