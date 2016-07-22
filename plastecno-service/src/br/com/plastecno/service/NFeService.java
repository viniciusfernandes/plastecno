package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.NFe;

@Local
public interface NFeService {


	NFe carregarIdentificacaoDestinatario(NFe nFe, Integer idPedido);

	NFe carregarIdentificacaoEmitente(NFe nFe, Integer idPedido);

	void emitirNFe(NFe nFe, Integer idPedido) throws BusinessException;

	EnderecoNFe gerarEnderecoNFe(Logradouro logradouro);

	NFe gerarNfe(Integer idPedido);

	void gerarXMLNfe(NFe nFe) throws BusinessException;

}
