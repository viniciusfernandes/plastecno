package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.entity.NFeDuplicata;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.Periodo;

@Local
public interface DuplicataService {

	void alterarDuplicataById(NFeDuplicata duplicata) throws BusinessException;

	void atualizarSituacaoDuplicataVencida();

	void cancelarLiquidacaoDuplicataById(Integer idDuplicata) throws BusinessException;

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
