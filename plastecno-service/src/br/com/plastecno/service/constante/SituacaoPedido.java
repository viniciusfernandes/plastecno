package br.com.plastecno.service.constante;

import java.util.ArrayList;
import java.util.List;


public enum SituacaoPedido {
	DIGITACAO ("DIGITAÇÃO") ,
	ORCAMENTO ("ORÇAMENTO"),
	ENVIADO ("ENVIADO"),
	CANCELADO ("CANCELADO"),
	COMPRA_AGUARDANDO_RECEBIMENTO("COMPRA AGUARDANDO RECEBIMENTO"),
	COMPRA_RECEBIDA("COMPRA RECEBIDA"),
	REVENDA_AGUARDANDO_EMPACOTAMENTO("REVENDA AGUARDANDO EMPACOTAMENTO"),
	ITEM_AGUARDANDO_COMPRA("ITEM AGUARDANDO COMPRA"),
	EMPACOTADO("EMPACOTADO"),
	COMPRA_ANDAMENTO("COMPRA EM ANDAMENTO"),
	ITEM_AGUARDANDO_MATERIAL("ITEM AGUARDANDO MATERIAL"),
	REVENDA_PARCIALMENTE_RESERVADA("REVENDA PARCIALMENTE RESERVADA"),
	ORCAMENTO_DIGITACAO ("ORÇAMENTO DIGITAÇÃO"),
	ORCAMENTO_ACEITO ("ORÇAMENTO ACEITO"),
	ORCAMENTO_CANCELADO("ORÇAMENTO CANCELADO");
	
	private String descricao;
	
	private SituacaoPedido(String descricao){
		this.descricao = descricao;
		
	}
	
	public String getDescricao(){
		return this.descricao;
	}
	
	private static List<SituacaoPedido> listaOrcamento = new ArrayList<SituacaoPedido>();
	static{
		listaOrcamento.add(ORCAMENTO);
		listaOrcamento.add(ORCAMENTO_ACEITO);
		listaOrcamento.add(ORCAMENTO_CANCELADO);
		listaOrcamento.add(ORCAMENTO_DIGITACAO);
	}
	public static List<SituacaoPedido> getListaOrcamento(){
		return listaOrcamento;
	}
}
