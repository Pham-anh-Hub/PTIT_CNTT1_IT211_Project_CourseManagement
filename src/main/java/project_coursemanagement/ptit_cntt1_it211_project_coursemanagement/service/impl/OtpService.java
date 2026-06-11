package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}