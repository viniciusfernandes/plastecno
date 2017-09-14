<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE>
<html>
<head>

<jsp:include page="/bloco/bloco_css.jsp" />
<jsp:include page="/bloco/bloco_relatorio_css.jsp" />

<script type="text/javascript" src="<c:url value="/js/jquery-min.1.8.3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/util.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mascara.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modalConfirmacao.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/autocomplete.js?${versaoCache}"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.datepicker.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.maskMoney.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.4.dialog.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/Chart.min.js"/>"></script>

<script type="text/javascript">
$(document).ready(function() {
	
	
	$('#botaoPesquisarPagamentoPeriodo').click(function(){
		var request = $.ajax({
			type: 'get',
			url: '<c:url value="/fluxocaixa/grafico/bar/mes"/>',
			data: {dataInicial: $('#dataInicial').val(), dataFinal: $('#dataFinal').val()}
		});
		
		
		request.done(function (response){
			if(response.erros != undefined){
				gerarListaMensagemErro(response.erros);
				return;
			}
			
			var grafico = response.grafico;
			if(grafico == undefined){
				return;
			}
			
			if(grafico.listaLabel == undefined || grafico.listaDado == undefined){
				gerarListaMensagemErro(['A lista de labels e dados estão em branco e devem ser enviadas']);
				return;
			}
			var chart = document.getElementById('myChart');
			chart.innerHTML = '';
			var ctx = document.getElementById('myChart').getContext('2d');
			var myLineChart = new Chart(ctx, {
			    type: 'bar',
			    data: {
			        labels: grafico.listaLabel,
			        datasets: [{
			            label: grafico.titulo,
			            backgroundColor: 'rgb(255, 99, 132)',
			            borderColor: 'rgb(255, 99, 132)',
			            data: grafico.listaDado
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
			gerarListaMensagemErro(['Falha na geracao do grafico de fluxo de caixa por mes. Excecao: ' + excecao]);
		});
		
	});
	inserirMascaraData('dataInicial');
	inserirMascaraData('dataFinal');
	
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
	<div class="input" style="width: 50%">
		<canvas id="myChart" ></canvas>
	</div>
</body>
</html>