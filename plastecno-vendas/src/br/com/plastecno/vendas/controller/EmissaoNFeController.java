package br.com.plastecno.vendas.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMSST;
import br.com.plastecno.service.nfe.constante.TipoMotivoDesoneracaoICMS;
import br.com.plastecno.service.nfe.constante.TipoOrigemMercadoria;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class EmissaoNFeController extends AbstractController {
    @Servico
    private ClienteService clienteService;

    @Servico
    private NFeService nFeService;

    @Servico
    private PedidoService pedidoService;

    public EmissaoNFeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Ramo de atividade");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);
    }

    public Download nfexml(String xml) {
        return gerarDownload(xml.getBytes(), "nfe.xml", "application/octet-stream");
    }

    @Get("emissaoNFe")
    public void emissaoNFeHome() {
        addAtributo("listaTipoFinalidadeEmissao", TipoFinalidadeEmissao.values());
        addAtributo("finalidadeEmissaoPadrao", TipoFinalidadeEmissao.NORMAL);

        addAtributo("listaTipoFormaPagamento", TipoFormaPagamento.values());
        addAtributo("formaPagamentoPadrao", TipoFormaPagamento.PRAZO);

        addAtributo("listaTipoEmissao", TipoEmissao.values());
        addAtributo("tipoEmissaoPadrao", TipoEmissao.NORMAL);
        addAtributo("listaTipoTributacaoICMS", TipoTributacaoICMS.values());
        addAtributo("listaTipoOrigemMercadoria", TipoOrigemMercadoria.values());
        addAtributo("listaTipoModalidadeDeterminacaoBCICMS", TipoModalidadeDeterminacaoBCICMS.values());
        addAtributo("listaTipoModalidadeDeterminacaoBCICMSST", TipoModalidadeDeterminacaoBCICMSST.values());
        addAtributo("listaTipoMotivoDesoneracao", TipoMotivoDesoneracaoICMS.values());
        addAtributo("listaTipoTributacaoIPI", TipoTributacaoIPI.values());
        addAtributo("listaTipoTributacaoPIS", TipoTributacaoPIS.values());
        addAtributo("listaTipoTributacaoCOFINS", TipoTributacaoCOFINS.values());

    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(NFe nf, Logradouro logradouro, Integer idPedido) {

        try {
            nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(nFeService.gerarEnderecoNFe(logradouro));
            nFeService.emitirNFe(nf, idPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).nfexml(lerXML());
        // irTopoPagina();
    }

    private String lerXML() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("java.io.tmpdir")
                    + "nfe.xml")));
            String l = null;
            StringBuilder xml = new StringBuilder();
            while ((l = br.readLine()) != null) {
                xml.append(l).append("\n");
            }
            br.close();
            return xml.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Get("emissaoNFe/pedido")
    public void pesquisarPedidoById(Integer idPedido) {
        Cliente cliente = pedidoService.pesquisarClienteResumidoEContatoByIdPedido(idPedido);

        addAtributo("cliente", cliente);
        addAtributo("logradouro", clienteService.pesquisarLogradouroFaturamentoById(cliente.getId()));
        addAtributo("listaItem", pedidoService.pesquisarItemPedidoByIdPedido(idPedido));
        addAtributo("idPedido", idPedido);

        irTopoPagina();
    }
}
