/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.service;

import com.bank.services.loanservice.model.Customer;
import com.bank.services.loanservice.model.CustomerRequest;
import com.bank.services.loanservice.model.Loan;
import com.bank.services.loanservice.model.LoanRequest;
import com.bank.services.loanservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private Optional<Customer> getCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    private Customer createCustomer(CustomerRequest customerRequest) {
        Customer customer = Customer.builder()
            .name(customerRequest.getName())
            .lastName(customerRequest.getLastName())
            .creditLimit(customerRequest.getCreditLimit())
            .usedCreditLimit(customerRequest.getUsedCreditLimit())
            .build();

        return customerRepository.save(customer);
    }

    public Customer setCustomerForLoan(LoanRequest loanRequest) {
        Optional<Customer> customer = getCustomerById(loanRequest.getCustomer().getId());
        return customer.orElseGet(() -> createCustomer(loanRequest.getCustomer()));
    }

    public double customerAvailableLimit(Customer customer) {
        return customer.getCreditLimit() - customer.getUsedCreditLimit();
    }

    public void updateCustomerLimits(Customer customer, Loan loan) {
        customer.setUsedCreditLimit(loan.getLoanAmount());
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}