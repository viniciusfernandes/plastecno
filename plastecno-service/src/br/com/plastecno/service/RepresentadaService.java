package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ContatoRepresentada;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface RepresentadaService {
	Integer desativar(Integer id);

	Integer inserir(Representada representada) throws BusinessException;

	Boolean isCalculoIPIHabilitado(Integer idRepresentada);

	boolean isCNPJExistente(Integer id, String cnpj);

	boolean isNomeFantasiaExistente(Integer id, String nomeFantasia);

	boolean isRevendedor(Integer idRepresentada);

	List<Representada> pesquisarBy(Representada filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	Representada pesquisarById(Integer id);

	List<Representada> pesquisarById(List<Integer> listaIdRepresentada);

	List<ContatoRepresentada> pesquisarContato(Integer id);

	List<Representada> pesquisarFornecedor(Boolean ativo);

	List<Representada> pesquisarFornecedorAtivo();

	Logradouro pesquisarLogradorouro(Integer id);

	List<Representada> pesquisarRepresentada();

	List<Representada> pesquisarRepresentada(Boolean ativo);

	List<Representada> pesquisarRepresentadaAtivo();

	List<Representada> pesquisarRepresentadaAtivoByTipoPedido(TipoPedido tipoPedido);

	Representada pesquisarRevendedor();

	TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada);

	Long pesquisarTotalRegistros(Representada filtro, Boolean apenasAtivos);
}
