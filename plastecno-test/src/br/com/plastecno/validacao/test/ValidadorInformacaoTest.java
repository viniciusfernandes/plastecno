package br.com.plastecno.validacao.test;

import static org.junit.Assert.*;

import org.junit.Test;

import br.com.plastecno.service.test.AbstractTest;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.validacao.ValidadorInformacao;

public class ValidadorInformacaoTest extends AbstractTest {
	private EntidadeValidacao gerarEntidadeValidacao() {
		EntidadeValidacao e = new EntidadeValidacao();
		e.setNomeFantasia("PLASTECNO");
		e.setRazaoSocial("Plastecno");
		e.setCnpj("46825523000178");
		e.setCpf("29186568876");
		e.setInscricaoEstadual("123456789012");
		e.setIdade(12);
		e.setQuantidade(32);
		return e;
	}

	@Override
	public void init() {

	}

	@Test
	public void testCampoTextoNaoObrigatorio() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setRazaoSocial(null);
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertFalse("O campo nao eh obrigatorio e nao precisa ser validado caso nao exista", throwed);
	}

	@Test
	public void testCampoTextoNaoObrigatorioTamanhaExcesso() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setRazaoSocial("sssssssssssssssssssssssssssss");
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo nao eh obrigatorio, mas excede o tamanho limite e deve ser validado", throwed);
	}

	@Test
	public void testCampoTextoNaoObrigatorioVazio() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setRazaoSocial("");
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo nao eh obrigatorio e nao precisa ser validado caso nao exista", throwed);
	}

	@Test
	public void testCampoTextoObrigatorioNulo() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setNomeFantasia(null);
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo foi definido como obrigatorio e deve ser preenchido", throwed);
	}

	@Test
	public void testCampoTextoObrigatorioTamanhoExcesso() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setNomeFantasia("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo excedeu o tamanho limite e deve ser validado", throwed);
	}

	@Test
	public void testCampoTextoObrigatorioVazio() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setNomeFantasia("");
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo foi definido como obrigatorio e deve ser preenchido", throwed);
	}

	@Test
	public void testCNPJNumeroDigitosInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setCnpj(e.getCnpj().substring(0, 13));
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O numero de digitos do CNPJ nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testCNPJPrimeiroDigitoVerificadorInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setCnpj(e.getCnpj().substring(0, 12) + "08");
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O digito verificador do nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testIncricaoEstadualInvalidoTamanhaExcesso() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setInscricaoEstadual(e.getInscricaoEstadual() + "1");
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O numero de caracteres do nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testCNPJSegundoDigitoVerificadorInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setCnpj(e.getCnpj().substring(0, 13) + "0");
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O digito verificador do nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testCNPJSEquencialInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		String CNPJ = null;
		for (int i = 0; i <= 9; i++) {
			CNPJ = "";
			for (int j = 1; j <= 14; j++) {
				CNPJ += i;
			}
			e.setCnpj(CNPJ);
			try {
				ValidadorInformacao.validar(e);
			} catch (InformacaoInvalidaException e1) {
				throwed = true;
			}
			assertTrue("O CNPJ \"" + CNPJ + "\" nao eh valido e deve ser validado", throwed);
		}

	}

	@Test
	public void testCPFNumeroDigitosInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setCpf(e.getCpf().substring(0, 10));
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O numero de digitos do cpf nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testCPFPrimeiroDigitoVerificadorInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setCpf(e.getCpf().substring(0, 9) + "06");
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O digito verificador do nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testCPFSegundoDigitoVerificadorInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		e.setCpf(e.getCpf().substring(0, 10) + "0");
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;

		}
		assertTrue("O digito verificador do nao eh valido e deve ser validado", throwed);
	}

	@Test
	public void testCPFSEquencialInvalido() {
		EntidadeValidacao e = gerarEntidadeValidacao();

		boolean throwed = false;
		String cpf = null;
		for (int i = 0; i <= 9; i++) {
			cpf = "";
			for (int j = 1; j <= 11; j++) {
				cpf += i;
			}
			e.setCpf(cpf);
			try {
				ValidadorInformacao.validar(e);
			} catch (InformacaoInvalidaException e1) {
				throwed = true;
			}
			assertTrue("O cpf \"" + cpf + "\" nao eh valido e deve ser validado", throwed);
		}

	}

	@Test
	public void testValidacaoInformacao() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			printMensagens(e1);
		}
	}

	@Test
	public void testNumeroPositivo() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setIdade(-1);
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo idade nao eh valido e deve ser validado", throwed);

		e.setIdade(0);
		throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertFalse("O campo idade esta zerado e eh positivo e nao deve ser validado", throwed);
	}

	@Test
	public void testNumeroEstritamentoPositivo() {
		EntidadeValidacao e = gerarEntidadeValidacao();
		e.setQuantidade(-1);
		boolean throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo quantidade eh estritamente positivo e nao pode ser negativo", throwed);

		e.setIdade(0);
		throwed = false;
		try {
			ValidadorInformacao.validar(e);
		} catch (InformacaoInvalidaException e1) {
			throwed = true;
		}
		assertTrue("O campo quantidade eh estritamente positivo e nao pode ser zero", throwed);
	}

}
