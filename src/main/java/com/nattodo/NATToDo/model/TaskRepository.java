package com.nattodo.NATToDo.model; 

import com.nattodo.NATToDo.model.Day; 
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Day, String> {
    Optional<Day> findByUserId(String userId);
}