/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.service;

import com.bank.services.loanservice.model.Customer;
import com.bank.services.loanservice.model.Loan;
import com.bank.services.loanservice.model.LoanInstallment;
import com.bank.services.loanservice.model.LoanRequest;
import com.bank.services.loanservice.model.LoanResponse;
import com.bank.services.loanservice.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LoanService {

    private final CustomerService customerService;
    private final LoanInstallmentService loanInstallmentService;

    private final LoanRepository loanRepository;

    public LoanService(LoanInstallmentService loanInstallmentService, LoanRepository loanRepository, CustomerService customerService) {
        this.loanInstallmentService = loanInstallmentService;
        this.loanRepository = loanRepository;
        this.customerService = customerService;
    }

    public LoanResponse createLoan(LoanRequest loanRequest) {
        Customer customer = customerService.setCustomerForLoan(loanRequest);
        if(checkCustomerLimit(customer, loanRequest.getAmount())
                            && checkNumberOfInstallments(loanRequest.getNumberOfInstallments()) && checkInterestRate(loanRequest.getInterestRate())) {
            Loan loan = Loan.builder()
                            .customerId(loanRequest.getCustomer().getId())
                            .loanAmount(calculateLoanAmount(loanRequest))
                            .numberOfInstallments(loanRequest.getNumberOfInstallments())
                            .createDate(new Date())
                            .isPaid(Boolean.FALSE)
                            .build();

            loanRepository.save(loan);
            customerService.updateCustomerLimits(customer, loan);
            loanInstallmentService.createInstallments(loan);
            log.info("Created loan {}", loan);
            return LoanResponse.builder().loanId(loan.getId()).success(Boolean.TRUE).build();
        } else {
            log.error("Error happened while creating loan");
            return LoanResponse.builder().success(Boolean.FALSE).build();
        }
    }

    public List<Loan> getLoansByCustomer(String customerId) {
        return loanRepository.getLoansByCustomerId(customerId);
    }


    private double calculateLoanAmount(LoanRequest loanRequest) {
        return loanRequest.getAmount() * (1 + loanRequest.getInterestRate());
    }

    public List<Loan> getAllLoans() {
        log.info("Get all loans");
        return loanRepository.findAll();
    }

    public List<LoanInstallment> listInstallments() {
        return null;
    }

    private boolean checkCustomerLimit(Customer customer, double amount) {
        return customerService.customerAvailableLimit(customer) > amount;
    }

    public boolean checkNumberOfInstallments(int numberOfInstallments) {
        return (numberOfInstallments == 6 || numberOfInstallments == 9 || numberOfInstallments == 12 || numberOfInstallments == 24);
    }

    private boolean checkInterestRate(double interestRate) {
        return (interestRate >= 0.1 && interestRate <= 0.5);
    }

}