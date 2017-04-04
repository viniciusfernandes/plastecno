package br.com.plastecno.service.test.builder;

import java.util.LinkedList;
import java.util.List;

import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Cidade;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pais;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.test.TestUtils;

public class EntidadeBuilder {
	private static final EntidadeBuilder builder = new EntidadeBuilder();

	public static EntidadeBuilder getInstance() {
		return builder;
	}

	private EntidadeRepository repositorio = EntidadeRepository.getInstance();

	private EntidadeBuilder() {
	}

	public Cliente buildCliente() {
		Cliente cliente = new Cliente();
		cliente.addLogradouro(buildLogradouroCliente(TipoLogradouro.FATURAMENTO));
		cliente.addLogradouro(buildLogradouroCliente(TipoLogradouro.ENTREGA));
		cliente.addLogradouro(buildLogradouroCliente(TipoLogradouro.COBRANCA));
		cliente.setRazaoSocial("Exercito Brasileiro");
		cliente.setNomeFantasia("Exercito Brasileiro");
		cliente.setCnpj("25632147000125");
		cliente.setRamoAtividade(buildRamoAtividade());
		cliente.setEmail("alex@gmail.com.br");
		cliente.setId(gerarId());
		cliente.setTipoCliente(TipoCliente.NORMAL);
		return cliente;
	}

	public Cliente buildClienteOrcamento() {
		Cliente cliente = new Cliente();
		cliente.setNomeFantasia("Exercito Brasileiro");
		cliente.setTipoCliente(TipoCliente.NORMAL);

		ContatoCliente contato = new ContatoCliente();
		contato.setNome("Andre Marcondes");
		contato.setEmail("andre@gmail.com");
		contato.setTelefone("999999999");
		contato.setDdd("11");
		contato.setRamal("4444");
		contato.setFax("88888888");

		cliente.addContato(contato);

		return cliente;
	}

	public Cliente buildClienteRevendedor() {
		Cliente cliente = buildCliente();
		cliente.setNomeFantasia("Revendedor Plastico");
		cliente.setRazaoSocial("Revendedor Plastico LTDA");
		cliente.setTipoCliente(TipoCliente.REVENDEDOR);
		return cliente;
	}

	public Endereco buildEndereco() {
		Bairro bairro = new Bairro();
		bairro.setDescricao("Centro");
		bairro.setId(gerarId());

		Cidade cidade = new Cidade();
		cidade.setDescricao("Sao Paulo");
		cidade.setUf("SP");
		cidade.setId(gerarId());

		Pais pais = new Pais();
		pais.setId(gerarId());
		pais.setDescricao("Brasil");

		Endereco endereco = new Endereco(bairro, cidade, pais);
		endereco.setCep("09910456");
		endereco.setDescricao("Av Paulista");

		repositorio.inserirEntidade(bairro);
		repositorio.inserirEntidade(cidade);
		repositorio.inserirEntidade(pais);
		repositorio.inserirEntidade(endereco);
		return endereco;
	}

	public ItemEstoque buildItemEstoque() {
		ItemEstoque item = new ItemEstoque();
		item.setFormaMaterial(FormaMaterial.CH);
		item.setMedidaExterna(200.0);
		item.setMedidaInterna(100.0);
		item.setComprimento(1000.0);
		item.setPrecoMedio(100.0);
		item.setQuantidade(10);
		return item;
	}

	public ItemEstoque buildItemEstoquePeca() {
		ItemEstoque item = buildItemEstoque();
		item.setFormaMaterial(FormaMaterial.PC);

		item.setDescricaoPeca("ENGRENAGEM PARA TRATOR");

		item.setMedidaExterna(null);
		item.setMedidaInterna(null);
		item.setComprimento(null);

		item.setMaterial(buildMaterial());
		item.setAliquotaIPI(0.1d);
		return item;

	}

	public ItemPedido buildItemPedido() {
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setAliquotaIPI(0.11d);
		itemPedido.setAliquotaICMS(0.06d);
		itemPedido.setMaterial(buildMaterial());
		itemPedido.setFormaMaterial(FormaMaterial.TB);
		itemPedido.setQuantidade(2);
		itemPedido.setMedidaExterna(120d);
		itemPedido.setMedidaInterna(100d);
		itemPedido.setComprimento(1000d);
		itemPedido.setTipoVenda(TipoVenda.KILO);
		itemPedido.setPrecoVenda(60d);
		return itemPedido;
	}

	public ItemPedido buildItemPedidoPeca() {
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setAliquotaIPI(11.1d);
		itemPedido.setAliquotaICMS(0.06d);
		itemPedido.setMaterial(buildMaterial());
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		itemPedido.setQuantidade(2);
		itemPedido.setMedidaExterna(null);
		itemPedido.setMedidaInterna(null);
		itemPedido.setComprimento(null);
		itemPedido.setTipoVenda(TipoVenda.PECA);
		itemPedido.setDescricaoPeca("ENGRENAGEM DE TESTE");
		itemPedido.setPrecoVenda(60d);
		return itemPedido;
	}

