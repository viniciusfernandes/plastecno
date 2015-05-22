package br.com.plastecno.service.test;

public abstract class DAOBuilder<T> {
	static final EntidadeBuilder ENTIDADE_BUILDER = EntidadeBuilder.getInstance();

	static final EntidadeRepository REPOSITORY = EntidadeRepository.getInstance();

	public abstract T build();
}
