<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	inicializarPaginador(
		inicializarFiltro, 
		'<c:out value="${not empty paginaSelecionada ? paginaSelecionada : 1}"/>', 
		'<c:out value="${not empty totalPaginas ? totalPaginas : 1}"/>');