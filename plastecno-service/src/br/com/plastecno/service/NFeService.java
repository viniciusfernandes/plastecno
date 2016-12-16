package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.NFe;

@Local
public interface NFeService {

	NFe carregarIdentificacaoEmitente(NFe nFe, Integer idPedido);

	String emitirNFe(NFe nFe, Integer idPedido, boolean isTriangularizacao) throws BusinessException;

	List<DuplicataNFe> gerarDuplicataByIdPedido(Integer idPedido);

	EnderecoNFe gerarEnderecoNFe(Logradouro logradouro, String telefone);

	NFe gerarNFeByIdPedido(Integer idPedido) throws BusinessException;

	NFe gerarNFeByNumero(Integer numero) throws BusinessException;

	Integer[] gerarNumeroSerieModeloNFe() throws BusinessException;

	String gerarXMLNfe(NFe nFe, Integer idPedido) throws BusinessException;

	boolean isNFeEmissaoFinalizada(Integer idPedido);

	List<Object[]> pesquisarCFOP();

	Integer pesquisarIdPedidoByNumeroNFe(Integer numeroNFe);

	List<NFeItemFracionado> pesquisarItemFracionado();

	List<Integer> pesquisarNumeroNFeByIdPedido(Integer idPedido);

	void validarEmissaoNFePedido(Integer idPedido) throws BusinessException;

	void validarNumeroNFePedido(Integer idPedido, Integer numeroNFe) throws BusinessException;

}
