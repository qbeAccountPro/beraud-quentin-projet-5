package com.safetynet.alerts.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.web.dao.MedicalRecordDao;
import com.safetynet.alerts.web.dao.PersonDao;
import com.safetynet.alerts.web.deserialization.model.PersonDeserialization;
import com.safetynet.alerts.web.logging.EndpointsLogger;
import com.safetynet.alerts.web.model.Household;
import com.safetynet.alerts.web.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Some javadoc.
 * 
 * This service class provides operations related to the person entity.
 * It interacts with the PersonDao to perform CRUD operations on person
 * objects.
 */
@Service
public class PersonService {

  @Autowired
  private final PersonDao personDao;
  private final HouseHoldService householdService;
  private final MedicalRecordDao medicalRecordDao;
  private EndpointsLogger log = new EndpointsLogger();

  public PersonService(PersonDao personDao, HouseHoldService houseHoldService, MedicalRecordDao medicalRecordDao) {
    this.personDao = personDao;
    this.householdService = houseHoldService;
    this.medicalRecordDao = medicalRecordDao;
  }

  /**
   * Some javadoc.
   * 
   * Adds a new person to the system or returns an error if a person with the same
   * first name and last name already exists.
   *
   * @param personDeserialize The deserialized person data.
   * @param methodeName       The name of the method invoking the operation.
   * @return A ResponseEntity indicating the result of the operation.
   */
  public ResponseEntity<String> addPerson(PersonDeserialization personDeserialize, String methodeName) {
    // Check if a person with the same FirstName and lastName exists :
    String firstName = personDeserialize.getFirstName();
    String lastName = personDeserialize.getLastName();
    Person matchingPerson = personDao.findByFirstNameAndLastName(firstName, lastName);
    if (matchingPerson == null) {
      // Create and convert the person from deserialization format to system format:
      Person person = new Person();
      person = convertPersonDeserializeOnPerson(personDeserialize);

      // Get the corresponding household :
      String address = personDeserialize.getAddress();
      Household household = householdService.getHouseholdByAddress(address);

      // If the households does not exist, one is created :
      if (household == null) {
        household = new Household();
        household = householdService.saveHousehold(address);
      }

      person.setIdHousehold(household.getId());
      personDao.save(person);
      return log.addedSuccessfully(methodeName);
    } else {
      return log.ExistingPerson(methodeName);
    }
  }

  /**
   * Some javadoc.
   * 
   * Updates a person's information based on their first name and last name or
   * returns an error if the person does not exist.
   *
   * @param firstName         The first name of the person to update.
   * @param lastName          The last name of the person to update.
   * @param personDeserialize The deserialized person data for the update.
   * @param methodeName       The name of the method invoking the operation.
   * @return A ResponseEntity indicating the result of the operation.
   */
  public ResponseEntity<String> updateByFirstAndLastName(String firstName, String lastName,
      PersonDeserialization personDeserialize, String methodeName) {

    // Get the corresponding person :
    Person person = personDao.findByFirstNameAndLastName(firstName, lastName);
    if (person != null) {
      // Get the address and check if exits or creates it :
      String address = personDeserialize.getAddress();
      Household household = householdService.getHouseholdByAddress(address);
      if (household == null) {
        household = householdService.saveHousehold(address);
      }

      updatePerson(person, personDeserialize, household.getId());
      return log.updatedSuccessfully(methodeName);
    } else {
      return log.argumentHasNoMatch(methodeName);
    }
  }

  /**
   * Some javadoc.
   * 
   * Deletes a person based on their first name and last name or returns an error
   * if the person does not exist.
   *
   * @param firstName   The first name of the person to delete.
   * @param lastName    The last name of the person to delete.
   * @param methodeName The name of the method invoking the operation.
   * @return A ResponseEntity indicating the result of the operation.
   */
  public ResponseEntity<String> deleteByFirstAndLastName(String firstName, String lastName, String methodeName) {
    // Check the person existing :
    Person person = personDao.findByFirstNameAndLastName(firstName, lastName);
    if (person != null) {
      medicalRecordDao.deleteByIdPerson(person.getId());
      personDao.deleteByFirstNameAndLastName(firstName, lastName);
      return log.deletedSuccessfully(methodeName);
    } else {
      return log.argumentHasNoMatch(methodeName);
    }
  }

