<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">


$(document).ready(function(){
	$('#cep').attr('maxlength', 8);
	
	$('#botaoPesquisaEndereco').click(function () {
		var cep = $('#cep').val(); 
		if (cep == undefined || cep == null || cep.trim().length == 0) {
			gerarListaMensagemAlerta(new Array('O CEP precisa ser preenchido para a pesquisa de endereço'));
			return;
		}
		
		var request = $.ajax({
							type: "get",
							url: '<c:url value="/cep/endereco"/>',
							data: 'cep='+$('#cep').val(),
						});
		request.done(function(response) {
			var endereco = response.endereco;
			if(endereco==undefined || endereco==null){
				return;
			}
			$('#endereco').val(endereco.descricao);
			$('#bairro').val(endereco.bairro.descricao);
			$('#cidade').val(endereco.cidade.descricao);
			$('#uf').val(endereco.cidade.uf);
			$('#pais').val(endereco.cidade.pais.descricao);
			$('#idCidade').val(endereco.cidade.id);
			$('#codigoMunicipio').val(endereco.cidade.codigoMunicipio);
		});
		
		request.fail(function(request, status) {
			alert('Falha na busca do CEP: ' + $('#cep').val()+' => Status da requisicao: '+status);
		});
	});	
});


function desabilitarCamposEndereco(isEnderecoExistente) {
	habilitar('#endereco', !isEnderecoExistente);
	habilitar('#bairro', !isEnderecoExistente);
	habilitar('#cidade', !isEnderecoExistente);
	habilitar('#uf', !isEnderecoExistente);
	habilitar('#pais', !isEnderecoExistente);
}
	
</script>
<fieldset id="bloco_logradouro">
	<legend>::: Endereço :::</legend>
	<input type="hidden" id="idLogradouro" name="logradouro.id" value="${logradouro.id}" />

	<c:if test="${isTipoLogradoutoHabilitado}">
		<div class="label">Tipo End.:</div>
		<div class="input" style="width: 80%">
			<select id="tipoLogradouro" name="logradouro.tipoLogradouro"
				style="width: 15%">
				<option value="">&lt&lt SELECIONE &gt&gt</option>
				<c:forEach var="tipo" items="${listaTipoLogradouro}">
					<option value="${tipo}"
						<c:if test="${tipo eq tipoLogradouroSelecionado}">selected</c:if>>${tipo}</option>
				</c:forEach>
			</select>
		</div>
	</c:if>

	<div class="label condicional">CEP:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="cep" name="logradouro.cep" maxlength="8"
			value="${logradouro.cep}" />
	</div>
	<div class="input" style="width: 2%">
		<input type="button" id="botaoPesquisaEndereco"
			title="Pesquisar Endereço" value="" class="botaoPesquisarPequeno"
			style="width: 20px" />
	</div>
	<c:if test="${not possuiMultiplosLogradouros}">
		<div class="input" style="width: 2%">
			<input type="button" id="botaoRemoverLogradouro"
				title="Remover Logradouro" value="" class="botaoRemover"
				style="width: 20px" />
		</div>
	</c:if>
	<div class="label" style="width: 7%">Cod. Mun.:</div>
	<div class="input" style="width: 10%">
		<input type="text" id="codigoMunicipio" name="logradouro.codigoMunicipio" maxlength="7"
			value="${logradouro.codigoMunicipio}" />
	</div>
	<div class="label" style="width: 10%">End. codificado:</div>
	<div class="input" style="width: 40%">
		<input type="checkbox" id="codificado" name="logradouro.codificado"
			<c:if test="${empty logradouro or (not empty logradouro and logradouro.codificado)}">checked="checked"</c:if>
			class="checkbox" />
	</div>
	<div class="label">Endereço:</div>
	<div class="input" style="width: 40%">
		<input type="text" id="endereco" name="logradouro.endereco"
			value="${logradouro.endereco}" class="uppercaseBloqueado" />
	</div>
	<div class="label" style="width: 8%">Número:</div>
	<div class="input" style="width: 30%">
		<input type="text" id="numero" name="logradouro.numero"
			value="${logradouro.numero}" maxlength="5" style="width: 25%" />
	</div>
	<div class="label">Complemento:</div>
	<div class="input" style="width: 70%">
		<input type="text" id="complemento" name="logradouro.complemento"
			value="${logradouro.complemento}" class="uppercaseBloqueado"
			style="width: 57%" />
	</div>
	<div class="label">Cidade:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="cidade" name="logradouro.cidade"
			value="${logradouro.cidade}" style="width: 40%"
			class="uppercaseBloqueado" />
	</div>
	<div class="label">Bairro:</div>
	<div class="input" style="width: 80%">
		<input type="text" id="bairro" name="logradouro.bairro"
			value="${logradouro.bairro}" style="width: 40%"
			class="uppercaseBloqueado" />
	</div>
	<div class="label">UF:</div>
	<div class="input" style="width: 5%">
		<input type="text" id="uf" name="logradouro.uf"
			value="${logradouro.uf}" />
	</div>
	<div class="label" style="width: 5%">País:</div>
	<div class="input" style="width: 21%">
		<input type="text" id="pais" name="logradouro.pais"
			value="${logradouro.pais}" class="uppercaseBloqueado" />
	</div>
	<c:if test="${possuiMultiplosLogradouros}">
		<div class="bloco_botoes">
			<a id="botaoIncluirLogradouro" title="Adicionar Dados do Logradouro" class="botaoAdicionar"></a>
			<a id="botaoLimparLogradouro" title="Limpar Dados do Logradouro" class="botaoLimpar"></a>
		</div>
	</c:if>

	<c:if test="${possuiMultiplosLogradouros}">
		<div style="width: 100%; margin-top: 15px;">
			<table id="tabelaLogradouro" class="listrada">
				<thead>
					<tr>

						<th style="width: 7%">Tipo</th>
						<th style="width: 10%">CEP</th>
						<th style="width: 25%">Endereço</th>
						<th style="width: 5%">Número</th>
						<th style="width: 10%">Complemento</th>
						<th style="width: 10%">Bairro</th>
						<th style="width: 10%">Cidade</th>
						<th style="width: 5%">UF</th>
						<th style="width: 10%">País</th>
						<th style="width: 10%">Cód.</th>
						<th>Ações</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${listaLogradouro}" var="logradouro"
						varStatus="status">
						<tr id="${status.count - 1}">
							<td style="display: none;">${logradouro.id}</td>
							<td style="text-align: center;">${logradouro.tipoLogradouro}</td>
							<td>${logradouro.cep}</td>
							<td>${logradouro.endereco}</td>
							<td>${logradouro.numero}</td>
							<td>${logradouro.complemento}</td>
							<td>${logradouro.bairro}</td>
							<td>${logradouro.cidade}</td>
							<td>${logradouro.uf}</td>
							<td>${logradouro.pais}</td>
							<td>${logradouro.codigoMunicipio}</td>
							<td style="display: none;">${logradouro.codificado}</td>
							<td>
								<input type="button" value="" title="Editar Dados do Logradouro" onclick="editarLogradouro(this);" class="botaoEditar" /> 
								<input type="button" value="" title="Remover Dados do Logradouro" onclick="removerLogradouro(this);" class="botaoRemover" />
							</td>
						</tr>
					</c:forEach>


				</tbody>
			</table>
		</div>
	</c:if>

</fieldset>

