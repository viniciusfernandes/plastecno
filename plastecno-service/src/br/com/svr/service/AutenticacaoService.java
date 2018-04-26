package br.com.svr.service;

import javax.ejb.Local;

import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.AutenticacaoException;
import br.com.svr.service.exception.CriptografiaException;

@Local
public interface AutenticacaoService {
    Usuario autenticar(String email, String senha) throws AutenticacaoException;

    String criptografar(String paramentro) throws CriptografiaException;

    String decriptografar(String parametro) throws CriptografiaException;
}
