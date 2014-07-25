<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<fieldset id="bloco_contato">
<legend>::: Dados do Contato :::</legend>
			<input type="hidden" id="contato_idContato" name="contato.id" value="${contato.id}" />

			<div class="label condicional">Nome:</div>
			<div class="input" style="width: 39%">
				<input type="text" id="contato_nome" name="contato.nome" value="${contato.nome}" /> 
			</div>

			<div class="label" style="width: 6%">Email:</div>
			<div class="input" style="width: 34%">
				<input type="text" id="contato_email" name="contato.email" value="${contato.email}" style="width: 100%" class="apenasLowerCase uppercaseBloqueado lowerCase"/>
			</div>
			
			<div class="label">DDI:</div>
			<div class="input" style="width: 4%">
				<c:choose>
					<c:when test="${empty contato or empty contato.ddi}">
						<input type="text" id="contato_ddi" name="contato.ddi" value="55" />
					</c:when>
					<c:otherwise>
						<td><input type="text" id="contato_ddi" name="contato.ddi" value="${contato.ddi}" /></td>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="label" style="width: 6%">DDD:</div>
			<div class="input" style="width: 4%">
				<input type="text" id="contato_ddd" name="contato.ddd" value="${contato.ddd}" maxlength="2" />
			</div>
			
			<div class="label" style="width: 8%">Telefone:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="contato_telefone" name="contato.telefone" value="${contato.telefone}" maxlength="9" />
			</div>

			<div class="label" style="width: 6%">Ramal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="contato_ramal" name="contato.ramal" value="${contato.ramal}" maxlength="5"/>
			</div>
			
			<div class="label" style="width: 5%">FAX:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="contato_fax" name="contato.fax" value="${contato.fax}" maxlength="8" style="width: 90%"/>
			</div>
			
			<div class="label">DDI:</div>
			<div class="input" style="width: 4%">
				<c:choose>
					<c:when test="${empty contato or empty contato.ddiSecundario}">
						<input type="text" id="contato_ddiSecundario" name="contato.ddiSecundario" value="55" maxlength="3"  />
					</c:when>
					<c:otherwise>
					<td><input type="text" id="contato_ddiSecundario" name="contato.ddiSecundario" value="${contato.ddiSecundario}" maxlength="3"  /></td>
					</c:otherwise>
				</c:choose>			
			</div>

			<div class="label" style="width: 6%">DDD:</div>
			<div class="input" style="width: 4%">
				<input type="text" id="contato_dddSecundario" name="contato.dddSecundario" value="${contato.dddSecundario}" maxlength="2" />
			</div>
			
			<div class="label" style="width: 8%">Telefone:</div>
			<div class="input" style="width: 15%">
				<input type="text" id="contato_telefoneSecundario" name="contato.telefoneSecundario" value="${contato.telefoneSecundario}" maxlength="9" />
			</div>

			<div class="label" style="width: 6%">Ramal:</div>
			<div class="input" style="width: 10%">
				<input type="text" id="contato_ramalSecundario" name="contato.ramalSecundario" value="${contato.ramalSecundario}" maxlength="5" />
			</div>
			
			<div class="label" style="width: 5%">FAX:</div>
			<div class="input" style="width: 20%">
				<input type="text" id="contato_faxSecundario" name="contato.faxSecundario" value="${contato.faxSecundario}" maxlength="8" style="width: 90%"/>
			</div>
			
			<c:if test="${!listaContatoDesabilitada}">
			<div class="bloco_botoes">
				<a id="botaoIncluirContato" title="Adicionar Dados do Contato" class="botaoAdicionar" ></a>
				<a id="botaoLimparContato" title="Limpar Dados do Contato" class="botaoLimpar" ></a>
			</div>
			
			<div style="width: 100%; margin-top: 15px;">
				<table id="tabelaContatos" class="listrada">
					<thead>
						<tr>
							<th style="width: 22%">Nome</th>
							<th style="width: 24%">Email</th>
							<th style="width: 22%">Pri. (DDI / DDD) Tel. / Ram. / Fax</th>
							<th style="width: 22%">Sec. (DDI / DDD) Tel. / Ram. / Fax</th>
							<th>A��es</th>
						</tr>
					</thead>
		
					<tbody>
						<c:forEach items="${listaContato}" var="contato" varStatus="status">
							<tr id="${status.count - 1}">
								<td style="display: none;">${contato.id}</td>
								<td>${contato.nome}</td>
								<td >${contato.email}</td>
								<td >${contato.telefoneFormatado}</td>
								<td >${contato.telefoneSecundarioFormatado}</td>
								<td >
									<input type="button" value="" title="Editar Dados do Contato"  onclick="editarContato(this);" class="botaoEditar"/>
									<input type="button" value="" title="Remover Dados do Contato" onclick="removerContato(this);" class="botaoRemover"/>
								</td>
							</tr>
						</c:forEach>
		
		
					</tbody>
				</table>
			</div>
			</c:if>
			
	</fieldset>
	
	