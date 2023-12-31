package com.safetynet.alerts.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.safetynet.alerts.web.deserialization.model.PersonDeserialization;
import com.safetynet.alerts.web.httpResponse.ResponseBuilder;
import com.safetynet.alerts.web.model.Household;
import com.safetynet.alerts.web.model.Person;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

  @Mock
  private HouseHoldService houseHoldService;

  @Mock
  private MedicalRecordService medicalRecordService;

  @Mock
  private ResponseBuilder response = new ResponseBuilder();

  @InjectMocks
  private PersonService personService;

  // Example of addresses or stations :
  private String ADDRESS_1, CITY_1;
  private String FIRSTNAME_1, LASTNAME_1;
  private String ZIP_1, PHONE_1, EMAIL_1;

  // Example of converted data :
  private List<Household> households;
  private Household household_1;
  private List<Person> persons;
  private Person person_1;
  private PersonDeserialization personDeserialization;

  @BeforeEach
  public void setUp() {
    // Set Adrresses :
    ADDRESS_1 = "ici";
    CITY_1 = "Lyon";
    FIRSTNAME_1 = "Quentin";
    LASTNAME_1 = "Beraud";
    ZIP_1 = "69000";
    PHONE_1 = "000";
    EMAIL_1 = "qbe@yahoo.com";

    // Set Households example data :
    households = new ArrayList<>();
    household_1 = new Household(1, ADDRESS_1);
    households.add(household_1);
    houseHoldService.setHouseholds(households);

    // Set Persons example data :
    persons = new ArrayList<>();
    person_1 = new Person(1, household_1.getId(), FIRSTNAME_1, LASTNAME_1, CITY_1, ZIP_1, PHONE_1, EMAIL_1);
    persons.add(person_1);
    personService.setPersons(persons);

    personDeserialization = new PersonDeserialization(0, FIRSTNAME_1, LASTNAME_1, ADDRESS_1, CITY_1, ZIP_1, PHONE_1,
        EMAIL_1);
  }

  @Test
    void testAddPersonSuccessfully() {
        when(houseHoldService.getHouseholdByAddress(ADDRESS_1)).thenReturn(household_1);
        personService.deleteByFirstAndLastName(FIRSTNAME_1, LASTNAME_1, "methodITtest");

        ResponseEntity<String> result = personService.addPerson(personDeserialization, "addPerson");
        ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.CREATED).body("Content added successfully.");

        assertEquals(excepted, result);
    }

  @Test
    void testAddPersonSuccessfullyWithInexistenteHousehold() {
        when(houseHoldService.getHouseholdByAddress(ADDRESS_1)).thenReturn(null);
        when(houseHoldService.saveHousehold(ADDRESS_1)).thenReturn(household_1);
        personService.deleteByFirstAndLastName(FIRSTNAME_1, LASTNAME_1, "methodITtest");


        ResponseEntity<String> result = personService.addPerson(personDeserialization, "addPerson");
        ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.CREATED).body("Content added successfully.");

        assertEquals(excepted, result);
    }

  @Test
  void testAddPersonWithExistingPerson() {

    ResponseEntity<String> result = personService.addPerson(personDeserialization, "addPerson");
    ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.CONFLICT).body("Mapping already exist.");

    assertEquals(excepted, result);
  }

  @Test
  void testDeleteByFirstNameAndLastNameWithInexistingPerson() {
    ResponseEntity<String> result = personService.deleteByFirstAndLastName(FIRSTNAME_1, "Factof",
        "deleteByFirstAndLastName");
    ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Argument has no match.");

    assertEquals(excepted, result);
  }

  @Test
  void testDeleteByFirstNameAndLastNameSuccessfully() {

    ResponseEntity<String> result = personService.deleteByFirstAndLastName(FIRSTNAME_1, LASTNAME_1,
        "deleteByFirstAndLastName");
    ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.OK).body("Content deleted successfully.");

    assertEquals(excepted, result);
  }

  @Test
  void testGetAllPersons() {
    personService.getAllPersons();
  }

  @Test
  void testGetPersonByFirstAndLastName() {
    personService.getPersonByFirstAndLastName(FIRSTNAME_1, LASTNAME_1);
  }

  @Test
  void testGetPersonById() {
    Person result = personService.getPersonById(person_1.getId());
    assertEquals(person_1, result);
  }

  @Test
  void testGetPersonsByCity() {
    List<Optional<Person>> optionalPersons = new ArrayList<>();
    optionalPersons.add(Optional.of(person_1));

    List<Person> result = personService.getPersonsByCity(CITY_1);

    assertEquals(persons, result);
  }

  @Test
  void testGetPersonsByHousehold() {
    List<Optional<Person>> optionalPersons = new ArrayList<>();
    optionalPersons.add(Optional.of(person_1));

    List<Person> result = personService.getPersonsByHousehold(household_1);

    assertEquals(persons, result);
  }

  @Test
  void testGetPersonsByHouseholds() {
    List<Optional<Person>> optionalPersons = new ArrayList<>();
    optionalPersons.add(Optional.of(person_1));

    List<Person> result = personService.getPersonsByHouseholds(households);

    assertEquals(persons, result);
  }

  @Test
  void testUpdateByFirstNameAndLastNameWithInexistingPerson() {
    ResponseEntity<String> result = personService.updateByFirstAndLastName(FIRSTNAME_1, "Wrong",
        personDeserialization, "updateByFirstAndLastName");
    ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Argument has no match.");

    assertEquals(excepted, result);
  }

  @Test
    void testUpdateByFirstNameAndLastNameSuccessfully() {
        when(houseHoldService.getHouseholdByAddress(ADDRESS_1)).thenReturn(null);
        when(houseHoldService.saveHousehold(ADDRESS_1)).thenReturn(household_1);

        ResponseEntity<String> result = personService.updateByFirstAndLastName(FIRSTNAME_1, LASTNAME_1,personDeserialization, "updateByFirstAndLastName");
        ResponseEntity<String> excepted = ResponseEntity.status(HttpStatus.OK).body("Content updated successfully.");

        assertEquals(excepted, result);
    }
}
