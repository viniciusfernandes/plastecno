package br.com.plastecno.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;

@Local
public interface DuplicataService {

	void alterarDataVendimentoValorById(Integer idDuplicata, Date dataVencimento, Double valor)
			throws BusinessException;

	void atualizarSituacaoDuplicataVencida();

	void configurarIdCliente();

	void inserirDuplicata(Integer numeroNFe, List<NFeDuplicata> listaDuplicata);

	void liquidarDuplicataById(Integer idDuplicata) throws BusinessException;

	NFeDuplicata pesquisarDuplicataById(Integer idDuplicata);

	List<NFeDuplicata> pesquisarDuplicataByIdCliente(Integer idCliente);

	List<NFeDuplicata> pesquisarDuplicataByIdPedido(Integer idPedido);

	List<NFeDuplicata> pesquisarDuplicataByNumeroNFe(Integer numeroNFe);

	List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo);

	void removerDuplicataById(Integer idDuplicata) throws BusinessException;

	void removerDuplicataByNumeroNFe(Integer numeroNFe);

}
