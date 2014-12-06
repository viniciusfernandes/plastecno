package br.com.plastecno.service.impl.relatorio;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.dao.RelatorioVendasDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.ClienteWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioClienteRamoAtividade;
import br.com.plastecno.service.wrapper.RelatorioVendaPeriodo;
import br.com.plastecno.service.wrapper.RelatorioVendaVendedorByRepresentada;
import br.com.plastecno.service.wrapper.VendaClienteWrapper;
import br.com.plastecno.service.wrapper.VendaRepresentadaWrapper;
import br.com.plastecno.service.wrapper.exception.AgrupamentoException;
import br.com.plastecno.util.StringUtils;

@Stateless
public class RelatorioServiceImpl implements RelatorioService {

    @PersistenceContext(name = "plastecno")
    private EntityManager entityManager;

    @EJB
    private ClienteService clienteService;

    @EJB
    private PedidoService pedidoService;

    @EJB
    private RepresentadaService representadaService;

    @EJB
    private RamoAtividadeService ramoAtividadeService;

    @EJB
    private UsuarioService usuarioService;

    private RelatorioVendasDAO relatorioVendasDAO;

    @Override
    public RelatorioClienteRamoAtividade gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade)
            throws BusinessException {

        if (idRamoAtividade == null) {
            throw new BusinessException("O ramo de atividade é obrigatório");
        }

        RamoAtividade ramoAtividade = this.ramoAtividadeService.pesquisarById(idRamoAtividade);
        List<Cliente> listaCliente = this.clienteService.pesquisarByRamoAtividade(idRamoAtividade);
        RelatorioClienteRamoAtividade relatorio = new RelatorioClienteRamoAtividade(
                "Relatório de Clientes com o ramo de atividades " + ramoAtividade.getSigla());

        StringBuilder descricaoContato = new StringBuilder();
        for (Cliente cliente : listaCliente) {

            if (cliente.isListaContatoPreenchida()) {
                Contato c = cliente.getContatoPrincipal();
                descricaoContato.append(c.getNome());

                if (StringUtils.isNotEmpty(c.getEmail())) {
                    descricaoContato.append(" - ").append(c.getEmail());
                }

                if (StringUtils.isNotEmpty(c.getTelefone())) {
                    descricaoContato.append(" - ").append(c.getTelefoneFormatado());
                }
            }

            relatorio.addCliente(new ClienteWrapper(cliente.getVendedor().getNomeCompleto(), cliente.getRazaoSocial(),
                    descricaoContato.toString()));

            descricaoContato.delete(0, descricaoContato.length());
        }

        return relatorio;
    }

    @Override
    public RelatorioVendaPeriodo gerarRelatorioVendaPeriodo(Periodo periodo)
            throws BusinessException {

        final List<Object[]> resultados = this.relatorioVendasDAO
                .pesquisarVendas(periodo.getInicio(), periodo.getFim());

        final StringBuilder titulo = new StringBuilder();
        titulo.append("Relatório das Vendas do Período de ");
        titulo.append(StringUtils.formatarData(periodo.getInicio()));
        titulo.append(" à ");
        titulo.append(StringUtils.formatarData(periodo.getFim()));
        final RelatorioVendaPeriodo relatorio = new RelatorioVendaPeriodo(
                titulo.toString());

        for (Object[] resultado : resultados) {

            try {
                relatorio.addVenda(resultado[0].toString(), new VendaRepresentadaWrapper(resultado[1].toString(),
                        (Double) resultado[2]));
            } catch (AgrupamentoException e) {
                throw new BusinessException(
                        "Falha na construcao do relatorio de vendas da representada por vendedor", e);
            }
        }

        return relatorio;
    }

    @Override
    public RelatorioVendaVendedorByRepresentada gerarRelatorioVendaVendedor(boolean orcamento, Periodo periodo,
            Integer idVendedor) throws BusinessException {
        Usuario vendedor = this.usuarioService.pesquisarVendedorById(idVendedor);

        if (vendedor == null) {
            throw new BusinessException("O vendedor é obrigatório para a geração do relatório");
        }

        final StringBuilder titulo = new StringBuilder(orcamento ? "Orçamento " : "Vendas ").append(" do Vendedor ")
                .append(vendedor.getNome()).append(" de ").append(StringUtils.formatarData(periodo.getInicio()))
                .append(" à ").append(StringUtils.formatarData(periodo.getFim()));

        final RelatorioVendaVendedorByRepresentada relatorio = new RelatorioVendaVendedorByRepresentada(
                titulo.toString());
        List<Pedido> listaPedido = this.pedidoService.pesquisarByPeriodoEVendedor(orcamento, periodo, idVendedor);
        for (Pedido pedido : listaPedido) {
            try {
                pedido.setDataEnvioFormatada(StringUtils.formatarData(pedido.getDataEnvio()));
                relatorio.addRepresentada(pedido.getRepresentada().getNomeFantasia(), new VendaClienteWrapper(pedido));
            } catch (Exception e) {
                throw new BusinessException("Falha na geracao do relatorio de vendas do vendedor " + idVendedor, e);
            }
        }

        return relatorio;
    }

    @PostConstruct
    public void init() {
        relatorioVendasDAO = new RelatorioVendasDAO(this.entityManager);
    }

    @Override
    public List<Cliente> pesquisarClienteByIdVendedor(Integer idVendedor) {
        return this.clienteService.pesquisarByIdVendedor(idVendedor);
    }

    @Override
    public List<Pedido> pesquisarEntregas(Periodo periodo) throws InformacaoInvalidaException {
        return this.pedidoService.pesquisarEnviadosByPeriodo(periodo);
    }
}
