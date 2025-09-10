package com.example.banking_api_challenge.dataInitializer;

import com.example.banking_api_challenge.model.Customer;
import com.example.banking_api_challenge.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {


    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args){
        if (customerRepository.count() == 0) {
            customerRepository.saveAll(List.of(
                    new Customer(null, "Arisha Barron", null),
                    new Customer(null, "Branden Gibson", null),
                    new Customer(null, "Rhonda Church", null),
                    new Customer(null, "Georgina Hazel", null)
            ));
        }
    }
}
