package br.com.plastecno.service.nfe.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.NFeService;

@Stateless
public class NfeServiceImpl implements NFeService {
	@EJB
	private PedidoService pedidoService;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNfe(Integer idPedido) {
		NFe nFe = new NFe();
		
		IdentificacaoDestinatarioNFe iDest = new IdentificacaoDestinatarioNFe();

		Cliente destinatario = pedidoService
				.pesquisarClienteByIdPedido(idPedido);

		carregarIdentificacaoEmitente(nFe, idPedido);

		return null;

	}

	private void carregarIdentificacaoEmitente(NFe nFe, Integer idPedido) {
		Representada emitente = pedidoService
				.pesquisarRepresentadaResumidaByIdPedido(idPedido);

		IdentificacaoEmitenteNFe iEmit = new IdentificacaoEmitenteNFe();
		iEmit.setCNPJ(emitente.getCnpj());
		iEmit.setInscricaoEstadual(emitente.getInscricaoEstadual());
		iEmit.setNomeFantasia(emitente.getNomeFantasia());
		iEmit.setRazaoSocial(emitente.getRazaoSocial());

		Logradouro logradouroEmit = emitente.getLogradouro();
		EnderecoNFe endEmit = new EnderecoNFe();
		endEmit.setBairro(logradouroEmit.getBairro());
		endEmit.setCep(logradouroEmit.getCep());
		endEmit.setCodigoPais(String.valueOf(55));
		endEmit.setComplemento(logradouroEmit.getComplemento());
		endEmit.setLogradouro(logradouroEmit.getDescricao());
		endEmit.setNomeMunicipio(logradouroEmit.getCidade());
		endEmit.setNomePais(logradouroEmit.getPais());
		endEmit.setNumero(logradouroEmit.getNumero() == null ? "" : String
				.valueOf(logradouroEmit.getNumero()));
		endEmit.setUF(logradouroEmit.getUf());
		endEmit.setNomePais(logradouroEmit.getPais());
		endEmit.setTelefone(null);

		iEmit.setEnderecoEmitenteNFe(endEmit);

		nFe.setIdentificacaoEmitenteNFe(iEmit);
	}
}
