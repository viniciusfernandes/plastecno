package br.com.plastecno.vendas.controller;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.entity.ContatoTransportadora;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.json.TransportadoraJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class TransportadoraController extends AbstractController {

    @Servico
    private ContatoService contatoService;

    @Servico
    private TransportadoraService transportadoraService;

    public TransportadoraController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Transportadora");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);
    }

    @Post("transportadora/desativacao")
    public void desativar(Integer idTransportadora) {
        this.transportadoraService.desativar(idTransportadora);
        gerarMensagemSucesso("Transportadora desativada com sucesso");
        irTopoPagina();
    }

    private void formatarDocumentos(Transportadora transportadora) {
        transportadora.setCnpj(this.formatarCNPJ(transportadora.getCnpj()));
        transportadora.setInscricaoEstadual(this.formatarInscricaoEstadual(transportadora.getInscricaoEstadual()));
    }

    @Post("transportadora/inclusao")
    public void inserir(Transportadora transportadora, Logradouro logradouro, List<ContatoTransportadora> listaContato,
            List<Integer> listaContatoRemocao) {
        try {
            if (temElementos(listaContato)) {
                transportadora.addContato(listaContato);
            }

            if (hasAtributo(logradouro)) {
                logradouro.setTipoLogradouro(TipoLogradouro.COMERCIAL);
                transportadora.setLogradouro(logradouro);
            }

            this.transportadoraService.inserir(transportadora);
            this.gerarMensagemCadastroSucesso(transportadora, "nomeFantasia");
        } catch (BusinessException e) {
            this.formatarDocumentos(transportadora);
            addAtributo("transportadora", transportadora);
            addAtributo("logradouro", logradouro);
            addAtributo("listaContato", listaContato);
            this.gerarListaMensagemErro(e);
        } catch (Exception e) {
            gerarLogErroInclusao("Transportadora", e);
        }
        irTopoPagina();
    }

    @Get("transportadora/{idTransportadora}")
    public void pesquisar(Integer idTransportadora) {

        Transportadora transportadora = this.transportadoraService.pesquisarById(idTransportadora);
        this.formatarDocumentos(transportadora);

        addAtributo("transportadora", transportadora);
        addAtributo("listaContato", this.transportadoraService.pesquisarContato(idTransportadora));
        addAtributo("logradouro", this.transportadoraService.pesquisarLogradorouro(idTransportadora));
        irTopoPagina();
    }

    @Get("transportadora/listagem")
    public void pesquisar(Transportadora filtro, Integer paginaSelecionada) {
        filtro.setCnpj(this.removerMascaraDocumento(filtro.getCnpj()));

        PaginacaoWrapper<Transportadora> paginacao = this.transportadoraService.paginarTransportadora(filtro, null,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        for (Transportadora transportadora : paginacao.getLista()) {
            this.formatarDocumentos(transportadora);
        }

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaTransportadora");
        addAtributo("transportadora", filtro);
    }

    @Get("transportadora/cnpj")
    public void pesquisarTransportadoraByCnpj(String cnpj) {
        Transportadora t = transportadoraService.pesquisarByCnpj(cnpj);
        serializarJson(new SerializacaoJson("transportadora", new TransportadoraJson(t)));
    }

    @Post("transportadora/contato/remocao/{idContato}")
    public void removerContato(Integer idContato) {
        this.contatoService.remover(idContato);
        irTopoPagina();
    }

    @Get("transportadora")
    public void transportadoraHome() {
        addAtributo("listaTipoLogradouroRenderizada", false);
    }
}
