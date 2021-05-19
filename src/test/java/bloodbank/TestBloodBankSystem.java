/**
 * File: OrderSystemTestSuite.java
 * Course materials (20F) CST 8277
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Student Name
 */
package bloodbank;

import static bloodbank.utility.MyConstants.APPLICATION_API_VERSION;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PERSON_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.PublicBloodBank;
import bloodbank.utility.MyConstants;

/*
* Failing test 28, 29
*/

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestBloodBankSystem {
	private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	private static final Logger logger = LogManager.getLogger(_thisClaz);

	static final String APPLICATION_CONTEXT_ROOT = "REST-BloodBank-Skeleton";
	static final String HTTP_SCHEMA = "http";
	static final String HOST = "localhost";
	static final int PORT = 8080;

	// test fixture(s)
	static URI uri;
	static HttpAuthenticationFeature adminAuth;
	static HttpAuthenticationFeature userAuth;

	@BeforeAll
	public static void oneTimeSetUp() throws Exception {
		logger.debug("oneTimeSetUp");
		uri = UriBuilder.fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION).scheme(HTTP_SCHEMA).host(HOST)
				.port(PORT).build();
		adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
		userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER_PREFIX, DEFAULT_USER_PASSWORD);
	}

	protected WebTarget webTarget;

	@BeforeEach
	public void setUp() {
		Client client = ClientBuilder
				.newClient(new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
		webTarget = client.target(uri);
	}

	@Test
	public void test01_all_customers_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
		List<Person> emps = response.readEntity(new GenericType<List<Person>>() {
		});
		assertThat(emps, is(not(empty())));

//		assertThat(emps, hasSize(1));

	}

	@Test
	public void test02_first_customer_last_name_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(new GenericType<List<Person>>() {
		}).get(0).getLastName(), is("Emami"));

	}

	@Test
	public void test03_get_person_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.PERSON_RESOURCE_NAME + "/" + 1).request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(new GenericType<Person>() {
		}).getLastName(), is("Emami"));

	}

	Person storedPerson;

	@Test
	public void test04_add_person_adminrole() throws JsonMappingException, JsonProcessingException {

		Person newPerson = new Person();
		newPerson.setFullName("harry", "potter");

		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PERSON_RESOURCE_NAME).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(newPerson, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));

		storedPerson = response.readEntity(Person.class);

		assertThat(storedPerson.getLastName(), is("potter"));
	}

	@Test
	public void test05_all_customers_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
//				.register(adminAuth)
				.path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(401));

	}

	@Test
	public void test06_first_customer_last_name_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(401));

	}

	@Test
	public void test07_get_person_by_id_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(MyConstants.PERSON_RESOURCE_NAME + "/" + 1).request()
				.get();
		assertThat(response.getStatus(), is(401));

	}

	@Test
	public void test08_add_person_userrole() throws JsonMappingException, JsonProcessingException {

		Person newPerson = new Person();
		newPerson.setFullName("harry", "potter");

		Response response = webTarget.register(userAuth).path(PERSON_RESOURCE_NAME).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(newPerson, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(401));
	}

	@Test
	public void test09_get_all_bloodbank_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.BLOODBANK_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
	}

	@Test
	public void test10_get_all_bloodbank_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
//				.register(adminAuth)
				.path(MyConstants.BLOODBANK_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(401));
	}

	@Test
	public void test11_get_bloodbank_with_id_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
