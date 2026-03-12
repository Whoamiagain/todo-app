package com.nattodo.NATToDo.repository;

import com.nattodo.NATToDo.model.Day;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayRepository extends MongoRepository<Day, String> {
}