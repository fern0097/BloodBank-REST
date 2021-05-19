package bloodbank.rest.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.BloodBank;

import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.USER_ROLE;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;

@Path(BLOODBANK_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BloodBankResource {

	private static final Logger LOG = LogManager.getLogger();
	
	@Inject
	protected BloodBankService bloodBankService;
	
	@Inject
	protected SecurityContext sc;
	
	@GET
	@RolesAllowed({ADMIN_ROLE,USER_ROLE})
	public Response getBloodBanks() {
		LOG.debug("getting all Blood Banks");
		List<BloodBank> banks = bloodBankService.getAllBloodBanks();
		Response response = Response.ok(banks).build();
		return response;
	}
	
	@GET
	@Path( "/patch")
	public Response getNumberOfBloodBanks() {
		LOG.debug("Getting total count of Blood banks");
		Long count = bloodBankService.getBloodBankCount();
		Response response = Response.ok(count).build();
		return response;
	}
	
	@GET
	@RolesAllowed({ADMIN_ROLE,USER_ROLE})
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getBloodBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT)int id) {
		LOG.debug("Getting a blood bank by id");
		BloodBank bank = bloodBankService.getBloodBankById(id);
		Response response = Response.ok(bank).build();
		return response;
	}
	
	@DELETE
	@Path( RESOURCE_PATH_ID_PATH)
	@RolesAllowed(ADMIN_ROLE)
	public Response deleteBloodBankById(int id) {
		LOG.debug("deleting a blood bank by id");
		bloodBankService.deleteBloodBankById(id);
		return Response.noContent().build();
	}
}
