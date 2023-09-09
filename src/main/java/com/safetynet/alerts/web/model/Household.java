package com.safetynet.alerts.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Some javadoc.
 * 
 * This class represents a Households entity in the system.
 * It is used to link a Person with a Firestation by address.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Household {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String address;
}