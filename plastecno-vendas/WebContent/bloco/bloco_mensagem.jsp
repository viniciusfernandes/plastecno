<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
$(document).ready(function(){
	var blocoMensagem = $('#bloco_mensagem');
	blocoMensagem.click(function(){
		$(this).fadeOut();
	});

	<c:if test="${empty listaMensagem}">
		blocoMensagem.fadeOut();	
	</c:if>
});

</script>
<a id="topo"></a>
<div id="bloco_mensagem" class="areaMensagem ${cssMensagem}"
	<c:if test="${empty listaMensagem}"> style="display: none;"</c:if>>
	<ul>
		<c:forEach var="mensagem" items="${listaMensagem}">
			<li>${mensagem}</li>
		</c:forEach>
	</ul>
</div>