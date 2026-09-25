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


    @Override //El request podemos recibir datos de la petición y el response es para que nosotros mandemos mensajes, el handler es el controlador
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception { //Se carga antes de cargar la página
                HandlerMethod controler = ((HandlerMethod) handler);
                if(request.getMethod().equalsIgnoreCase("post")){ //Para que se salga cuando se use en un método post con el mismo nombre o dirección
                    return true;
                }
        logger.info("LoadingTimeInterceptor: preHandle() entrando..."+ controler.getMethod().getName());

        long start = System.currentTimeMillis(); //Nos guarda momento de inicio en milisegundos
        request.setAttribute("start", start);//Guardamos el start en el request porque es lo que se comparte entre el pre y el post y así ver cuánto se tarda

        Random random = new Random();
        int delay = random.nextInt(500); //Rango de valor entre 0 y 499
        Thread.sleep(delay); 

        Map<String, String> json = new HashMap<>(); //Estas líneas de código es por si es falso, que retorne un json de error
        json.put("error", "No tiene acceso a esta página!");
        json.put("date", new Date().toString());
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(json); //Lo transforma en un json
        response.setContentType("application/json"); //Le decimos que le mandamos un json
        response.setStatus(401); //Determinamos el status del error
        response.getWriter().write(jsonString); //Agregamos el json para mandar
        //response.sendRedirect(request.getContextPath().concat("/login")); //Redirige en caso de que se trabaje con Thymeleaf, a otra página en caso de que sea falso, 
      return false; //Si retorna false no se ejecuta la página y restringe el acceso al controlador
    } 
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception { //Se ejecuta al terminar la carga de la página
        if(request.getMethod().equalsIgnoreCase("get")){ //Para que se salga cuando se use en un método post con el mismo nombre o dirección
            
        
        long end = System.currentTimeMillis();
        long start = (long)request.getAttribute("start");
        long result = end - start;
        logger.info("Tiempo trascurrido: " + result +" milisegundos");

        logger.info("LoadingTimeInterceptor: postHandle() saliendo..." + ((HandlerMethod) handler).getMethod().getName());
        }
    }

}
