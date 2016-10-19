package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.NFe;

@Local
public interface NFeService {

	NFe carregarIdentificacaoEmitente(NFe nFe, Integer idPedido);

	String emitirNFe(NFe nFe, Integer idPedido) throws BusinessException;

	List<DuplicataNFe> gerarDuplicataByIdPedido(Integer idPedido);

	EnderecoNFe gerarEnderecoNFe(Logradouro logradouro, String telefone);

	NFe gerarNFeByIdPedido(Integer idPedido) throws BusinessException;

	String gerarXMLNfe(NFe nFe, Integer idPedido) throws BusinessException;

	List<Object[]> pesquisarCFOP();

	void validarEmissaoNFePedido(Integer idPedido) throws BusinessException;

}
