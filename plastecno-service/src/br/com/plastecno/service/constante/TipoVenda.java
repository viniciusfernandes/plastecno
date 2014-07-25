package br.com.plastecno.service.constante;

public enum TipoVenda {
	KILO ('K') ,
	PECA ('P');
	
	private char codigo;
	private TipoVenda(char codigo) {
		this.codigo = codigo;
	}
	
	public char getCodigo() {
		return this.codigo;
	}
}
