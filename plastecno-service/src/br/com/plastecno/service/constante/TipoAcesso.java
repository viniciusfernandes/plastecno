package br.com.plastecno.service.constante;

public enum TipoAcesso {
	ADMINISTRACAO,
	ASSOCIACAO_CLIENTE_VENDEDOR,
	CONSULTA_RELATORIO_VENDAS_REPRESENTADA,
	CONSULTA_RELATORIO_CLIENTE_REGIAO,
	CONSULTA_RELATORIO_CLIENTE_VENDEDOR,
	CONSULTA_RELATORIO_ENTREGA,
	CADASTRO_BASICO,
	CADASTRO_CLIENTE,
	CADASTRO_PEDIDO,
	CADASTRO_PEDIDO_COMPRA,
	GERENCIA_VENDAS,
	MANUTENCAO,
	OPERACAO_CONTABIL;
	
	public static TipoAcesso valueOfBy(String descricao) {
		try {
			return valueOf(descricao);
		} catch (IllegalArgumentException e){
			return null;	
		}
	} 
}
