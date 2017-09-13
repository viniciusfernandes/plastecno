<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE>
<html>
<head>
<jsp:include page="/bloco/bloco_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/Chart.min.js"/>"></script>

<script type="text/javascript">
$(document).ready(function() {
	
	
	$('#botaoPesquisarPagamentoPeriodo').click(function(){
		var request = $.ajax({
			type: 'get',
			url: '<c:url value="/fluxocaixa/grafico"/>'
		});
		
		
		request.done(function (response){
			var dados = response.dados;
			var ctx = document.getElementById('myChart').getContext('2d');
			var myLineChart = new Chart(ctx, {
			    type: 'bar',
			    data: {
			        labels: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
			        datasets: [{
			            label: "My First dataset",
			            //backgroundColor: 'rgb(255, 99, 132)',
			            borderColor: 'rgb(255, 99, 132)',
			            data:dados,
			        }]
			    },
			    options: {
			        scales: {
			            xAxes: [{
			                stacked: true
			            }],
			            yAxes: [{
			                stacked: true
			            }]
			        }
			    }
			});
		});
		
		request.fail(function(request, status, excecao) {
			var mensagem = 'Falha na pesquisa do item de venda sugerido: '+ idCampoPesquisavel;
			mensagem += ' para a URL ' + url;
			mensagem += ' contendo o valor de requisicao ' + parametro;
			mensagem += ' => Excecao: ' + excecao;
			gerarListaMensagemErro(new Array(mensagem));
		});
	});
	
	
});
</script>

</head>
<body>
	<jsp:include page="/bloco/bloco_mensagem.jsp" />
		<fieldset>
			<legend>::: Fluxo de Caixa do Período :::</legend>
				<div class="label" style="width: 30%">Data Inícial:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="dataInicial" name="dataInicial"
						value="${dataInicial}" maxlength="10" class="pesquisavel" />
				</div>
				<div class="label" style="width: 10%">Data Final:</div>
				<div class="input" style="width: 10%">
					<input type="text" id="dataFinal" name="dataFinal"
						value="${dataFinal}" maxlength="100" class="pesquisavel"
						style="width: 100%" />
				</div>
				<div class="input" style="width: 2%">
					<input type="submit" id="botaoPesquisarPagamentoPeriodo" title="Pesquisar Pagamentos por Período" value="" class="botaoPesquisarPequeno" style="width: 100%"/>
				</div>
				
		</fieldset>
<canvas id="myChart" ></canvas>

Aqui deve estar o fluxo.........
</body>
</html>