package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.NFe;

@Local
public interface NFeService {

	NFe carregarIdentificacaoEmitenteDestinatario(NFe nFe, Integer idPedido);

	NFe gerarNfe(Integer idPedido);

	void gerarXMLNfe(NFe nFe) throws BusinessException;

}
