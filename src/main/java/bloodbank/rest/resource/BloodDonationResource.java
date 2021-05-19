package bloodbank.rest.resource;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.BLOOD_DONATION_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.USER_ROLE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.BloodDonation;


@Path(BLOOD_DONATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BloodDonationResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
	public Response getAllDonations() {
		Response response = null;
		LOG.debug("getting all the blood donations info");
		List<BloodDonation> donations = service.getAllBloodDonations();
		response = Response.ok(donations).build();
		return response;

	}

	@DELETE
	@Path(RESOURCE_PATH_ID_PATH)
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	public Response deleteById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		if (service.deleteDonationById(id)) {
			response = Response.noContent().build();

		} else {
			response = Response.status(Status.NOT_FOUND).build();
		}
		return response;

	}

	@POST
	@RolesAllowed(ADMIN_ROLE)
	public Response addBloodDonations(BloodDonation donation) {
		BloodDonation newDonation = service.addBloodDonation(donation);
		Response response = Response.ok(newDonation).build();
		return response;
	}

	@GET
	@Path(RESOURCE_PATH_ID_PATH)
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	public Response findBloodDonationById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		BloodDonation donation = service.getBloodDonationById(id);
		response = Response.ok(donation).build();
		return response;
	}
}
