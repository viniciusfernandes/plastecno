package br.com.svr.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.ContatoService;
import br.com.svr.service.TransportadoraService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.entity.ContatoTransportadora;
import br.com.svr.service.entity.LogradouroTransportadora;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.json.TransportadoraJson;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class TransportadoraController extends AbstractController {

    @Servico
    private ContatoService contatoService;

    @Servico
    private TransportadoraService transportadoraService;

    public TransportadoraController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        setNomeTela("Transportadora");
        verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.CADASTRO_BASICO);
    }

    @Post("transportadora/desativacao")
    public void desativar(Integer idTransportadora) {
        transportadoraService.desativar(idTransportadora);
        gerarMensagemSucesso("Transportadora desativada com sucesso");
        irTopoPagina();
    }

    private void formatarDocumentos(Transportadora transportadora) {
        transportadora.setCnpj(this.formatarCNPJ(transportadora.getCnpj()));
        transportadora.setInscricaoEstadual(this.formatarInscricaoEstadual(transportadora.getInscricaoEstadual()));
    }

    @Post("transportadora/inclusao")
    public void inserir(Transportadora transportadora, LogradouroTransportadora logradouro,
            List<ContatoTransportadora> listaContato, List<Integer> listaContatoRemocao) {
        try {
            if (temElementos(listaContato)) {
                transportadora.addContato(listaContato);
            }

            if (hasAtributo(logradouro)) {
                logradouro.setTipoLogradouro(TipoLogradouro.COMERCIAL);
                transportadora.setLogradouro(logradouro);
            }

            transportadoraService.inserir(transportadora);
            gerarMensagemCadastroSucesso(transportadora, "nomeFantasia");
        } catch (BusinessException e) {
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

        Transportadora transportadora = transportadoraService.pesquisarTransportadoraLogradouroById(idTransportadora);

        addAtributo("transportadora", transportadora);
        addAtributo("listaContato", transportadoraService.pesquisarContato(idTransportadora));
        addAtributo("logradouro", transportadoraService.pesquisarLogradorouro(idTransportadora));
        irTopoPagina();
    }

    @Get("transportadora/listagem")
    public void pesquisar(Transportadora filtro, Integer paginaSelecionada) {
        filtro.setCnpj(this.removerMascaraDocumento(filtro.getCnpj()));

        PaginacaoWrapper<Transportadora> paginacao = transportadoraService.paginarTransportadora(filtro, null,
                calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());
        for (Transportadora t : paginacao.getValor()) {
            formatarDocumentos(t);
        }
        inicializarPaginacao(paginaSelecionada, paginacao, "listaTransportadora");
        addAtributo("transportadora", filtro);
    }

    @Get("transportadora/cnpj")
    public void pesquisarTransportadoraByCnpj(String cnpj) {
        Transportadora t = transportadoraService.pesquisarByCnpj(cnpj);
        TransportadoraJson tJson = t == null ? new TransportadoraJson(null) : new TransportadoraJson(t,
                t.getLogradouro());
        serializarJson(new SerializacaoJson("transportadora", tJson));
    }

    @Get("transportadora/listagem/nome")
    public void pesquisarTransportadoraByNomeFantasia(String nomeFantasia) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Transportadora> l = transportadoraService.pesquisarByNomeFantasia(nomeFantasia);
        for (Transportadora t : l) {
            lista.add(new Autocomplete(t.getId(), t.getNomeFantasia()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
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
