package br.com.plastecno.vendas.relatorio.conversor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.ioc.Component;
import br.com.plastecno.vendas.relatorio.conversor.exception.ConversaoHTML2PDFException;

@Component
// O gerador de relatorio sera injetado pelo vraptor no construtor dos
// controllers
public final class GeradorRelatorioPDF {

    /*
     * Os grupos encontrados em cada linha sao recuperados contendo as chaves
     * como {cliente.nome}, {pedido.id}, etc. Sendo que para recuperarmos os
     * valores dos objetos atraves de reflection devemos remover as chaves,
     * ficando assim: {cliente.nome} => cliente.nome, {pedido.id} => pedido.id
     */
    private static String limpar(String property) {
        return property.replace("{", "").replace("}", "");
    }

    private static Map<String, List<String>> LINHAS = new HashMap<String, List<String>>();

    private StringBuilder cabecalho;
    private final Charset charset;
    private final ConversorHTML2PDF conversor;
    private StringBuilder css;
    private StringBuilder html;
    private boolean layoutConfigurado;
    private final String regex = "\\{[a-zA-Z]+(\\.[a-zA-Z]+)*\\}";

    private final PropertyResolver resolver;

    public GeradorRelatorioPDF() {
        charset = Charset.forName("UTF-8");
        conversor = new ConversorHTML2PDF(charset);
        resolver = new PropertyResolver();
    }

    public void addAtributo(String property, Object value) {
        this.resolver.addProperty(property, value);
    }

    /*
     * Esse metodo foi implementado para reaproveitamento de codigo para futuros
     * relatorios exibirem o mesmo cabecalho, mas ainda nao sei o porque ele
     * esta com problemas no charset, entao removemos a marcacao dele no
     * relatorio de pedidos.
     */
    @Deprecated
    private void addCabecalho(File arquivoCabecalho) throws ConversaoHTML2PDFException {
        this.cabecalho = new StringBuilder();
        copiarConteudo(arquivoCabecalho, this.cabecalho);

        // Temos que adicionar o conteudo do CABECALHO nas propriedades que
        // serao
        // populadas no momento de converter o arquivo para o CABECALHO seja
        // incluido
        // no HTML resultante da conversao.
        this.addAtributo("conteudoCabecalho", this.cabecalho.toString());
    }

    private void addCss(File arquivoCSS) throws ConversaoHTML2PDFException {
        this.css = new StringBuilder();
        copiarConteudo(arquivoCSS, this.css);

        // Temos que adicionar o conteudo CSS nas propriedades que serao
        // populadas no momento de converter o arquivo para o o CSS sea incluido
        // no HTML resultante da conversao.
        this.addAtributo("conteudoCss", this.css.toString());
    }

    private void configurarLayout(File arquivo) throws ConversaoHTML2PDFException {
        // Vamos configuraro o layout apenas 1 vez pois foi definido que o CSS e
        // o cabecalho do relatorio sera o mesmo para todos os arquivos gerados,
        // independente do tipo de relatorio requisitado. Entao, se tivermos um
        // laco para gerar varios relatorios em um lote teremos um ganho de
        // performance ao carregar o CSS.
        if (!this.layoutConfigurado) {
            this.addCss(new File(arquivo.getParentFile(), "/css/relatorio.css"));
            this.addCabecalho(new File(arquivo.getParentFile(), "/cabecalho.html"));
            layoutConfigurado = true;
        }
    }