	public List<PerfilAcesso> buildListaPerfilAcesso() {
		List<PerfilAcesso> l = new LinkedList<PerfilAcesso>();
		for (TipoAcesso tipoAcesso : TipoAcesso.values()) {
			PerfilAcesso p = new PerfilAcesso();
			p.setDescricao(tipoAcesso.toString());
			l.add(p);
		}
		return l;
	}

	public Logradouro buildLogradouro(TipoLogradouro tipoLogradouro) {
		Logradouro logradouro = new Logradouro(buildEndereco());
		logradouro.setTipoLogradouro(tipoLogradouro);
		return logradouro;
	}

	public LogradouroCliente buildLogradouroCliente(TipoLogradouro tipoLogradouro) {
		LogradouroCliente logradouro = new LogradouroCliente(buildEndereco());
		logradouro.setNumero("223");
		logradouro.setTipoLogradouro(tipoLogradouro);
		return logradouro;
	}

	public Material buildMaterial() {
		Material material = new Material();
		material.setSigla("PLAST");
		material.setDescricao("PLASTICO DURO");
		material.setPesoEspecifico(0.33);
		return material;
	}

	public Pedido buildPedido() {
		Usuario vendedor = buildVendedor();
		Cliente cliente = buildCliente();
		cliente.setVendedor(vendedor);

		Representada representada = buildRepresentada();
		Contato contato = new Contato();
		contato.setNome("Adriano");
		contato.setDdd("11");
		contato.setDdi("55");
		contato.setTelefone("99996321");

		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setRepresentada(representada);
		pedido.setProprietario(vendedor);
		pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
		pedido.setFinalidadePedido(FinalidadePedido.CONSUMO);
		pedido.setContato(contato);
		pedido.setTipoEntrega(TipoEntrega.CIF);
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		pedido.setFormaPagamento("30 dias uteis");
		return pedido;
	}

	public Pedido buildPedidoRevenda() {
		Pedido pedido = buildPedido();
		pedido.setRepresentada(buildRepresentadaRevendedora());
		return pedido;
	}

	public RamoAtividade buildRamoAtividade() {
		RamoAtividade ramoAtividade = new RamoAtividade();
		ramoAtividade.setAtivo(true);
		ramoAtividade.setDescricao("Industria Belica");
		ramoAtividade.setSigla("IB");
		ramoAtividade.setId(gerarId());
		repositorio.inserirEntidade(ramoAtividade);
		return ramoAtividade;
	}

	public Representada buildRepresentada() {
		Representada representada = new Representada(null, "COBEX");
		representada.setAtivo(true);
		representada.setRazaoSocial("COBEX LTDA");
		representada.setEmail("vendas@cobex.com.br");
		representada.setComissao(0.05);
		representada.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO);
		representada.setAliquotaICMS(0.18);
		representada.setCnpj("77336617000107");
		representada.setInscricaoEstadual("123456789");

		Logradouro l = buildLogradouro(TipoLogradouro.FATURAMENTO);
		l.setCep("09910345");
		l.setEndereco("Rua Parnamirim");
		l.setNumero("432");
		l.setCidade("Diadema");
		l.setComplemento("Conjunto 330");
		representada.setLogradouro(l);

		return representada;
	}

	public Representada buildRepresentadaFornecedor() {
		Representada representada = buildRepresentada();
		representada.setNomeFantasia("Fornecedor Plastico");
		representada.setEmail("fornecedorplastico@cobex.com.br");
		representada.setRazaoSocial("Fornecedor Plastico LTDA");
		representada.setTipoRelacionamento(TipoRelacionamento.FORNECIMENTO);
		return representada;
	}

	public Representada buildRepresentadaRevendedora() {
		Representada representada = buildRepresentada();
		representada.setNomeFantasia("Revendedor Plastico");
		representada.setEmail("revendedorplastico@cobex.com.br");
		representada.setRazaoSocial("Revendedor Plastico LTDA");
		representada.setTipoRelacionamento(TipoRelacionamento.REVENDA);
		return representada;
	}

	public Transportadora buildTransportadora() {
		Transportadora t = new Transportadora();
		t.setAtivo(true);
		t.setCnpj("03233998000162");
		t.setInscricaoEstadual("1234567");
		t.setLogradouro(buildLogradouro(TipoLogradouro.FATURAMENTO));
		t.setNomeFantasia("Transport teste");
		t.setRazaoSocial("Transport teste LTDA");
		return t;
	}

	public Usuario buildVendedor() {
		Usuario vendedor = new Usuario(gerarId(), "Vinicius", "Fernandes Vendedor");
		vendedor.setEmail("vinicius@teste.com.br");
		vendedor.setSenha("1234567");
		vendedor.setAtivo(true);
		vendedor.addPerfilAcesso(buildListaPerfilAcesso());
		return vendedor;
	}

	public Integer gerarId() {
		return (int) (9999 * Math.random());
	}

}
