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
    Pedido inserir(Pedido pedido) throws BusinessException;

    Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException;

    Pedido pesquisarById(Integer id);

    Long pesquisarTotalRegistros(Integer idCliente);

    List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido);

    ItemPedido pesquisarItemPedido(Integer idItemPedido);

    Date pesquisarDataInclusao(Integer idPedido);

    Usuario pesquisarVendedor(Integer idPedido);

    Date pesquisarDataEnvio(Integer idPedido);

    void enviar(Integer idPedido, byte[] arquivoAnexado) throws BusinessException;

    Pedido removerItemPedido(Integer idItemPedido) throws BusinessException;

    Double pesquisarValorPedido(Integer idPedido);

    List<Pedido> pesquisarByIdCliente(Integer idCliente, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    List<Pedido> pesquisarByIdCliente(Integer idCliente);

    Long pesquisarTotalItemPedido(Integer idPedido);

    boolean contemItemPedido(Integer idPedido);

    boolean isClienteProspectado(Integer idPedido);

    Double calcularValorPedido(Integer idPedido) throws BusinessException;

    List<Pedido> pesquisarByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Long pesquisarTotalRegistros(Integer idCliente, Integer idVendedor);

    Integer pesquisarIdVendedorByIdPedido(Integer idPedido);

    PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    void cancelar(Integer idPedido) throws BusinessException;

    boolean isPedidoEnviado(Integer idPedido);

    Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException;

    Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido, Double aliquotaIPI) throws BusinessException;

    Double pesquisarValorPedidoIPI(Integer idPedido);

    List<Logradouro> pesquisarLogradouro(Integer idPedido);

    List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada);

    List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException;

    List<Pedido> pesquisarEnviadosByPeriodo(Periodo periodo);

    List<Pedido> pesquisarByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
            throws BusinessException;
    
    Integer copiarPedido(Integer idPedido) throws BusinessException;
}
