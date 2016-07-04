package br.com.plastecno.service.nfe;

import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Main {

	/**
	 * @param args
	 * @throws JAXBException
	 */
	public static void main(String[] args) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(NFe.class);
		Marshaller m = context.createMarshaller();
		// for pretty-print XML in JAXB
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		NFe nFe = new NFe();
		nFe.setDataHoraEmissao(new Date().toString());
		nFe.setDestinoOperacao(0);
		nFe.setConsumidorFinal(44);

		DetalhamentoProdutoServicoNFe d1 = new DetalhamentoProdutoServicoNFe();
		d1.setNumeroItem(43);

		DetalhamentoProdutoServicoNFe d2 = new DetalhamentoProdutoServicoNFe();
		d2.setNumeroItem(55);

		ProdutoServicoNFe s1 = new ProdutoServicoNFe();
		s1.setCFOP("xxx");
		s1.setCodigo("cod444");

		ProdutoServicoNFe s2 = new ProdutoServicoNFe();
		s2.setCFOP("qqq");
		s2.setCodigo("cod555");

		d1.setProdutoServicoNFe(s1);
		d2.setProdutoServicoNFe(s2);

		nFe.addDetalhamentoProdutoServico(d1);
		nFe.addDetalhamentoProdutoServico(d2);

		m.marshal(nFe, System.out);
	}

}
