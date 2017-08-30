package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface PagamentoService {

	Integer inserir(Pagamento pagamento) throws BusinessException;

	List<Pagamento> pesquisarByIdPedido(Integer idPedido);


}
