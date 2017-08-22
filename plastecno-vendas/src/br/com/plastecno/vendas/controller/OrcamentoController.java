package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class OrcamentoController extends AbstractController {
    @Servico
    private MaterialService materialService;

    @Servico
    private RepresentadaService representadaService;

    public OrcamentoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        verificarPermissaoAcesso("acessoCadastroPedidoPermitido", TipoAcesso.CADASTRO_PEDIDO_VENDAS);

    }

    @Post("orcamento/item/inclusao")
    public void inserirItemPedido(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
        forwardTo(PedidoController.class).inserirItemPedido(numeroPedido, itemPedido, aliquotaIPI);
    }

    @Post("orcamento/item/inclusao")
    public void inserirPedido(Integer numeroPedido, ContatoCliente contato, Cliente cliente) {
        if (cliente != null) {
            cliente.addContato(contato);
        }
    }

    @Get("orcamento")
    public void orcamentoHome() {
        addAtributo("orcamento", true);
        addAtributoCondicional("pedidoDesabilitado", false);
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentadaAtiva());
        addAtributo("vendedorNome", getUsuario().getNome());
        addAtributo("vendedorEmail", getUsuario().getEmail());
        addAtributoCondicional("idRepresentadaSelecionada", representadaService.pesquisarIdRevendedor());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    @Get("orcamento/cliente")
    public void pesquisarClienteByNomeFantasia(String nomeFantasia) {
        forwardTo(PedidoController.class).pesquisarClienteByNomeFantasia(nomeFantasia);
    }

    @Get("orcamento/material")
    public void pesquisarMaterial(String sigla, Integer idRepresentada) {
        forwardTo(PedidoController.class).pesquisarMaterial(sigla, idRepresentada);
    }

    public void pesquisarOrcamentoById(Integer idOrcamento) {
        Pedido orcamento = null;
        addAtributo("pedidoDesabilitado", isPedidoDesabilitado(orcamento));

    }
}
