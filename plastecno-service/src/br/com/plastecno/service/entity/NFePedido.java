package br.com.plastecno.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.plastecno.service.nfe.constante.TipoNFe;
import br.com.plastecno.service.nfe.constante.TipoSituacaoNFe;

@Entity
@Table(name = "tb_nfe_pedido", schema = "vendas")
public class NFePedido {

	@Column(name = "id_pedido")
	private Integer idPedido;

	private Integer modelo;

	@Id
	private Integer numero;

	@Column(name = "numero_associado")
	private Integer numeroAssociado;

	private Integer serie;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_nfe")
	private TipoNFe tipoNFe;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_situacao_nfe")
	private TipoSituacaoNFe tipoSituacaoNFe;

	@Column(name = "xml_nfe")
	private String xmlNFe;

	public NFePedido() {
	}

	public NFePedido(Integer numero) {
		this.numero = numero;
	}

	public NFePedido(Integer numero, Integer serie, Integer modelo, String xmlNFe, Integer idPedido,
			Integer numeroAssociado, TipoNFe tipoNFe, TipoSituacaoNFe tipoSituacaoNFe) {
		this.numero = numero;
		this.serie = serie;
		this.modelo = modelo;
		this.idPedido = idPedido;
		this.xmlNFe = xmlNFe;
		this.numeroAssociado = numeroAssociado;
		this.tipoNFe = tipoNFe;
		this.tipoSituacaoNFe = tipoSituacaoNFe;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getModelo() {
		return modelo;
	}

	public Integer getNumero() {
		return numero;
	}

	public Integer getNumeroAssociado() {
		return numeroAssociado;
	}

	public Integer getSerie() {
		return serie;
	}

	public TipoNFe getTipoNFe() {
		return tipoNFe;
	}

	public TipoSituacaoNFe getTipoSituacaoNFe() {
		return tipoSituacaoNFe;
	}

	public String getXmlNFe() {
		return xmlNFe;
	}

	public boolean isTriangularizacao() {
		return TipoNFe.TRIANGULARIZACAO.equals(tipoNFe);
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setModelo(Integer modelo) {
		this.modelo = modelo;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public void setNumeroAssociado(Integer numeroAssociado) {
		this.numeroAssociado = numeroAssociado;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public void setTipoNFe(TipoNFe tipoNFe) {
		this.tipoNFe = tipoNFe;
	}

	public void setTipoSituacaoNFe(TipoSituacaoNFe tipoSituacaoNFe) {
		this.tipoSituacaoNFe = tipoSituacaoNFe;
	}

	public void setXmlNFe(String xmlNFe) {
		this.xmlNFe = xmlNFe;
	}

}
