package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Cidade;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.Pais;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;

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
