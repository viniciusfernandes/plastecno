package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.wrapper.Periodo;

@Local
public interface DuplicataService {

	List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo);

}