    private void copiarConteudo(File arquivo, StringBuilder conteudo) throws ConversaoHTML2PDFException {
        BufferedReader reader;
        try {
            /*
             * Aqui nao estamos passando o charset pois o conteudo sera
             * convertido apenas na etapa final ao fazermos o gerarHTML.
             */

            reader = gerarBufferedFileReader(arquivo, null);
        } catch (FileNotFoundException e1) {
            throw new ConversaoHTML2PDFException("Nao foi possivel encontrar o arquivo " + arquivo.getName(), e1);
        }
        String linha = null;
        try {
            while ((linha = reader.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
        } catch (IOException e) {
            throw new ConversaoHTML2PDFException("Falha na leitura do arquivo " + arquivo.getName(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new ConversaoHTML2PDFException("Falha na liberacao dos recursos na leitura do arquivo "
                            + arquivo.getName(), e);
                }
            }
        }
    }

    private BufferedReader gerarBufferedFileReader(File file, Charset charset) throws FileNotFoundException {
        if (charset == null) {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        }
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }

    public String gerarHTML() {
        return html == null ? "" : html.toString();
    }

    private List<String> gerarLinhas(File arquivo) throws ConversaoHTML2PDFException {
        List<String> linhas = LINHAS.get(arquivo.getName());
        if (linhas == null) {

            int numLinha = 1;
            linhas = new LinkedList<String>();
            BufferedReader reader = null;
            try {
                reader = gerarBufferedFileReader(arquivo, charset);
                String linha = null;
                while ((linha = reader.readLine()) != null) {
                    linhas.add(linha);
                    numLinha++;
                }
            } catch (Exception e) {
                throw new ConversaoHTML2PDFException("Falha na leitura do template HTML do relatorio\""
                        + arquivo.getName() + "\". Problemas na linha: " + numLinha, e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    throw new ConversaoHTML2PDFException("Falha ao encerrar a leitura do arquivo \""
                            + arquivo.getName() + "\". Nao foi possivel liberar recursos de leitura do arquivo.", e);
                }
            }
            LINHAS.put(arquivo.getName(), linhas);
        }
        return linhas;
    }

    public byte[] gerarPDF() throws ConversaoHTML2PDFException {
        // Aqui estamos usando o valor defaul do PDF do pedido
        return html == null || html.length() == 0 ? new byte[0] : conversor.converter(new ByteArrayInputStream(this
                .gerarHTML().getBytes(charset)), 590, 840);
    }

    public byte[] gerarPDF(int largula, int altura) throws ConversaoHTML2PDFException {
        return html == null || html.length() == 0 ? new byte[0] : conversor.converter(new ByteArrayInputStream(this
                .gerarHTML().getBytes(charset)), largula, altura);
    }

    public void processar(File arquivo) throws ConversaoHTML2PDFException {

        configurarLayout(arquivo);

        this.html = new StringBuilder();
        final Pattern p = Pattern.compile(regex);
        Matcher m = null;
        String lista = null;
        String grupo = null;
        boolean encontrou = false;
        int numLinha = 1;

        final List<String> linhas = gerarLinhas(arquivo);

        List<String> listaGrupo = new ArrayList<String>();
        // Variavel utilizada para apontar qual eh o numero da linha que teve
        // problemas durante o processamento.
        try {

            for (String linha : linhas) {
                encontrou = false;
                lista = null;
                listaGrupo.clear();

                m = p.matcher(linha);
                while (m.find()) {
                    grupo = m.group(0);
                    listaGrupo.add(grupo);
                    // Verificando se o padrao de iteracao "lista" foi
                    // encontrado
                    if (lista == null && grupo.contains("lista")) {
                        lista = grupo;
                    }

                    // Indicacao de que foi encontrado uma marcacao para
                    // substituir por valores dos objetos
                    encontrou = true;
                }

                // Verificando se a marcacao encontrada sera uma iteracao de uma
                // lista
                if (encontrou && lista != null) {
                    Collection<?> colecao = (Collection<?>) resolver.getValue(limpar(lista));

                    if (colecao != null && !colecao.isEmpty()) {
                        Object valor = null;
                        String linhaParseada = null;

                        // Varrendo os objetos da lista
                        for (Object o : colecao) {
                            linhaParseada = linha;

                            /*
                             * Convencionamos que os atributos que serao
                             * preenchidos foram encontrados apos a "lista", por
                             * exemplo: {listaCliente} ... {nome}, {idade},
                             * {documento}, onde nome, idade, etc, sao atributos
                             * dos objetos contidos em listaCliente. Sendo
                             * assim, ao encontrarmos a listaCliente, pulamos
                             * para os proximos grupos encontrados, ou seja,
                             * nome, idade e documento. Alem disso, todos os
                             * atributos que devem ser repetidos deverao estar
                             * na mesma linha logo apos a marcacao
                             * "listaCliente".
                             */
                            for (String parametro : listaGrupo) {

                                if (parametro.contains("lista")) {
                                    linhaParseada = linhaParseada.replace(parametro, "");
                                    // Pulando para os proximos grupos
                                    // encontrados na linha lida.
                                    continue;
                                }
                                valor = resolver.getValue(limpar(parametro), o);
                                linhaParseada = linhaParseada.replace(parametro, valor == null ? "" : valor.toString());
                            }
                            html.append(linhaParseada).append("\n");
                        }
                    }

                } else if (encontrou) {
                    Object valor = null;
                    for (String re : listaGrupo) {
                        valor = resolver.getValue(limpar(re));
                        linha = linha.replace(re, valor == null ? "" : valor.toString());
                    }
                    html.append(linha).append("\n");
                }

                // Se nao encontrou uma marcacao devemos apenas copiar a linha
                if (!encontrou) {
                    html.append(linha).append("\n");
                }

                numLinha++;
            }
        } catch (Exception e) {
            throw new ConversaoHTML2PDFException("Falha ao popular os valores do relatorio. Problemas na linha: "
                    + numLinha, e);
        }
    }
}
