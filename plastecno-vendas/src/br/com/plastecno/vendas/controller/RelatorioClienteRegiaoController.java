package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.RegiaoService;
import br.com.plastecno.service.entity.Regiao;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioClienteRegiaoController extends AbstractController {

    @Servico
    private ClienteService clienteService;

    @Servico
    private RegiaoService regiaoService;

    public RelatorioClienteRegiaoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/cliente/regiao")
    public void relatorioClienteRegiaoHome() {
    }

    @Get("relatorio/cliente/regiao/listagem/cliente")
    public void pesquisarClienteByRegiao(Regiao regiao) {
        try {
            addAtributo("listaCliente", this.clienteService.pesquisarClienteByIdRegiao(regiao.getId()));
            addAtributo("regiao", regiao);
            addAtributo("relatorioGerado", true);
            addAtributo("tituloRelatorio", "Relatório dos Clientes da Região " + regiao.getNome());
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        this.irPaginaHome();
    }

    @Get("relatorio/cliente/regiao/listagem")
    public void pesquisarRegiaoByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Regiao> listaRegiao = this.regiaoService.pesquisarBy(new Regiao(nome));
        for (Regiao regiao : listaRegiao) {
            lista.add(new Autocomplete(regiao.getId(), regiao.getNome()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

}
