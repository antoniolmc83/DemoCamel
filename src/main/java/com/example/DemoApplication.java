package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.spring.boot.FatJarRouter;
import org.restlet.Component;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.restlet.ext.spring.SpringServerServlet;

@SpringBootApplication
public class DemoApplication extends FatJarRouter{

//	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
//	}
	
    @Override
    public void configure() {
    	
    	restConfiguration().component("restlet");
    	
        rest("/hello").get().to("direct:hello");
        
        from("direct:hello")
        .transform().simple("Hello World!")
        .process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				List<String> l = new ArrayList<>();
				l.add("file1.txt");
				l.add("file2.txt");
				
				exchange.getIn().getHeaders().put("FILES_LIST", l);
			}
		})
        .log("Size ${headers.FILES_LIST.size}")
        .loop(simple("${headers.FILES_LIST.size}"))
        	.log("INSIDE LOOP ${headers.FILES_LIST.get(${property.CamelLoopIndex})}")
        	.pollEnrich("file:/C:/temp?fileName=${headers.FILES_LIST.get(${property.CamelLoopIndex})}&noop=true")
        	
        .end()
        .log("FIN");
        
        
        from("file:/C:/temp?fileName=file1.txt&noop=false&move=destino")
        .log("file1 - 1")
        .process(new Processor(){

			@Override
			public void process(Exchange arg0) throws Exception {
				System.out.println("ssssss 11");
				
			}
        	
        })
//        .to("file:/C:/temp/destino")
        .log("file1 - 2")
        .setProperty("diffFile", constant("file1.txt_diff"))
        .to("direct:genericdiffstep")
        .end();

        from("file:/C:/temp?fileName=file2.txt&noop=false&move=destino")
        .log("file2 - 1")
        .process(new Processor(){

			@Override
			public void process(Exchange arg0) throws Exception {
				System.out.println("ssssss 22");
				
			}
        	
        })
//        .to("file:/C:/temp/destino")
        .log("file2 - 2")
        .setProperty("diffFile", constant("file2.txt_diff"))
        .to("direct:genericdiffstep")
        .end();
        
        
        from("direct:genericdiffstep")
        .log("genericdiffstep: before poll ${property.diffFile}")
        .pollEnrich().simple("file:/C:/temp?fileName=${property.diffFile}_add")
        .pollEnrich().simple("file:/C:/temp?fileName=${property.diffFile}_old")
        .log("")
        //.aggregationStrategy(new ExampleAggregationStrategy())
        .log("genericdiffstep: after poll ${property.diffFile}")
        .end();
        
        
        

        
    }
	
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
    	
    	SpringServerServlet serverServlet = new SpringServerServlet();
    	ServletRegistrationBean regBean = new ServletRegistrationBean( serverServlet, "/rest/*");
    	
    	
    	Map<String,String> params = new HashMap<String,String>();
    	
    	params.put("org.restlet.component", "restletComponent");
    	
    	regBean.setInitParameters(params);
    	
    	return regBean;
    }    
    @Bean
    public Component restletComponent() {
    	return new Component();
    }
    
    @Bean
    public RestletComponent restletComponentService() {
    	return new RestletComponent(restletComponent());
    }	
}
