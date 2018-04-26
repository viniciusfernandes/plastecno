package br.com.svr.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.svr.service.wrapper.exception.AgrupamentoException;
import br.com.svr.util.NumeroUtils;

/**
 * Classe que encapsula tota a informacao do relatorio de cliente por ramo de
 * atividades.
 */
class Agrupamento<T extends Grupo, K extends Grupo> {

    private final String titulo;
    private final Map<String, T> repositorioGrupo = new HashMap<String, T>();

    public Agrupamento(String titulo) {
        this.titulo = titulo;
    }

    public void addGrupo(T grupo) {
        if (!this.repositorioGrupo.containsKey(grupo.getNome())) {
            this.repositorioGrupo.put(grupo.getNome(), grupo);
        }
    }

    public void addSubgrupo(String nomeGrupo, K subgrupo, Class<T> grupoClass) throws AgrupamentoException {
        T g = this.repositorioGrupo.get(nomeGrupo);
        if (g == null) {
            try {
                g = grupoClass.getConstructor(String.class).newInstance(nomeGrupo);
            } catch (Exception e) {
                throw new AgrupamentoException("Falha na construcao de um grupo do tipo " + grupoClass.getName()
                        + " cujo nome eh " + nomeGrupo, e);
            }
            this.repositorioGrupo.put(nomeGrupo, g);
        }
        g.addSubgrupo(subgrupo);
    }

    public List<T> getListaGrupo() {
        return new ArrayList<T>(this.repositorioGrupo.values());
    }

    public String getTitulo() {
        return titulo;
    }

    public double getValorTotalAgrupado() {
        double total = 0;
        final Set<Entry<String, T>> entrySet = this.repositorioGrupo.entrySet();
        for (Entry<String, T> entry : entrySet) {
            total += entry.getValue().getValorTotal();
        }

        return total;
    }

    public String getValorTotalAgrupadoFormatado() {
        return NumeroUtils.formatarValor2Decimais(this.getValorTotalAgrupado());
    }
}
