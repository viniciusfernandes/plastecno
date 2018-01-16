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
<style type="text/css">
#graficoTipoPagamento, #graficoFaturamentoAnual {
	margin-top: 150px;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	var charts = new Array();
	
	$('#botaoPesquisarPagamentoPeriodo').click(function(){
		var request = $.ajax({
			type: 'get',
			url: '<c:url value="/fluxocaixa/graficos"/>',
			data: {dataInicial: $('#dataInicial').val(), dataFinal: $('#dataFinal').val()}
		});
		
		
		request.done(function (response){
			if(response.erros != undefined){
				gerarListaMensagemErro(response.erros);
				return;
			}
			
			var painel = response.painelGrafico;
			if(painel == undefined){
				return;
			}
			<%-- Limpando os grafico para uma nova renderizacao --%>
			if(charts.length >0){
				for (var i = 0; i < charts.length; i++) {
					charts[i].destroy();
				}
			}
			<%-- Criando arrays dos grafico apenas para a inicializacao do dataset --%>
			var graficos = [painel.graficoFluxoMensal, painel.graficoFaturamentoMensal, painel.graficoPagamentoMensal];
			var datasets = new Array();
			var cores = ['#4CB538', '#4592E0', '#DB64A9'];
			for (var i = 0; i < graficos.length; i++) {
				if(graficos[i].listaLabel == undefined || graficos[i].listaDado == undefined){
					gerarListaMensagemErro(['A lista de labels e dados do gráfico '+graficos[i].titulo+ ' estão em branco e devem ser enviadas']);
					return;
				}
				datasets[i] = {
		            label: graficos[i].titulo,
		            backgroundColor: cores[i],
		            data: graficos[i].listaDado
		        };
			}			
			var ctxFlx = document.getElementById('graficoFluxoMensal').getContext('2d');
			 charts[0] = new Chart(ctxFlx, {
			    type: 'bar',
			    data: {
			        labels: graficos[0].listaLabel,
			        datasets: datasets
			    },
			    options: {
				      title: {
				        display: true,
				        text: 'Fuxo de Caixa com Créd. ICMS (R$)'
				      }
				    }
			});
			

			var ctxLine = document.getElementById("graficoFaturamentoMensal").getContext("2d");
			charts[1] = new Chart(ctxLine, {
			  type: 'line',
			  data: {
			    labels: painel.graficoFaturamentoMensal.listaLabel,
			    datasets: [{
			    	label:'Valores',
			      fill: false,
			      backgroundColor: '#8fa8c8',
			      pointBackgroundColor: '#75539e',
			      borderColor: '#75539e',
			      pointHighlightStroke: '#75539e',
			      data: painel.graficoFaturamentoMensal.listaDado,
			    }]
			  },
			  options: {
				title: {
					display: true,
				    text: 'Faturamento Mensal com Créd. ICMS (R$)'
				}
			  }
			});
			
			var ctxFatAnual = document.getElementById("graficoFaturamentoAnual").getContext("2d");
			charts[2] = new Chart(ctxFatAnual, {
			  type: 'bar',
			  data: {
			    labels: painel.graficoFaturamentoAnual.listaLabel,
			    datasets: [{
			    	label:'Valores',
			      backgroundColor: '#C96AC3',
			      borderColor: '#75539e',
			      data:  painel.graficoFaturamentoAnual.listaDado,
			    }]
			  },
			  options: {
				title: {
					display: true,
				    text: 'Faturamento Anual com Créd. ICMS (R$)'
				}
			  }
			});
			
			
			var ctxPag = document.getElementById("graficoTipoPagamento").getContext("2d");
			charts[3] = new Chart(ctxPag, {
				  type: 'doughnut',
				  data: {
				    labels: painel.graficoTipoPagamento.listaLabel,
				    datasets: [{
				    	label:'Pag. do período.',
				      backgroundColor: ['#7db9e8', '#76DB79', '#E8997D', '#A85A8B', '#C5CC6E', '#C9776C', '#5999A5'],
				      data: painel.graficoTipoPagamento.listaDado,
				    }]
				  },
				  options: {
					title: {
						display: true,
					    text: 'Pagamentos (R$)'
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
	
	$('#botaoPesquisarPagamentoPeriodo').click();
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
	<div class="input" style="width: 40%; height: 20%">
		<canvas id="graficoFluxoMensal" ></canvas>
	</div>
	<div class="input" style="width: 40%; height: 20%">
		<canvas id="graficoFaturamentoMensal" ></canvas>
	</div>
	<div class="input" style="width: 40%; height: 20%; margin-top: 2%">
		<canvas id="graficoTipoPagamento"></canvas>
	</div>
	<div class="input" style="width: 40%; height: 20%; margin-top: 2%">
		<canvas id="graficoFaturamentoAnual"></canvas>
	</div>
</body>
</html>