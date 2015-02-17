package br.com.plastecno.service.test;

import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Cidade;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pais;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;

public class EntidadeBuilder {
	public static EntidadeBuilder getInstance() {
		return builder;
	}

	private EntidadeRepository repositorio = EntidadeRepository.getInstance();

	private static final EntidadeBuilder builder = new EntidadeBuilder();

	private EntidadeBuilder() {
	}

	public Cliente buildCliente() {
		Cliente cliente = new Cliente();
		cliente.setProspeccaoFinalizada(false);
		cliente.addLogradouro(buildLogradouroCliente(TipoLogradouro.FATURAMENTO));
		cliente.addLogradouro(buildLogradouroCliente(TipoLogradouro.ENTREGA));
		cliente.addLogradouro(buildLogradouroCliente(TipoLogradouro.COBRANCA));
		cliente.setRazaoSocial("Exercito Brasileiro");
		cliente.setNomeFantasia("Exercito Brasileiro");
		cliente.setCnpj("25632147000125");
		cliente.setRamoAtividade(buildRamoAtividade());
		cliente.setId(gerarId());
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

	public ItemPedido buildItemPedido() {
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setAliquotaIPI(11.1d);
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

	public Logradouro buildLogradouro(TipoLogradouro tipoLogradouro) {
		Logradouro logradouro = new Logradouro(buildEndereco());
		logradouro.setTipoLogradouro(tipoLogradouro);
		return logradouro;
	}

	public LogradouroCliente buildLogradouroCliente(TipoLogradouro tipoLogradouro) {
		LogradouroCliente logradouro = new LogradouroCliente(buildEndereco());
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
		pedido.getRepresentada().setNomeFantasia("PLASTECNO FILIAL");
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
		Representada representada = new Representada(1, "COBEX");
		representada.setAtivo(true);
		representada.setRazaoSocial("COBEX LTDA");
		representada.setEmail("vendas@cobex.com.br");
		representada.setComissao(0.05);
		representada.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO);
		return representada;
	}

	public Usuario buildVendedor() {
		Usuario vendedor = new Usuario(gerarId(), "Vinicius", "Fernandes Vendedor");
		vendedor.setVendedorAtivo(true);
		vendedor.setEmail("vinicius@teste.com.br");
		vendedor.setSenha("1234567");
		return vendedor;
	}

	public Integer gerarId() {
		return (int) (9999 * Math.random());
	}

}
