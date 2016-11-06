package br.com.plastecno.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_pedido_nfe", schema = "vendas")
public class PedidoNFe {

	@Column(name = "id_pedido")
	@Id
	private Integer idPedido;

	private Integer modelo;
	private Integer numero;
	private Integer serie;

	@Column(name = "xml_nfe")
	private String xmlNFe;

	public PedidoNFe() {
	}

	public PedidoNFe(Integer idPedido, Integer numero, Integer serie, Integer modelo, String xmlNFe) {
		this.idPedido = idPedido;
		this.numero = numero;
		this.serie = serie;
		this.modelo = modelo;
		this.xmlNFe = xmlNFe;
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

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public void setXmlNFe(String xmlNFe) {
		this.xmlNFe = xmlNFe;
	}
}
