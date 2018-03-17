package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;

@Local
public interface MaterialService {
	void desativar(Integer id);

	Integer inserir(Material material) throws BusinessException;

	Integer inserir(Material material, List<Integer> listaIdRepresentadaAssociada) throws BusinessException;

	boolean isMaterialAssociadoRepresentada(Integer idMaterial, Integer idRepresentada);

	boolean isMaterialExistente(Integer idMaterial);

	boolean isMaterialExistente(String sigla, Integer idMaterial);

	boolean isMaterialImportado(Integer idMaterial);

	PaginacaoWrapper<Material> paginarMaterial(Material filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Material> pesquisarBy(Material filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	Material pesquisarById(Integer id);

	List<Material> pesquisarBySigla(String sigla);

	List<Material> pesquisarBySigla(String sigla, Integer idRepresentada);

	Material pesquisarBySiglaIdentica(String sigla);

	List<Material> pesquisarMaterialAtivoBySigla(String sigla, Integer idRepresentada);

	Double pesquisarPesoEspecificoById(Integer idMaterial);

	List<Representada> pesquisarRepresentadasAssociadas(Integer idMaterial);

	List<Representada> pesquisarRepresentadasNaoAssociadas(Integer idMaterial);

	Long pesquisarTotalRegistros(Material filtro, Boolean apenasAtivos);
}
