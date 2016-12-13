package br.com.plastecno.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_nfe_pedido", schema = "vendas")
public class NFePedido {

	@Column(name = "id_pedido")
	private Integer idPedido;

	private Integer modelo;

	@Id
	private Integer numero;

	@Column(name = "numero_triang")
	private Integer numeroTriangularizado;

	private Integer serie;

	@Column(name = "xml_nfe")
	private String xmlNFe;

	public NFePedido() {
	}

	public NFePedido(Integer numero, Integer serie, Integer modelo, String xmlNFe, Integer idPedido,
			Integer numeroTriangularizado) {
		this.numero = numero;
		this.serie = serie;
		this.modelo = modelo;
		this.idPedido = idPedido;
		this.xmlNFe = xmlNFe;
		this.numeroTriangularizado = numeroTriangularizado;
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

	public Integer getNumeroTriangularizado() {
		return numeroTriangularizado;
	}

	public Integer getSerie() {
		return serie;
	}

	public String getXmlNFe() {
		return xmlNFe;
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

	public void setNumeroTriangularizado(Integer numeroTriangularizado) {
		this.numeroTriangularizado = numeroTriangularizado;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public void setXmlNFe(String xmlNFe) {
		this.xmlNFe = xmlNFe;
	}

}
