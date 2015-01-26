package br.com.plastecno.service.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;

import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.dao.GenericDAO;
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
import br.com.plastecno.service.exception.BusinessException;

class MockedRepository {
	private static final Map<Class<?>, List<Object>> entidades = new HashMap<Class<?>, List<Object>>();

	<T> boolean contemEntidade(Class<T> classe, String nomeAtributo, Object valorAtributo, Object valorIdEntidade) {
		T entidade = pesquisarEntidadeById(classe, (Integer) valorIdEntidade);
		if (entidade == null) {
			return false;
		}
		try {
			Field field = classe.getDeclaredField(nomeAtributo);
			field.setAccessible(true);
			try {
				Object valor = field.get(entidade);
				return valor != null && valor.equals(valorAtributo);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalArgumentException("Falha no acesso o valor do atributo \"" + nomeAtributo
						+ "\" da entidade cujo valor eh \"" + valorAtributo + "\"", e);
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \"" + nomeAtributo
					+ "\"", e);
		}
	}

	private Cliente gerarCliente() {
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

		inserirEntidade(bairro);
		inserirEntidade(cidade);
		inserirEntidade(pais);
		inserirEntidade(endereco);
		return endereco;
	}

	Integer gerarId() {
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
		pedido.setTipoPedido(TipoPedido.REPRESENTACAO);
		return pedido;
	}

	public Pedido gerarPedidoRevenda() {
		Pedido pedido = gerarPedido();
		pedido.setTipoPedido(null);
		pedido.getRepresentada().setNomeFantasia("PLASTECNO FILIAL");
		return pedido;
	}

	public RamoAtividade gerarRamoAtividade() {
		RamoAtividade ramoAtividade = new RamoAtividade();
		ramoAtividade.setAtivo(true);
		ramoAtividade.setDescricao("Industria Belica");
		ramoAtividade.setSigla("IB");
		ramoAtividade.setId(gerarId());
		inserirEntidade(ramoAtividade);
		return ramoAtividade;
	}

	public Representada gerarRepresentada() {
		Representada representada = new Representada(1, "COBEX");
		return representada;
	}

	public Usuario gerarVendedor() {
		Usuario vendedor = new Usuario(gerarId(), "Vinicius", "Fernandes Vendedor");
		vendedor.setVendedorAtivo(true);
		inserirEntidade(vendedor);
		return vendedor;
	}

	void init() {
		initGenericDAO();
	}

	private void initGenericDAO() {
		new MockUp<GenericDAO<Object>>() {

			@Mock
			Object alterar(Object t) {
				inserirEntidade(t);
				return t;
			}

			@Mock
			Object inserir(Object t) {
				try {
					Method m = t.getClass().getMethod("setId", Integer.class);
					m.invoke(t, gerarId());
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				inserirEntidade(t);
				return t;
			}

			@Mock
			<T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
					Object nomeIdEntidade, Object valorIdEntidade) {
				return contemEntidade(classe, nomeAtributo, valorAtributo, valorIdEntidade);
			}
		};

	}

	void inserirEntidade(Object entidade) {
		if (!entidades.containsKey(entidade.getClass())) {
			entidades.put(entidade.getClass(), new ArrayList<Object>());
		}
		entidades.get(entidade.getClass()).add(entidade);
	}

	@SuppressWarnings("unchecked")
	<T> T pesquisarEntidadeById(Class<T> classe, Integer Id) {
		if (!entidades.containsKey(classe)) {
			return null;
		}
		Integer idObj = null;
		for (Object o : entidades.get(classe)) {
			try {
				idObj = (Integer) o.getClass().getMethod("getId", (Class[]) null).invoke(o, (Object[]) null);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			if (idObj != null) {
				return (T) o;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	<T> List<T> pesquisarTodos(Class<T> classe) {
		return (List<T>) entidades.get(classe);
	}

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
