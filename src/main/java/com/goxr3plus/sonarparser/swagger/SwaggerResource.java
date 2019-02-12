package main.java.com.goxr3plus.sonarparser.swagger;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.config.Scanner;
import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Info;
import io.swagger.models.Swagger;

/**
 *	Expose the swagger API listing without the ".json" at the end. 
 *  We had to do that because ".json" was removed by the configured extensionMappings of CXF. 
 */
@Path(SwaggerConfiguration.SWAGGER_ENDPOINT)
@Component
public class SwaggerResource {//extends ApiListingResource {

//	private ApiListingResource wrapped = new ApiListingResource();

	private Swagger swagger;
	
	@Autowired
	private Environment env;
	
	@PostConstruct
	public void init() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setSchemes(new String[] { "http", "https" });
		beanConfig.setResourcePackage("com.goxr3plus.sonarparser");
		beanConfig.setScan(true);

		// TODO
		Info swaggerInfo = new Info();
		swaggerInfo.setTitle(env.getProperty("spring.application.name"));
		swaggerInfo.setDescription("API specification");
		swaggerInfo.setVersion("1.0");
		beanConfig.setInfo(swaggerInfo);
	}
	
	@GET
	@Path("")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getListing(
			@Context final Application app,
			@Context final HttpHeaders headers,
			@Context final UriInfo uriInfo,
			@QueryParam("type") final String type) throws IOException {
		return Response.ok(scan(app, uriInfo)).build();
    }

    private synchronized Swagger scan(final Application app, final UriInfo uriInfo) {
    	if (swagger != null)
    		return swagger;
    	
        SwaggerContextService ctxService = new SwaggerContextService()
//            .withServletConfig(sc)
            .withBasePath(getBasePath(uriInfo));

        Scanner scanner = ctxService.getScanner();
        if (scanner != null) {
            SwaggerSerializers.setPrettyPrint(scanner.getPrettyPrint());
            swagger = new SwaggerContextService()
//                .withServletConfig(sc)
                .withBasePath(getBasePath(uriInfo))
                .getSwagger();
            Set<Class<?>> classes;
//            if (scanner instanceof JaxrsScanner) {
//                JaxrsScanner jaxrsScanner = (JaxrsScanner) scanner;
//                classes = jaxrsScanner.classesFromContext(app, sc);
//            } else {
                classes = scanner.classes();
//            }
            if (classes != null) {
                Reader reader = new Reader(swagger, null);//context
                swagger = reader.read(classes);
                if (scanner instanceof SwaggerConfig) {
                    swagger = ((SwaggerConfig) scanner).configure(swagger);
                } else {
                    SwaggerConfig swaggerConfig = ctxService.getConfig();
                    if (swaggerConfig != null) {
//                        LOGGER.debug("configuring swagger with " + swaggerConfig);
                        swaggerConfig.configure(swagger);
                    } else {
//                        LOGGER.debug("no configurator");
                    }
                }
                new SwaggerContextService()
//                    .withServletConfig(sc)
                    .withBasePath(getBasePath(uriInfo))
                    .updateSwagger(swagger);
            }
        }

        return swagger;
    }

    private static String getBasePath(final UriInfo uriInfo) {
        if (uriInfo != null) {
            return uriInfo.getBaseUri().getPath();
        } else {
            return "/";
        }
    }
}
