package main.java.com.goxr3plus.sonarparser.swagger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestController()
@RequestMapping()
public class SwaggerHomeController {

	@GetMapping("/api-doc")
	public Mono<Void> home(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.SEE_OTHER);
		response.getHeaders().add(
				HttpHeaders.LOCATION, "/api-doc/index.html?url=" + SwaggerConfiguration.SWAGGER_ENDPOINT);

		return response.setComplete();
	}

//	@GetMapping("/api-doc")
//	public Mono<String> doc() {
//
//		return Mono.just("");
//	}

}
