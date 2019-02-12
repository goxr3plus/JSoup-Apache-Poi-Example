package main.java.com.goxr3plus.sonarparser.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class SwaggerConfiguration {

	public final static String SWAGGER_ENDPOINT = "/api/v1.0/api-swagger"; ///api/v1.0/api-swagger
	
	@Configuration
	public static class SwaggerWebFluxConfigurer implements WebFluxConfigurer {

		@Override
		public void addResourceHandlers(final ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/api-doc" + "/**")
					.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/3.20.5/");

			registry.addResourceHandler("/swagger/examples/**")
					.addResourceLocations("classpath:/swagger/examples/");
		}
		
		
	}

}
