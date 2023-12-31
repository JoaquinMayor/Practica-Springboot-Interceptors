package com.joaquin.curso.springboot.app.interceptor.springbootinterceptor.interceptors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("loadingTimeInterceptor")
public class LoadingTimeInterceptor implements HandlerInterceptor{ //Siempre implementar en los interceptores
//importante configurar los interceptores en un archivo config 
    private static final Logger logger = LoggerFactory.getLogger(LoadingTimeInterceptor.class);


    @Override //El request podemos recibir datos de la peticion y el resonse es para que nosostros mandemos mensajes, el handler es el controlador
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception { //Se carga antes de cargar la pagina
                HandlerMethod controler = ((HandlerMethod) handler);
        logger.info("LoadingTimeInterceptor: preHandle() entrando..."+ controler.getMethod().getName());

        long start = System.currentTimeMillis(); //Nos guarda momento de inicio en milisegundos
        request.setAttribute("start", start);//guardamos el start en el request porque es lo que se comparte entre el pre y el post y asi ver cuanto se tarda

        Random random = new Random();
        int delay = random.nextInt(500); //Rango de valor entre 0 y 500
        Thread.sleep(delay); 

        Map<String, String> json = new HashMap<>(); //Estas lineas de codigo es por si es falso, que retorne un json de error
        json.put("error", "No tiene acceso a esta pagina!");
        json.put("date", new Date().toString());
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(json); //lo transforma en un json
        response.setContentType("application/json"); //le decimos que le mandamos un json
        response.setStatus(401); //determinamos el status del error
        response.getWriter().write(jsonString); //agregamos el json para mandar

      return false; //si retorna false no se ejecuta la pagina y restringe el acceso al controlador
    } 
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception { //Se ejecuta al terminar la carga de la pagina
        long end = System.currentTimeMillis();
        long start = (long)request.getAttribute("start");
        long result = end - start;
        logger.info("Tiempo trascurrido: " + result +" milisegundos");

        logger.info("LoadingTimeInterceptor: postHandle() saliendo..." + ((HandlerMethod) handler).getMethod().getName()) ;
    }

}
