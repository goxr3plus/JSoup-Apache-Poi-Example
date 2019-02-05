package main.java.com.goxr3plus.sonarparser.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Component
@Path("/api/v1.0/bankfiles")
@Api(tags = "Bank Files Service")
public class Service {


	@GET
	@Path("upload/operator/{operatorId}/year/{year}/period/{accCd}")
	@Produces({MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation(value = "Upload Cyprus Bank Files")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 500, message = "SERVICE UNAVAILABLE")
	})
	public void uploadCyprusFiles(
		@ApiParam(value = "Operator Id")
		@PathParam("operatorId") final Integer operatorId,
		@ApiParam(value = "Account Period Year")
		@PathParam("year") final Integer year,
		@ApiParam(value = "Account Period Code")
		@PathParam("accCd") final Integer accCd) {

	}

}
