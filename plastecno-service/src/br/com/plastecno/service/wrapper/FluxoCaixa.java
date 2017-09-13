package br.com.plastecno.service.wrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.exception.BusinessException;

public class FluxoCaixa {
	private class Node {
		private final String key;
		private Node next;
		private Node parent;
		private Node previous;
		private Node subnode;

		private double value;

		public Node(String key, double value) {
			if (key == null) {
				throw new IllegalStateException("A chave para a criacao do node nao pode ser nula.");
			}

			this.key = key;
			this.value = value;
		}

		public void addNext(Node next) {
			if (!this.hasParent()) {
				throw new IllegalStateException("Tentativa de adicionar um node a um node sem pai.");
			}

			if (next != null) {
				next.parent = this.parent;
			}
			addNext(this, next);

		}

		private void addNext(Node node, Node newNode) {
			if (newNode == null || node == null) {
				return;
			}
			if (newNode.equals(node)) {
				node.value = newNode.value;
				return;
			}

			if (newNode.isAfter(node)) {
				if (!node.hasNext()) {
					node.next = newNode;
					newNode.previous = node;
					newNode.next = null;
					return;
				}

				if (node.hasNext()) {
					if (newNode.equals(node.next)) {
						node.next.value = newNode.value;
						return;
					}

					if (newNode.isBefore(node.next)) {
						newNode.next = node.next;
						newNode.previous = node;

						node.next.previous = newNode;
						node.next = newNode;
						return;
					}

					if (newNode.isAfter(node.next)) {
						addNext(node.next, newNode);
					}
				}
			}

			if (newNode.isBefore(node)) {
				if (!node.hasPrevious()) {
					node.previous = newNode;
					newNode.next = node;
					newNode.previous = null;
					return;
				}

				if (node.hasPrevious()) {
					if (node.previous.equals(newNode)) {
						node.previous.value = newNode.value;
						return;
					}

					if (newNode.isAfter(node.previous)) {
						newNode.previous = node.previous;
						newNode.next = node;

						node.previous.next = newNode;
						node.previous = newNode;
						return;
					}

					if (newNode.isBefore(node.previous)) {
						addNext(node.previous, newNode);
					}
				}
			}
		}

		public void addSubnode(Node subnode) {
			if (subnode == null) {
				return;
			}

			subnode.parent = this;

			if (!this.hasSubnodes()) {
				this.subnode = subnode;
				return;
			}
			addNext(this.subnode, subnode);
		}

		public int compareTo(Node o) {
			return key.compareTo(o.key);
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		public boolean hasNext() {
			return next != null && !next.equals(previous);
		}

		public boolean hasParent() {
			return this.parent != null;
		}

		public boolean hasPrevious() {
			return this.previous != null;
		}

		public boolean hasSubnodes() {
			return subnode != null;
		}

		public boolean isAfter(Node node) {
			return this.compareTo(node) > 0;
		}

		public boolean isBefore(Node node) {
			return this.compareTo(node) < 0;
		}
	}

	private Date dataFinal;
	private Date dataInicial;

	private Map<Date, double[]> fluxo;

	public FluxoCaixa(Periodo periodo) {
		fluxo = new HashMap<>();
		this.dataInicial = periodo.getInicio();
		this.dataFinal = periodo.getFim();

		if (dataInicial == null || dataFinal == null) {
			throw new IllegalStateException("As datas de inicio e fim do fluxo de caixa nao podem ser nulas.");
		}
	}

	private void add(Date dt, Double val, int pos) {
		double[] valores = null;
		if ((valores = fluxo.get(dt)) == null) {
			valores = new double[3];
			fluxo.put(dt, valores);
		}
		valores[pos] = val == null ? 0 : val;
	}

	public void addCreditoICMS(Date dtVencimento, Double valor) throws BusinessException {

		if (!isDataVencimentoValida(dtVencimento)) {
			throw new BusinessException("A data de credito de ICMS esta fora do periodo definido para o fluxo de caixa");
		}
		add(dtVencimento, valor, 2);
	}

	public void addDuplicata(Date dtVencimento, Double valor) throws BusinessException {

		if (!isDataVencimentoValida(dtVencimento)) {
			throw new BusinessException(
					"A data de vencimento da duplicata esta fora do periodo definido para o fluxo de caixa");
		}
		add(dtVencimento, valor, 0);
	}

	public void addPagamento(Date dtVencimento, Double valor) throws BusinessException {

		if (!isDataVencimentoValida(dtVencimento)) {
			throw new BusinessException("A data de pagamento esta fora do periodo definido para o fluxo de caixa");
		}
		add(dtVencimento, valor, 1);
	}

	private boolean isDataVencimentoValida(Date dtVencimento) {
		return dtVencimento != null && (dtVencimento.after(dataInicial) && dtVencimento.before(dataFinal));

	}
}
