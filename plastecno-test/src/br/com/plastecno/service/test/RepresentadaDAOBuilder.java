package br.com.plastecno.service.test;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.dao.RepresentadaDAO;
import br.com.plastecno.service.entity.Representada;

public class RepresentadaDAOBuilder extends DAOBuilder<RepresentadaDAO> {

	@Override
	public RepresentadaDAO build() {
		new MockUp<RepresentadaDAO>() {
			@Mock
			Representada pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Representada.class, id);
			}

			@Mock
			String pesquisarNomeFantasiaById(Integer idRepresentada) {
				Representada r = REPOSITORY.pesquisarEntidadeById(Representada.class, idRepresentada);
				return r != null ? r.getNomeFantasia() : null;
			}

			@Mock
			public List<Representada> pesquisarRepresentadaByTipoRelacionamento(boolean ativo, TipoRelacionamento... tipos) {
				List<Representada> l = REPOSITORY.pesquisarTodos(Representada.class);

				if (tipos == null || tipos.length <= 0) {
					return l;
				}

				List<Representada> lista = new ArrayList<Representada>();
				for (Representada representada : l) {
					for (int i = 0; i < tipos.length; i++) {
						if (tipos[i].equals(representada.getTipoRelacionamento())) {
							lista.add(representada);
							break;
						}
					}
				}
				return lista;
			}

			@Mock
			List<Representada> pesquisarRepresentadaExcluindoRelacionamento(Boolean ativo,
					TipoRelacionamento tipoRelacionamento) {
				List<Representada> lista = REPOSITORY.pesquisarEntidadeByRelacionamento(Representada.class, "ativo", true);
				List<Representada> representadas = new ArrayList<Representada>();
				for (Representada representada : lista) {
					if (tipoRelacionamento != null && !tipoRelacionamento.equals(representada.getTipoRelacionamento())) {
						representadas.add(representada);
					}
				}
				return representadas;
			}

			@Mock
			TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada) {
				Representada representada = REPOSITORY.pesquisarEntidadeById(Representada.class, idRepresentada);
				return representada == null ? null : representada.getTipoApresentacaoIPI();
			}

			@Mock
			TipoRelacionamento pesquisarTipoRelacionamento(Integer idRepresentada) {
				return REPOSITORY.pesquisarEntidadeAtributoById(Representada.class, idRepresentada, "tipoRelacionamento",
						TipoRelacionamento.class);
			}
		};
		return new RepresentadaDAO(null);
	}

}
