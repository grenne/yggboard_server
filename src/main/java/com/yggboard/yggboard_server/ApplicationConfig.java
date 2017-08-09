package com.yggboard.yggboard_server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class ApplicationConfig extends Application {

    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(
           		Rest_Crud.class,
           		Rest_Index.class,
           		Rest_UserPerfil.class,
           		Rest_Habilidade.class,
           		Rest_Curso.class,
           		Rest_Badge.class,
           		Rest_Carreira.class,
           		Rest_Email.class,
           		Rest_Usuario.class,
           		Rest_ProcessosBatch.class,
           		Rest_UploadFiles.class,
           		Rest_Objetivos_Empresa.class,
           		Rest_Avaliacao.class,
           		Rest_Hierarquia.class
           	        		));
    }

}
