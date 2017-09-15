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
			
			var listaGrafico = response.listaGrafico;
			if(listaGrafico == undefined){
				return;
			}

			var datasets = new Array();
			var rgb = [{r:76, g:181, b:56}, {r:69 , g:146, b:224}, {r:219, g:100, b:169}];
			for (var i = 0; i < 3; i++) {
				if(listaGrafico[i].listaLabel == undefined || listaGrafico[i].listaDado == undefined){
					gerarListaMensagemErro(['A lista de labels e dados do gr�fico '+listaGrafico[i].titulo+ ' est�o em branco e devem ser enviadas']);
					return;
				}
				datasets[i] = {
		            label: listaGrafico[i].titulo,
		            backgroundColor: 'rgb('+rgb[i].r+', '+rgb[i].g+', '+rgb[i].b+')',
		            data: listaGrafico[i].listaDado
		        };
			}			
			var ctxFlx = document.getElementById('graficoFluxoMensal').getContext('2d');
			new Chart(ctxFlx, {
			    type: 'bar',
			    data: {
			        labels: listaGrafico[0].listaLabel,
			        datasets: datasets
			    },
			    options: {
				      title: {
				        display: true,
				        text: 'Fuxo de Caixa com Cr�d. ICMS (R$)'
				      }
				    }
			});
			

			var ctxLine = document.getElementById("graficoFaturamentoMensal").getContext("2d");
			new Chart(ctxLine, {
			  type: 'line',
			  data: {
			    labels: listaGrafico[2].listaLabel,
			    datasets: [{
			    	label:'Val. Mensal.',
			      fill: false,
			      backgroundColor: '#8fa8c8',
			      pointBackgroundColor: '#75539e',
			      borderColor: '#75539e',
			      pointHighlightStroke: '#75539e',
			      data: listaGrafico[2].listaDado,
			    }]
			  },
			  options: {
				title: {
					display: true,
				    text: 'Faturamento com Cr�d. ICMS (R$)'
				}
			  }
			});
			
			var ctxPag = document.getElementById("graficoPagamento").getContext("2d");
			new Chart(ctxPag, {
				  type: 'doughnut',
				  data: {
				    labels: listaGrafico[3].listaLabel,
				    datasets: [{
				    	label:'Pag. do per�odo.',
				      backgroundColor: ['#7db9e8', '#76DB79', '#E8997D', '#A85A8B'],
				      data: listaGrafico[3].listaDado,
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
			<legend>::: Fluxo de Caixa do Per�odo :::</legend>
				<div class="label" style="width: 30%">Data In�cial:</div>
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
					<input type="submit" id="botaoPesquisarPagamentoPeriodo" title="Pesquisar Pagamentos por Per�odo" value="" class="botaoPesquisarPequeno" style="width: 100%"/>
				</div>
				
		</fieldset>
	<div class="input" style="width: 50%; height: 20%">
		<canvas id="graficoFluxoMensal" ></canvas>
	</div>
	<div class="input" style="width: 50%; height: 20%">
		<canvas id="graficoFaturamentoMensal" ></canvas>
	</div>
	<div class="input" style="width: 50%; height: 20%; margin-top: 2%">
		<canvas id="graficoPagamento"></canvas>
	</div>
</body>
</html>