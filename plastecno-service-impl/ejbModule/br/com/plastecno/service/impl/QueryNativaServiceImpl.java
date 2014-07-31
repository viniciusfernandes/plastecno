package br.com.plastecno.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import br.com.plastecno.service.QueryNativaService;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.StringUtils;

@Stateless
public class QueryNativaServiceImpl implements QueryNativaService {

	private final String NOME_DATA_SOURCE = "java:/datasources/plastecno";
	
	private Connection connection;
	
	@Override
	public String executar(String query) throws BusinessException {
		if (StringUtils.isEmpty(query)) {
			return null;
		}
		
		this.abrirConexao();
		try {
			ResultSet resultSet = this.connection.prepareStatement(query).executeQuery();
			final StringBuilder resultado = new StringBuilder();
			final ResultSetMetaData metaData = resultSet.getMetaData(); 
			final int TOTAL_COLUNAS = metaData.getColumnCount();
			int indiceColuna = 1;
			
			while (resultSet.next()) {
				while (indiceColuna <= TOTAL_COLUNAS) {
					resultado.append(metaData.getColumnName(indiceColuna)).append(": ");
					resultado.append(resultSet.getObject(indiceColuna)).append("\t");
					indiceColuna++;
				}
				indiceColuna = 1;
				resultado.append("\n\n");
			}
			return resultado.toString();
		} catch (SQLException e) {
			BusinessException businessException = new BusinessException("Não foi possível executar a query "+query);
			businessException.addMensagem(e.getMessage());
			throw businessException;
		} finally {
			this.fecharConexao();
		}
		
	}

	private void abrirConexao () throws BusinessException {
		DataSource ds;
		try {
			ds = (DataSource) new InitialContext().lookup(NOME_DATA_SOURCE);
		} catch (NamingException e1) {
			throw new BusinessException("Não foi possível encontrato o data source "+NOME_DATA_SOURCE, e1);
		}
        try {
			this.connection = ds.getConnection();
		} catch (SQLException e) {
			throw new BusinessException("Não foi possível abri uma conexao com o banco de dados para execucao de query nativa", e);
		}
	}
	
	private void fecharConexao() throws BusinessException {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				throw new BusinessException("Não foi possível fechar a conexao", e);
			}
		}
	}
}
