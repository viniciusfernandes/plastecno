package br.com.svr.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.EnderecamentoService;
import br.com.svr.service.entity.Bairro;
import br.com.svr.service.entity.Cidade;
import br.com.svr.service.entity.Endereco;
import br.com.svr.service.entity.Pais;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.json.SerializacaoJson;

@Resource
public final class EnderecamentoController extends AbstractController {

    @Servico
    private EnderecamentoService enderecamentoService;

    public EnderecamentoController(Result result) {
        super(result);
    }

    @Get("cep/endereco")
    public void pesquisarEndereco(String cep) {
        Endereco endereco = enderecamentoService.pesquisarByCep(cep);

        if (endereco == null) {
            Bairro bairro = new Bairro();
            bairro.setDescricao("Nao existente");

            Cidade cidade = new Cidade();
            cidade.setDescricao("Nao existente");

            Pais pais = new Pais();
            pais.setDescricao("Nao existente");

            endereco = new Endereco(bairro, cidade, pais);
            endereco.setDescricao("Nao existente");
        }
        serializarJson(new SerializacaoJson(endereco, true));
    }
}
