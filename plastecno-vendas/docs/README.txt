========== PAINEL DE GERENCIAMENTO NO SERVIDOR JAVA ==========================

1) Para configurar serviços e datasource do servidor JBoss7 devemos acessar o painel de gerenciamento
na URL http://plastecno.com:9990, com o usuario=fernando e senha=@jboss715

2) Para criar um usuario novo deve-se ir ate o diretorio de instalacao do jboss <JBOSS_HOME>\bin 
e executar o script add-user.bat. Para isso deve-se conectar via ftp. 

========== DEPLOY DA APLICACAO NO SERVIDOR JAVA ==========================

1) Acessar o painel do jboss: http://174.142.42.90:8666/zp/appservermgr/
2) Autenticar do painel do jboss: user=plastecno,  senha=93104827
3) Parar o servidor para efetuar o envio do pacote via FTP, pois a transferecia do pacote eh demorada
	e o hot deploy pode apresentar problemas
4) Utilizar o LeechFTP para enviar o pacote .ear: host=174.142.42.90, senha=93104827
5) Copiar os arquivos .ear e o driver do banco de dados para appservers/jboss-7.x/standalone/deployments

========== CRIACAO DO BANCO DE DADOS - ESQUEMA VENDAS =======================================

1) Para acessar o banco de dados devemos ir ate o painel que fica em http://184.107.24.87/php-pg-admin/
e autenticar com user=postgres, senha=XnJ@!1uK
2) Selecionar o nó "esquema" da arvore e abrir a janela para execucao de "SQLs"
3) Colar o script para criacao do esquema de vendas no 	SQL editor

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
