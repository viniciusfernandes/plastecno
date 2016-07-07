package br.com.plastecno.service.nfe;

import javax.ejb.Local;

@Local
public interface NFeService {

	NFe gerarNfe(Integer idPedido);

}