//				.register(adminAuth)
				.path(MyConstants.BLOODBANK_RESOURCE_NAME + "/" + 1).request().get();
		assertThat(response.getStatus(), is(401));
	}

	BloodBank storedBB;

	@Test
	public void test12_all_customer_invalid_media_type() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));

	}

	@Test
	public void test13_get_customer_invalid_media_type() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));
	}

	@Test
	public void test14_get_person_by_id_invalid_media_type() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.PERSON_RESOURCE_NAME + "/" + 1).request().get();
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));
	}

	@Test
	public void test15_add_person_invalid_media() throws JsonMappingException, JsonProcessingException {
		Person newPerson = new Person();
		newPerson.setFullName("harry1", "potter1");

		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PERSON_RESOURCE_NAME).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(newPerson, MediaType.APPLICATION_JSON));
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));
	}

	@Test
	void test_16_getAllPersonWith_UserRoleTest() {
		Response response = webTarget.register(userAuth).path(PERSON_RESOURCE_NAME).request().get();
		assertEquals(401, response.getStatus());
	}

	@Test
	public void test17_get_all_donation_records_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME).request()
				.get();
		assertThat(response.getStatus(), is(401));
	}

	@Test
	public void test18_get_all_donation_records_invalid_media() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME).request().get();
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));
	}

	DonationRecord storedDonationRecord;

	@Test
	void test19_getPersonById_Not_The_CurrentUserTest() {
		Response response = webTarget.register(userAuth).path(PERSON_RESOURCE_NAME + "/ " + 1).request().get();
		assertEquals(401, response.getStatus()); // forbidden
	}

	@Test
	public void test20_get_donation_record_by_id_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME + "/" + 1)
				.request().get();
		assertThat(response.getStatus(), is(401));
	}

	@Test
	public void test21_get_donation_record_by_id_invalid_media() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME + "/" + 1).request().get();
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));
	}

	@Test
	public void test22_delete_donation_record_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME + "/" + 1).request().delete();
		assertThat(response.getStatus(), is(404));
	}

	@Test
	public void test23_delete_donation_record_by_id_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME + "/" + 1)
				.request().delete();
		assertThat(response.getStatus(), is(401));
	}

	@Test
	public void test24_delete_donation_record_by_id_invalid_mediatype()
			throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MyConstants.DONATION_RECORD_RESOURCE_NAME + "/" + 1).request().delete();
		assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML)));
	}

	@Test
	public void test25_cannot_retrieve_address_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
				.path(MyConstants.PERSON_RESOURCE_NAME + "/" + MyConstants.CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + 1)
				.request().get();
		assertThat(response.getStatus(), is(401));
	}

	@Test
	public void test26_non_existent_person__adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(MyConstants.PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
	}

	@Test
	void test_27_add_PersonWith_UserRoleTest() {
		Person newPerson = new Person();
		newPerson.setFirstName("First Name");
		newPerson.setLastName("Last Name");
		newPerson.setFullName(newPerson.getFirstName(), newPerson.getLastName());

		Response response = webTarget.register(userAuth).path(PERSON_RESOURCE_NAME).request()
				.post(Entity.json(newPerson));
		assertEquals(401, response.getStatus()); // unauthorized
	}

	@Test
	public void test28_non_existent_bloodbank__adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(MyConstants.BLOODBANK_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));

	}

	@Test
	void test_29_add_Address_For_PersonWith_AdminRoleTest() {
		Address newAddress = new Address();
		newAddress.setCity("Manchester");
		newAddress.setCountry("UK");
		newAddress.setStreet("Main Street");
		newAddress.setProvince("Central");

		Response response = webTarget.register(adminAuth).path(PERSON_RESOURCE_NAME + "/1/address").request()
				.put(Entity.json(newAddress));
		assertEquals(200, response.getStatus());
	}

	@Test
	public void test30_add_Address_For_PersonWithUser_RoleTest() throws JsonMappingException, JsonProcessingException {
		Address newAddress = new Address();
		newAddress.setCity("Manchester");
		newAddress.setCountry("UK");
		newAddress.setStreet("Main Street");
		newAddress.setProvince("Central");

		Response response = webTarget.register(userAuth).path(PERSON_RESOURCE_NAME + "/1/address").request()
				.put(Entity.json(newAddress));
		assertEquals(401, response.getStatus());
	}
	
	@Test
	void test_31_UserRole_Cannot_Update_Test() {
		BloodBank blood=new PublicBloodBank();
		
		String updateValue="bloody school";
		blood.setName(updateValue);
		
      Response response = webTarget
              .register(userAuth)
              .path(BLOODBANK_RESOURCE_NAME + "/" + 1)
              .request()
              .put(Entity.json(updateValue));
    
      assertThat(response.getStatus(),is(401));
  }
	
}