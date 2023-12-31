package com.safetynet.alerts.web.serialization.model;

import java.util.List;

import com.safetynet.alerts.web.model.Household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Some javadoc.
 * 
 * Model class representing an FloodAlertByHousehold object to contains all
 * floods object by address.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloodAlertByHousehold {
  private Household household;
  private List<FloodAlert> floods;
}