  /**
   * Some javadoc.
   * 
   * Get a list of all persons in the system.
   *
   * @return A list of all person objects.
   */
  public List<Person> getAllPersons() {
    return personDao.findAll();
  }

  /**
   * Some javadoc.
   * 
   * Convert a person with deserialization format to a person with sytem format.
   *
   * @param personDeserialization A person with deserialization format.
   * @return Return a person with system format.
   */
  private Person convertPersonDeserializeOnPerson(PersonDeserialization personDeserialization) {
    Person person = new Person();
    person.setFirstName(personDeserialization.getFirstName());
    person.setLastName(personDeserialization.getLastName());
    person.setPhone(personDeserialization.getPhone());
    person.setZip(personDeserialization.getZip());
    person.setEmail(personDeserialization.getEmail());
    person.setCity(personDeserialization.getCity());
    return person;
  }

  /**
   * Some javadoc.
   * 
   * Update a person object from the sytem.
   *
   * @param person            The person present in the system.
   * @param deserializePerson The new data for the person in the system.
   * @param idHousehold       The id corresponding at the deserializePerson.
   * 
   * @return A list of person objects corresponding to the provided first
   *         name and
   *         last name.
   */
  public void updatePerson(Person person, PersonDeserialization deserializePerson, int idHousehold) {
    person.setIdHousehold(idHousehold);
    person.setCity(deserializePerson.getCity());
    person.setEmail(deserializePerson.getEmail());
    person.setPhone(deserializePerson.getPhone());
    person.setZip(deserializePerson.getZip());
    personDao.save(person);
  }

  /**
   * Some javadoc.
   * 
   * Get a Person based on the provided ID.
   *
   * @param id The ID of the person object to find.
   * @return The person object corresponding to the provided ID, or null if
   *         not
   *         found.
   */
  public Person getPersonById(int id) {
    Optional<Person> personOptional = personDao.findById(id);
    return personOptional.orElse(null);
  }

  /**
   * Some javadoc.
   * 
   * Retrieves a list of persons associated with a list of households.
   *
   * @param households The list of households for which to retrieve associated
   *                   persons.
   * @return A list of persons associated with the provided list of households.
   */
  public List<Person> getPersonsByHouseholds(List<Household> households) {
    List<Person> persons = new ArrayList<>();
    for (Household household : households) {
      persons.addAll(getPersonsByHousehold(household));
    }
    return persons;
  }

  /**
   * Some javadoc.
   * 
   * Retrieves a list of persons associated with a specific household.
   *
   * @param household The household for which to retrieve associated persons.
   * @return A list of persons associated with the provided household.
   */
  public List<Person> getPersonsByHousehold(Household household) {
    List<Optional<Person>> personOptionals = personDao.findByIdHousehold(household.getId());
    List<Person> persons = new ArrayList<>();
    for (Optional<Person> personOptional : personOptionals) {
      personOptional.ifPresent(persons::add);
    }
    return persons;
  }

  /**
   * Some javadoc.
   * 
   * Retrieves a list of persons residing in a specific city.
   *
   * @param city The city for which to retrieve associated persons.
   * @return A list of persons residing in the provided city.
   */
  public List<Person> getPersonsByCity(String city) {
    List<Optional<Person>> personOptionals = personDao.findByCity(city);
    List<Person> persons = new ArrayList<>();
    for (Optional<Person> personOptional : personOptionals) {
      personOptional.ifPresent(persons::add);
    }
    return persons;
  }

  /**
   * Some javadoc.
   * 
   * Retrieves a person based on their first name and last name.
   *
   * @param firstName The first name of the person.
   * @param lastName  The last name of the person.
   * @return The person associated with the provided first name and last name, or
   *         null if not found.
   */
  public Person getPersonByFirstAndLastName(String firstName, String lastName) {
    return personDao.findByFirstNameAndLastName(firstName, lastName);
  }
}