package br.com.svr.service.constante;


public enum TipoLogradouro {
	COBRANCA(0),
	COMERCIAL(1),
	ENTREGA(2),
	FATURAMENTO(3),
	RESIDENCIAL(4);
	
	private final Integer codigo;
	private TipoLogradouro(Integer codigo){this.codigo=codigo;}
	public Integer getCodigo() {
		return codigo;
	}
}
