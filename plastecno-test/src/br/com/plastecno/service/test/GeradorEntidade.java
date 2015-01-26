package br.com.plastecno.service.test;

import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoLogradouro;
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

public class GeradorEntidade {
	public static GeradorEntidade getInstance() {
		return gerador;
	}
	private RepositorioEntidade repositorio = RepositorioEntidade.getInstance();

	private static final GeradorEntidade gerador = new GeradorEntidade();

	private GeradorEntidade() {
	}

	public Cliente gerarCliente() {
		Cliente cliente = new Cliente();
		cliente.setProspeccaoFinalizada(false);
		cliente.addLogradouro(gerarLogradouroCliente(TipoLogradouro.FATURAMENTO));
		cliente.addLogradouro(gerarLogradouroCliente(TipoLogradouro.ENTREGA));
		cliente.addLogradouro(gerarLogradouroCliente(TipoLogradouro.COBRANCA));
		cliente.setRazaoSocial("Exercito Brasileiro");
		cliente.setNomeFantasia("Exercito Brasileiro");
		cliente.setCnpj("25632147000125");
		cliente.setRamoAtividade(gerarRamoAtividade());

		cliente.setId(gerarId());
		return cliente;
	}

	public Endereco gerarEndereco() {
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

	public Integer gerarId() {
		return (int) (9999 * Math.random());
	}

	public ItemPedido gerarItemPedido() {
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setAliquotaIPI(11.1d);
		itemPedido.setMaterial(gerarMaterial());
		itemPedido.setFormaMaterial(FormaMaterial.TB);
		itemPedido.setQuantidade(2);
		itemPedido.setMedidaExterna(120d);
		itemPedido.setMedidaInterna(100d);
		itemPedido.setComprimento(1000d);
		itemPedido.setTipoVenda(TipoVenda.KILO);
		itemPedido.setPrecoVenda(60d);
		return itemPedido;
	}

	public Logradouro gerarLogradouro(TipoLogradouro tipoLogradouro) {
		Logradouro logradouro = new Logradouro(gerarEndereco());
		logradouro.setTipoLogradouro(tipoLogradouro);
		return logradouro;
	}

	public LogradouroCliente gerarLogradouroCliente(TipoLogradouro tipoLogradouro) {
		LogradouroCliente logradouro = new LogradouroCliente(gerarEndereco());
		logradouro.setTipoLogradouro(tipoLogradouro);
		return logradouro;
	}

	public Material gerarMaterial() {
		Material material = new Material(1, "PLAST", "PLASTICO DURO");
		material.setPesoEspecifico(0.33);
		return material;
	}

	public Pedido gerarPedido() {
		Usuario vendedor = gerarVendedor();
		Cliente cliente = gerarCliente();
		cliente.setVendedor(vendedor);
		Representada representada = new Representada(1, "COBEX");
		Contato contato = new Contato();
		contato.setNome("Adriano");

		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setRepresentada(representada);
		pedido.setVendedor(vendedor);
		pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
		pedido.setFinalidadePedido(FinalidadePedido.CONSUMO);
		pedido.setContato(contato);
		return pedido;
	}

	public Pedido gerarPedidoRevenda() {
		Pedido pedido = gerarPedido();
		pedido.getRepresentada().setNomeFantasia("PLASTECNO FILIAL");
		return pedido;
	}

	public RamoAtividade gerarRamoAtividade() {
		RamoAtividade ramoAtividade = new RamoAtividade();
		ramoAtividade.setAtivo(true);
		ramoAtividade.setDescricao("Industria Belica");
		ramoAtividade.setSigla("IB");
		ramoAtividade.setId(gerarId());
		repositorio.inserirEntidade(ramoAtividade);
		return ramoAtividade;
	}

	public Representada gerarRepresentada() {
		Representada representada = new Representada(1, "COBEX");
		return representada;
	}

	public Usuario gerarVendedor() {
		Usuario vendedor = new Usuario(gerarId(), "Vinicius", "Fernandes Vendedor");
		vendedor.setVendedorAtivo(true);
		repositorio.inserirEntidade(vendedor);
		return vendedor;
	}

}
