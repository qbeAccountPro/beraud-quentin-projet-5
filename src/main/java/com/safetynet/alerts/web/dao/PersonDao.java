package com.safetynet.alerts.web.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.web.model.Person;

@Repository
public interface PersonDao extends JpaRepository<Person, Integer> {
    Person findById(int id);
}