<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
							<div class="coluna_acoes_listagem">
									<form action="<c:url value="/pagamento/"/>${elemento.id}" >
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										<input type="submit" value="" title="Editar Pagamento" class="botaoEditar"/>
									</form>
									
									<c:choose>
										<c:when test="${not elemento.liquidado}">
											<form action="<c:url value="/pagamento/liquidacao/"/>${elemento.id}" method="post" >
												<input type="hidden" name="dataInicial" value="${dataInicial}"/>
												<input type="hidden" name="dataFinal" value="${dataFinal}"/>
												<input type="submit" value="" title="Liquidar Pagamento" class="botaoVerificacaoEfetuadaPequeno" />
											</form>
										</c:when>
										<c:otherwise>
											<form action="<c:url value="/pagamento/retonoliquidacao/"/>${elemento.id}" method="post" >
												<input type="hidden" name="dataInicial" value="${dataInicial}"/>
												<input type="hidden" name="dataFinal" value="${dataFinal}"/>
												<input type="submit" value="" title="Retornar Liquidação Pagamento" class="botaoVerificacaoFalhaPequeno" />
											</form>
										</c:otherwise>
									</c:choose>
									<form action="<c:url value="/pagamento/remocao/"/>${elemento.id}" method="post">
										<input type="hidden" name="dataInicial" value="${dataInicial}"/>
										<input type="hidden" name="dataFinal" value="${dataFinal}"/>
										<input type="submit" value="" title="Remover Pagamento" class="botaoRemover"/>
									</form>
								</div>