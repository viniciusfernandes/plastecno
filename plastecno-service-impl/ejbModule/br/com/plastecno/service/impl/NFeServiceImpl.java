package br.com.plastecno.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.NFe;

@Stateless
public class NFeServiceImpl implements NFeService {
	public static void main(String[] args) throws JAXBException {
		NFeService n = new NFeServiceImpl();
		n.gerarNfe(43);
	}

	@EJB
	private PedidoService pedidoService;

	private void carregarIdentificacaoDestinatario(NFe nFe, Integer idPedido) {
		Cliente destinatario = pedidoService
				.pesquisarClienteByIdPedido(idPedido);

		IdentificacaoDestinatarioNFe iDest = new IdentificacaoDestinatarioNFe();
		iDest.setEmail(destinatario.getEmail());
		iDest.setInscricaoEstadual(destinatario.getInscricaoEstadual());
		iDest.setInscricaoMunicipal(null);
		iDest.setInscricaoSUFRAMA(null);
		iDest.setNomeFantasia(destinatario.getNomeFantasia());
		iDest.setTipoContribuinte(null);
		iDest.setTipoDocumento(null);
		iDest.setNomeFantasia(destinatario.getNomeFantasia());

		EnderecoNFe endDest = new EnderecoNFe();
		Logradouro logradouroEmit = destinatario.getLogradouroFaturamento();
		endDest.setBairro(logradouroEmit.getBairro());
		endDest.setCep(logradouroEmit.getCep());
		endDest.setCodigoPais(String.valueOf(55));
		endDest.setComplemento(logradouroEmit.getComplemento());
		endDest.setLogradouro(logradouroEmit.getEndereco());
		endDest.setNomeMunicipio(logradouroEmit.getCidade());
		endDest.setNomePais(logradouroEmit.getPais());
		endDest.setNumero(logradouroEmit.getNumero() == null ? "" : String
				.valueOf(logradouroEmit.getNumero()));
		endDest.setUF(logradouroEmit.getUf());
		endDest.setNomePais(logradouroEmit.getPais());
		endDest.setTelefone(null);

		iDest.setEnderecoDestinatarioNFe(endDest);

		nFe.setIdentificacaoDestinatarioNFe(iDest);
	}

	private void carregarIdentificacaoEmitente(NFe nFe, Integer idPedido) {
		Representada emitente = pedidoService
				.pesquisarRepresentadaIdPedido(idPedido);

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
		endEmit.setLogradouro(logradouroEmit.getEndereco());
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

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNfe(Integer idPedido) {
		NFe nFe = new NFe();

		carregarIdentificacaoEmitente(nFe, idPedido);
		carregarIdentificacaoDestinatario(nFe, idPedido);

		return nFe;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void gerarXMLNfe(NFe nFe) throws BusinessException {
		try {
			JAXBContext context = JAXBContext.newInstance(NFe.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(nFe, System.out);
		} catch (Exception e) {
			throw new BusinessException(
					"Falha na geracao do XML da NFe do pedido No. ", e);
		}
	}
}
