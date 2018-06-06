========== CRIACAO DO BANCO DE DADOS - ESQUEMA ENDERECAMENTO =======================================

1) Devemos construir o esquema de enderecamento via  "php-pg-admin"
2) Utilizar o "wiscp" para copiar os arquivos para a construção do esquema "criacao_tabela_enderecamento.sql" e "update_tabela_enderecamento.sql"
via para o diretorio /home
3) Abriremos uma conexao SSH via putty para execução do script via terminal do PostgreSQL. Para isso vamos utilizar 
host=184.107.24.87, porta=22, usuario=root, senha=XnJ@!1uK
4) Ir ate o diretorio /home e lá estarao os arquivos "criacao_tabela_enderecamento.sql" e "update_tabela_enderecamento.sql"
5) Alternar para o usuario "postgres", para isso rodar o comando "su postgres"
5.1) Criar o banco de dados através do comando CREATE DATABASE <meu banco de dados>;
5.2) Alternar para o banco de dados que acabou de criar através do comando \c <meu banco de dados>;
5.3) Para executar os scripts de criação do banco devemos utilzar \i <meu arquivo.sql>
5.4) É muito importante executar o comando especificando o caminho até o diretório utilizando o slash ("/")
6) Abrir o terminal rodando o "psql" e executar a importacao atraves do comando \i <arquivo-enderecamento.sql> \encoding UTF-8,
	lembrando que o caminho do arquivo deve ser crescrito através de barras invertidas "/".
7) E depois o comando para atualizar as tabelas \i <arquivo-vendas.sql> \encoding UTF-8
8) sair
9) EXEMPLO:
drop schema vendas cascade;
drop schema enderecamento cascade;
create schema vendas;
create schema enderecamento;
\i C:/Users/vinicius/ambiente_trabalho/temp/dump.sql \encoding UTF-8;

========== PROBLEMAS RECORRENTES =======================================
1) Conflitos com versões do java: certifique-se de que os compiladores para exportar o projeto estão na versão 6 da jdk. Já o projeto com os testes
	unitários devem ser compilados e executados com a versão 8 da jdk para que a API de Mock execute.
2) Teste unitário: certifique-se que esse projeto contém a jdk na biblioteca, pois sua execução não ocorrerá se no lugar da 
	jdk contiver a jre.
2) O sistema não efetua login: em alguns momentos o banco de dados apresenta algum problema de socket e não conseguimos efetuar a comunicação 
	com o banco, o que fica claro quando tentamos acessar o sistema via o browser. Temos que reiniciar ou reinstalar o banco.
3) Problemas lançando exceção do tipo Spring, VRaptor, etc: Verificar se os projetos estão compilando ou com algum problema de configuração.
Fechar os projetos e abri-los novamente e tente fechar e abrir novamente o Eclipse, funcionou da ultima vez que o problema surgiu. 
com.caelum.vraptor.proxy.ProxyInvocationException: org.springframework.beans.factory.BeanCurrentlyInCreationException

=========== COMANDOS PROMPT WINDOWS ===================
1) Para remover arquivos recursivamente
for /d /r . %d in (<padrao do nomedo arquivo>) do @if exist "%d" rd /s/q "%d"

================ CONFUIGURACAO DE ENVIO DE EMAIL DO HOTMAIL ==================
Use as configurações a seguir no seu aplicativo de email. 

    Servidor de entrada (POP3)
        Endereço do servidor: pop-mail.outlook.com
        Porta: 995
        Conexão criptografada: SSL 
    Servidor de saída (SMTP)
        Endereço do servidor: smtp-mail.outlook.com
        Porta: 25 (ou 587 se a 25 estiver bloqueada)
        Autenticação: Sim
        Conexão criptografada: TLS 
    Nome de usuário: seu endereço de email
    Senha: sua senha
    email.setTLS(true);