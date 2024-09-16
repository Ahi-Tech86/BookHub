package com.ahicode.storage.entities;

import com.ahicode.api.dtos.RegistrationRequestDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "registration_requests")
public class RegistrationRequest {
    @Id
    private String id;
    private String email;
    private RegistrationRequestDto dto;
    private String activationCode;

    @Indexed(expireAfter = "1200s")
    private Instant createAt;
}
