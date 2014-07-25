package br.com.plastecno.service.wrapper;

class ChaveValorWrapper<K, V> {
    protected final K chave;
    protected final V valor;

    public ChaveValorWrapper(K chave, V valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public K getChave() {
        return chave;
    }

    public V getValor() {
        return valor;
    }
}
