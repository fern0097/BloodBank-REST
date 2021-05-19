package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.BLOOD_RECORD_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.USER_ROLE;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.DonationRecord;

@Path(BLOOD_RECORD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DonationRecordResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	public Response getAllRecords() {
		Response response = null;
		LOG.debug("getting all the donation records info");
		List<DonationRecord> records = service.getAllRecords();
		response = Response.ok(records).build();
		return response;

	}

	@POST
	@RolesAllowed(ADMIN_ROLE)
	public Response addRecord(DonationRecord record) {
		Response response = null;
		DonationRecord newRecord = service.addDonationRecord(record);
		response = Response.ok(newRecord).build();
		return response;
	}

	@DELETE
	@Path(RESOURCE_PATH_ID_PATH)
	@RolesAllowed(ADMIN_ROLE)
	public Response deleteById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		if (service.deleteRecordById(id)) {
			response = Response.noContent().build();

		} else {
			response = Response.status(Status.NOT_FOUND).build();
		}
		return response;

	}

	@GET
	@Path(RESOURCE_PATH_ID_PATH)
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	public Response findRecordById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		DonationRecord record = service.getRecordById(id);
		response = Response.ok(record).build();
		return response;
	}
}
