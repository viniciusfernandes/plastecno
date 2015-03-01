package br.com.plastecno.vendas.util;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.plastecno.vendas.util.exception.ServiceLocatorException;

public final class ServiceLocator {
    @SuppressWarnings("unchecked")
    public static <T> T locate(Class<T> classe) throws ServiceLocatorException {

        StringBuilder serviceName = new StringBuilder();

        serviceName.append("java:global/plastecno-sistema-teste/plastecno-service-impl-teste/").append(classe.getSimpleName())
                .append("Impl!").append(classe.getName());
        try {
            return (T) context.lookup(serviceName.toString());
        } catch (NamingException e) {
            throw new ServiceLocatorException("Falha na localizacao do servico: " + serviceName, e);
        }
    }
    private static Properties properties;

    private static InitialContext context;

    static {
        properties = new Properties();
        properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        try {
            context = new InitialContext(properties);
        } catch (NamingException e) {
            throw new IllegalStateException("Falha na inicializacao do service locator. "
                    + "Nao foi possivel inicial o contexto para efetuar os lookups dos recursos.", e);
        }
    }

    private ServiceLocator() {
    }
}
