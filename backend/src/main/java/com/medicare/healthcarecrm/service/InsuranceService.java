package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Insurance;
import com.medicare.healthcarecrm.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsuranceService {
    @Autowired
    private InsuranceRepository insuranceRepository;

    public Insurance createInsurance(Insurance insurance) {
        return insuranceRepository.save(insurance);
    }
}
