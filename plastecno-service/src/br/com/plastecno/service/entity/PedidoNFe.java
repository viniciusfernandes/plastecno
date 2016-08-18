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

	@Column(name = "xml_nfe")
	private String xmlNFe;

	public PedidoNFe() {
	}

	public PedidoNFe(Integer idPedido, String xmlNFe) {
		this.idPedido = idPedido;
		this.xmlNFe = xmlNFe;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public String getXmlNFe() {
		return xmlNFe;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setXmlNFe(String xmlNFe) {
		this.xmlNFe = xmlNFe;
	}
}
