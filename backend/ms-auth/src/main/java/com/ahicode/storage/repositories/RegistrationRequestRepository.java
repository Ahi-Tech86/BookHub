package com.ahicode.storage.repositories;

import com.ahicode.storage.entities.RegistrationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegistrationRequestRepository extends MongoRepository<RegistrationRequest, String> {
    Optional<RegistrationRequest> findByEmail(String email);
}
