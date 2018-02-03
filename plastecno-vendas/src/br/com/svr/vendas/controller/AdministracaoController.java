package br.com.svr.vendas.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.interceptor.download.FileDownload;
import br.com.svr.service.exception.BusinessException;

@Resource
public final class AdministracaoController extends AbstractController {

    public AdministracaoController(final Result result) {
        super(result);
    }

    @Get("administracao/log")
    public Download efetuarDownloadLogServidor() throws BusinessException {
        final int tamanhoBuffer = 1024;
        final byte[] buffer = new byte[tamanhoBuffer];
        final String nomeArquivoLog = "server.log";
        final String nomeArquivoZipado = "svr-log.zip";
        final File arquivoZipado = new File(System.getProperty("java.io.tmpdir") + File.separator + nomeArquivoZipado);
        ZipOutputStream zos = null;
        FileInputStream in = null;

        try {
            zos = new ZipOutputStream(new FileOutputStream(arquivoZipado));
            ZipEntry ze = new ZipEntry(nomeArquivoLog);
            zos.putNextEntry(ze);
            in = new FileInputStream(System.getProperty("jboss.server.log.dir") + File.separator + nomeArquivoLog);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        } catch (IOException ex) {
            gerarLogErro("compactacao do arquivo de log", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }

            } catch (IOException e) {
                gerarLogErro("liberacao dos recursos utilizados na compactacao do arquivo de log", e);
            }
        }

        final String contentType = "application/txt;charset=ISO-8859-1";
        return new FileDownload(arquivoZipado, contentType, nomeArquivoZipado);
    }
}
