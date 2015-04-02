package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_usuario", schema = "vendas")
@InformacaoValidavel
public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1885114358725849134L;

	@Id
	@SequenceGenerator(name = "usuarioSequence", sequenceName = "vendas.seq_usuario_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuarioSequence")
	private Integer id;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 6, 30 }, nomeExibicao = "Senha do usuario")
	private String senha;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 50 }, nomeExibicao = "Email do usuario")
	private String email;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 20 }, nomeExibicao = "Nome do usuario")
	private String nome;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 40 }, nomeExibicao = "Sobrenome do usuario")
	private String sobrenome;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CPF, nomeExibicao = "CPF")
	private String cpf;

	private boolean ativo;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_usuario_tb_perfil_acesso", schema = "vendas", joinColumns = { @JoinColumn(name = "id_usuario") }, inverseJoinColumns = { @JoinColumn(name = "id_perfil_acesso") })
	@InformacaoValidavel(nomeExibicao = "Perfil do usuario")
	private List<PerfilAcesso> listaPerfilAcesso;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de contato do usuario")
	private List<ContatoUsuario> listaContato;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_logradouro")
	@InformacaoValidavel(cascata = true, nomeExibicao = "Logradouro da usuario")
	private Logradouro logradouro;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	private List<Remuneracao> listaRemuneracao;

	@OneToMany(mappedBy = "vendedor", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	private List<Cliente> listaCliente;

	public Usuario() {
	}

	public Usuario(Integer id) {
		this.id = id;
	}

	public Usuario(Integer id, String nome) {
		this(id);
		this.nome = nome;
	}

	public Usuario(Integer id, String nome, String sobrenome) {
		this(id, nome);
		this.sobrenome = sobrenome;
	}

	public void addCliente(Cliente cliente) {
		if (this.listaCliente == null) {
			this.listaCliente = new ArrayList<Cliente>();
		}
		cliente.setVendedor(this);
		this.listaCliente.add(cliente);
	}

	public void addContato(ContatoUsuario contato) {
		if (this.listaContato == null) {
			this.listaContato = new ArrayList<ContatoUsuario>();
		}
		this.listaContato.add(contato);
		contato.setUsuario(this);
	}

	public void addContato(List<ContatoUsuario> listaContato) {
		for (ContatoUsuario contato : listaContato) {
			this.addContato(contato);
		}
	}

	public void addPerfilAcesso(List<PerfilAcesso> listaPerfilAcesso) {
		if (this.listaPerfilAcesso == null) {
			this.listaPerfilAcesso = new ArrayList<PerfilAcesso>();
		}
		for (PerfilAcesso perfilAcesso : listaPerfilAcesso) {
			this.listaPerfilAcesso.add(perfilAcesso);
		}
	}

	public void addPerfilAcesso(PerfilAcesso perfilAcesso) {
		if (this.listaPerfilAcesso == null) {
			this.listaPerfilAcesso = new ArrayList<PerfilAcesso>();
		}
		this.listaPerfilAcesso.add(perfilAcesso);
	}

	public void addRemuneracao(List<Remuneracao> listaRemuneracao) {
		for (Remuneracao remuneracao : listaRemuneracao) {
			this.addRemuneracao(remuneracao);
		}
	}

	public void addRemuneracao(Remuneracao remuneracao) {
		if (this.listaRemuneracao == null) {
			this.listaRemuneracao = new ArrayList<Remuneracao>();
		}
		this.listaRemuneracao.add(remuneracao);
		remuneracao.setUsuario(this);
	}

	public String getCpf() {
		return cpf;
	}

	public String getEmail() {
		return email;
	}

	public Integer getId() {
		return id;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public List<ContatoUsuario> getListaContato() {
		return listaContato;
	}

	public List<PerfilAcesso> getListaPerfilAcesso() {
		return listaPerfilAcesso;
	}

	List<Remuneracao> getListaRemuneracao() {
		return listaRemuneracao;
	}

	public Logradouro getLogradouro() {
		return logradouro;
	}

	public String getNome() {
		return nome;
	}

	public String getNomeCompleto() {
		return this.nome + " " + this.sobrenome;
	}

	public Remuneracao getRemuneracaoVigente() {
		Remuneracao remuneracaoVigente = null;
		if (this.listaRemuneracao == null) {
			return remuneracaoVigente;
		}

		for (Remuneracao remuneracaoVendedor : this.listaRemuneracao) {
			if (remuneracaoVendedor.getDataFimVigencia() == null) {
				remuneracaoVigente = remuneracaoVendedor;
				break;
			}
		}
		return remuneracaoVigente;
	}

	public String getSenha() {
		return senha;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public boolean isComprador() {
		if (listaPerfilAcesso == null) {
			return false;
		}
		for (PerfilAcesso perfilAcesso : listaPerfilAcesso) {
			if (TipoAcesso.CADASTRO_PEDIDO_COMPRA.toString().equals(perfilAcesso.getDescricao())) {
				return true;
			}
		}
		return false;
	}

	public void limparListaCliente() {
		if (this.listaCliente != null) {
			this.listaCliente.clear();
		}
	}

	public void removerListaCliente(List<Cliente> listaCliente) {
		if (this.listaCliente != null) {
			this.listaCliente.removeAll(listaCliente);
		}
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public void setListaContato(List<ContatoUsuario> listaContato) {
		this.listaContato = listaContato;
	}

	public void setListaPerfilAcesso(List<PerfilAcesso> listaPerfilAcesso) {
		this.listaPerfilAcesso = listaPerfilAcesso;
	}

	public void setListaRemuneracao(List<Remuneracao> listaRemuneracao) {
		this.listaRemuneracao = listaRemuneracao;
	}

	public void setLogradouro(Logradouro logradouro) {
		this.logradouro = logradouro;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}
}
