package com.safetynet.alerts.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.web.deserialization.model.FirestationDeserialization;
import com.safetynet.alerts.web.logging.EndpointsLogger;
import com.safetynet.alerts.web.model.Firestation;
import com.safetynet.alerts.web.model.Household;

/**
 * Some javadoc.
 * 
 * This service class provides operations related to the Firestation entity.
 * 
 * It interacts with the FirestationDao to perform CRUD operations on
 * Firestation objects.
 */
@Service
public class FirestationService {
  List<Firestation> firestations;

  @Autowired
  HouseHoldService houseHoldService;

  private EndpointsLogger log = new EndpointsLogger();

  public void setFirestations(List<Firestation> firestations) {
    this.firestations = firestations;
  }

  /**
   * Some javadoc.
   * 
   * Adds a new firestation or updates an existing one with the provided
   * information.
   *
   * @param firestationDeserialization The deserialized firestation data.
   * @param methodeName                The name of the method invoking the
   *                                   operation.
   * @return A ResponseEntity indicating the result of the operation.
   */
  public ResponseEntity<String> addFirestation(FirestationDeserialization fD,
      String methodeName) {

    Household household = houseHoldService.getHouseholdByAddress(fD.getAddress());
    Firestation firestation = getFirestationByStation(fD.getStation());

    if (household == null) {
      household = houseHoldService.saveHousehold(fD.getAddress());
    }
    if (firestation == null) {
      firestation = new Firestation();
      firestation.setId(firestations.size() + 1);
      firestation.setIdHouseholds(Arrays.asList(household.getId()));
      firestation.setStation(fD.getStation());
      firestations.add(firestation);
      return log.addedSuccessfully(methodeName);
    } else if (firestationGetIdHousehold(firestation, household)) {
      return log.ExistingMappingBetweenAddressAndFirestation(methodeName);
    } else {
      firestation.getIdHouseholds().add(household.getId());
      return log.addedSuccessfully(methodeName);
    }
  }

  /**
   * Some javadoc.
   * 
   * Updates the station number for a household at the specified address.
   *
   * @param firestationDeserialization The deserialized firestation data.
   * @param address                    The address of the household to update.
   * @param methodeName                The name of the method invoking the
   *                                   operation.
   * @return A ResponseEntity indicating the result of the operation.
   */
  public ResponseEntity<String> updateStationByAddress(FirestationDeserialization firestationDeserialization,
      String address, String methodeName) {
    Household household = houseHoldService.getHouseholdByAddress(address);
    if (household == null) {
      return log.argumentHasNoMatch(methodeName);
    } else {
      Integer idHousehold = household.getId();
      List<Firestation> matchingFirestations = getFirestationsByHousehold(household);
      String station = firestationDeserialization.getStation();
      for (Firestation firestation : matchingFirestations) {
        if (!firestation.getStation().equals(station)) {
          if (firestation.getIdHouseholds().size() == 1) {
            firestations.remove(firestation);
          } else {
          firestation.getIdHouseholds().remove(Integer.valueOf(idHousehold));
          firestations.set(firestation.getId() - 1, firestation);
          }
        }
      }
      Firestation firestation = getFirestationByStation(station);
      if (firestation == null) {
        firestation = new Firestation();
        firestation.setStation(station);
        firestation.setIdHouseholds(Arrays.asList(idHousehold));
        firestation.setId(firestations.size() + 1);
        firestations.add(firestation);
        return log.updatedSuccessfully(methodeName);
      } else {
        List<Integer> idHouseholds = new ArrayList<>();
        for (Integer id : firestation.getIdHouseholds()) {
          idHouseholds.add(id);
        }
        idHouseholds.add(idHousehold);
        firestation.setIdHouseholds(idHouseholds);
        firestations.set(firestation.getId() - 1, firestation);
        return log.updatedSuccessfully(methodeName);
      }
    }
  }

  /**
   * 
   * Deletes station numbers associated with a specific address.
   *
   * @param address     The address for which to delete station numbers.
   * @param methodeName The name of the method invoking the operation.
   * @return A ResponseEntity indicating the result of the operation.
   */
  public ResponseEntity<String> deleteStationAtThisAddress(String address, String methodeName) {

    // Get all firestations by an address :
    Household household = houseHoldService.getHouseholdByAddress(address);
    List<Firestation> firestationsMatching = getFirestationsByHousehold(household);
    // Check if any firestation match with this address :
    if (firestationsMatching.isEmpty()) {
      return log.argumentHasNoMatch(methodeName);
    } else {
      // For each firestation check if they have at least one address or delete it :
      for (Firestation firestation : firestationsMatching) {
        if (firestation.getIdHouseholds().size() == 1) {
          firestations.remove(firestation);
        } else {
          firestation.getIdHouseholds().remove(Integer.valueOf(household.getId()));
        }
      }
      return log.deletedSuccessfully(methodeName);
    }
  }

  /**
   * Some javadoc.
   * 
   * Deletes a firestation object based on its firestation number.
   *
   * @param station     The firestation number to delete.
   * @param methodeName The name of the method invoking the deletion.
   * @return A ResponseEntity indicating the result of the deletion operation.
   */
  public ResponseEntity<String> deleteFirestationByStation(String station, String methodeName) {
    // Check if the firestation exists :
    Firestation firestation = getFirestationByStation(station);
    if (firestation != null) {
      firestations.remove(firestation);
      return log.deletedSuccessfully(methodeName);
    } else {
      return log.argumentHasNoMatch(methodeName);
    }
  }

  /**
   * Some javadoc.
   * 
   * Get a firestation object by his firestation number.
   *
   * @param station The firestation number to link at this objects.
   */
  public Firestation getFirestationByStation(String station) {
    return firestations.stream()
        .filter(firestation -> firestation.getStation().equals(station)).findFirst().orElse(null);
  }

  /**
   * Some javadoc.
   * 
   * Get a firestation object list by id
   *
   * @param id An list of firestation id.
   */
  public List<Firestation> getFirestationsByIdList(List<Integer> ids) {
    return firestations.stream()
        .filter(firestation -> ids.contains(firestation.getId()))
        .collect(Collectors.toList());
  }

  /**
   * Some javadoc.
   * 
   * Retrieves a list of firestation objects associated with a specific household.
   *
   * @param household The household for which to retrieve associated firestation
   *                  objects.
   * @return A list of firestation objects associated with the specified
   *         household.
   */
  public List<Firestation> getFirestationsByHousehold(Household household) {
    return firestations.stream().filter(firestation -> firestation.getIdHouseholds().contains(household.getId()))
        .collect(Collectors.toList());
  }

  /**
   * Some javadoc.
   * 
   * Checks if a firestation object has an associated household with a given ID.
   *
   * @param firestation The firestation object to check.
   * @param household   The household for which to check association.
   * @return True if the firestation is associated with the specified household,
   *         otherwise False.
   */
  public Boolean firestationGetIdHousehold(Firestation firestation, Household household) {
    for (Integer idHousehold : firestation.getIdHouseholds()) {
      if (idHousehold == household.getId()) {
        return true;
      }
    }
    return false;
  }

  public List<Firestation> getAllFirestations() {
    return firestations;
  }

}