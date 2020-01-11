package com.spectred.service;

import com.spectred.beans.Bean;

@Bean
public class SalaryService {
    public Integer getSalary(Integer exp) {
        return exp * 10;
    }
}
