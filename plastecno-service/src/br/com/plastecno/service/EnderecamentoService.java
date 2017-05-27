package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.UF;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface EnderecamentoService {
    Endereco inserir(Endereco endereco) throws BusinessException;

    boolean isCepExitente(String cep);

    /**
     * Metodo implementado para realizar uma pesquisa dos bairros atraves de um
     * like no prefixo do CEP, ou seja, '099%' ou '09910%' ou '09910470%'.
     * @param cep
     * @return lista de bairros e suas cidades de acordo com o prefixo do CEP.
     */
    List<Bairro> pesquisarBairroByCep(String cep);

    List<Bairro> pesquisarBairroById(List<Integer> listaIdBairro);

    Endereco pesquisarByCep(String cep);

    List<String> pesquisarCEPExistente(List<String> listaCep);

	List<UF> pesquisarUF();
}
