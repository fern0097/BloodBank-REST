/**
 * File: RecordService.java
 * Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package bloodbank.ejb;

import static bloodbank.entity.BloodBank.ALL_BLOODBANKS_QUERY_NAME;
import static bloodbank.entity.BloodBank.DONATION_COUNT;
import static bloodbank.entity.BloodDonation.BLOOD_DONATION_ALL_QUERY;
import static bloodbank.entity.BloodBank.BLOODBANK_BY_ID_QUERY;
import static bloodbank.entity.Person.ALL_PERSONS_QUERY_NAME;
import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.USER_FOR_OWNING_PERSON_QUERY;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.PU_NAME;
import static bloodbank.utility.MyConstants.USER_ROLE;
import static bloodbank.entity.DonationRecord.ALL_RECORDS_QUERY_NAME;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;

/**
 * Stateless Singleton ejb Bean - BloodBankService
 */
@Singleton
public class BloodBankService implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger();

	@PersistenceContext(name = PU_NAME)
	protected EntityManager em;
	@Inject
	protected Pbkdf2PasswordHash pbAndjPasswordHash;

	public List<Person> getAllPeople() {
		TypedQuery<Person> q = em.createNamedQuery(Person.ALL_PERSONS_QUERY_NAME, Person.class);
		return q.getResultList();
	}

	public Person getPersonId(int id) {
		return em.createQuery("SELECT p FROM Person p WHERE p.id = :param1", Person.class).setParameter("param1", id)
				.getSingleResult();
	}

	@Transactional
	public Person persistPerson(Person newPerson) {
		em.persist(newPerson);
		return em.find(Person.class, newPerson.getId());
	}

	@Transactional
	public void buildUserForNewPerson(Person newPerson) {
		SecurityUser userForNewPerson = new SecurityUser();
		userForNewPerson
				.setUsername(DEFAULT_USER_PREFIX + "_" + newPerson.getFirstName() + "." + newPerson.getLastName());
		Map<String, String> pbAndjProperties = new HashMap<>();
		pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
		pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
		pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
		pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
		pbAndjPasswordHash.initialize(pbAndjProperties);
		String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
		userForNewPerson.setPwHash(pwHash);
		userForNewPerson.setPerson(newPerson);
		SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY, SecurityRole.class)
				.setParameter(PARAM1, USER_ROLE).getSingleResult();
		userForNewPerson.getRoles().add(userRole);
		userRole.getUsers().add(userForNewPerson);
		em.persist(userForNewPerson);
	}

	@Transactional
	public Person setAddressFor(int id, Address newAddress) {
		Person pers = em.find(Person.class, id);
		em.merge(pers);
		return pers;
	}

	/**
	 * to update a person
	 * 
	 * @param id                - id of entity to update
	 * @param personWithUpdates - entity with updated information
	 * @return Entity with updated information
	 */
	@Transactional
	public Person updatePersonById(int id, Person personWithUpdates) {
		Person personToBeUpdated = getPersonId(id);
		if (personToBeUpdated != null) {
			em.refresh(personToBeUpdated);
			em.merge(personWithUpdates);
			em.flush();
		}
		return personToBeUpdated;
	}

	/**
	 * to delete a person by id
	 * 
	 * @param id - person id to delete
	 */
	@Transactional
	public void deletePersonById(int id) {
		Person person = getPersonId(id);
		if (person != null) {
			em.refresh(person);
			TypedQuery<SecurityUser> findUser = em.createNamedQuery(USER_FOR_OWNING_PERSON_QUERY, SecurityUser.class)
					.setParameter(PARAM1, person.getId());
			SecurityUser sUser = findUser.getSingleResult();
			em.remove(sUser);
			em.remove(person);
		}
	}

	public List<BloodBank> getAllBloodBanks() {
		TypedQuery<BloodBank> query = em.createNamedQuery(ALL_BLOODBANKS_QUERY_NAME, BloodBank.class);
		return query.getResultList();
	}

	public Long getBloodBankCount() {
		TypedQuery<Long> query = em.createNamedQuery(DONATION_COUNT, Long.class);
		return query.getSingleResult();
	}

	public BloodBank getBloodBankById(int id) {
		TypedQuery<BloodBank> query = em.createNamedQuery(BLOODBANK_BY_ID_QUERY, BloodBank.class).setParameter("param1",
				id);
		return query.getSingleResult();
	}

	@Transactional
	public void deleteBloodBankById(int id) {
		BloodBank bank = getBloodBankById(id);
		if (bank != null) {
			em.remove(bank);
		}
	}

	public List<BloodDonation> getAllBloodDonations() {
		TypedQuery<BloodDonation> q = em.createNamedQuery(BLOOD_DONATION_ALL_QUERY, BloodDonation.class);
		return q.getResultList();
	}

	public boolean deleteDonationById(int id) {
		BloodDonation donation = em.find(BloodDonation.class, id);
		if (donation != null) {
			em.refresh(donation);
			em.remove(donation);
			return true;
		}
		return false;
	}

	@Transactional
	public BloodDonation addBloodDonation(BloodDonation donation) {
		em.persist(donation);
		return em.find(BloodDonation.class, donation.getId());
	}

	public BloodDonation getBloodDonationById(int id) {
		TypedQuery<BloodDonation> query = em
				.createNamedQuery("SELECT b FROM BloodDonation b WHERE b.id = :param1", BloodDonation.class)
				.setParameter("param1", id);
		return query.getSingleResult();
	}

	public List<DonationRecord> getAllRecords() {

		TypedQuery<DonationRecord> q = em.createNamedQuery(ALL_RECORDS_QUERY_NAME, DonationRecord.class);
		return q.getResultList();
	}

	@Transactional
	public DonationRecord addDonationRecord(DonationRecord record) {
		em.persist(record);
		return em.find(DonationRecord.class, record.getId());
	}

	@Transactional
	public boolean deleteRecordById(int id) {
		DonationRecord record = em.find(DonationRecord.class, id);
		if (record != null) {
			em.refresh(record);
			em.remove(record);
			return true;
		}
		return false;
	}

	public DonationRecord getRecordById(int id) {
		TypedQuery<DonationRecord> query = em
				.createNamedQuery("SELECT b FROM DonationRecord b WHERE b.id = :param1", DonationRecord.class)
				.setParameter("param1", id);
		return query.getSingleResult();
	}
}