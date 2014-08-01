<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
$(document).ready(function() {
	$("#botaoIncluirComentario").click(function () {
		if(!isEmpty($("#comentario").val())) {
			$(this).closest('form').submit();			
		}
	});
	
	$("#botaoLimparComentario").click(function () {
		$("#comentario").val("");	
	});
});
</script>
<fieldset id="bloco_comentario">
<legend>::: Comentários :::</legend>

			<form action="<c:url value="/cliente/inclusao/comentario"/>" method="post">
				<input type="hidden" value="${cliente.id}" name="idCliente"/>
				<div class="label condicional">Comentário:</div>
				<div class="input" style="width: 80%">
					<input type="text" id="comentario" name="comentario" value="${comentario}" style="width: 100%"/> 
				</div>
	
				<div class="bloco_botoes">
					<a id="botaoIncluirComentario" title="Adicionar Dados do Comentario" class="botaoAdicionar" ></a>
					<a id="botaoLimparComentario" title="Limpar Dados do Comentario" class="botaoLimpar" ></a>
				</div>
			
			</form>
						
			<div class="label condicional">Histórico:</div>
			<div class="input areatexto" style="width: 80%">
				<textarea style="width: 100%;" disabled="disabled">
				${comentarios}
				</textarea>
			</div>

			
			
	</fieldset>
	
	