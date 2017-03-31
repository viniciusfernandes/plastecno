package br.com.plastecno.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;

@Local
public interface DuplicataService {

	void alterarDataVendimentoById(Integer idDuplicata, Date dataVencimento) throws BusinessException;

	void inserirDuplicata(Integer numeroNFe, List<NFeDuplicata> listaDuplicata);

	List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo);

	NFeDuplicata pesquisarDuplicataById(Integer idDuplicata);

}
