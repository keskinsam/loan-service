/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.controller;

import com.bank.services.loanservice.model.Customer;
import com.bank.services.loanservice.model.Loan;
import com.bank.services.loanservice.model.LoanInstallment;
import com.bank.services.loanservice.model.LoanRequest;
import com.bank.services.loanservice.model.LoanResponse;
import com.bank.services.loanservice.model.PaymentRequest;
import com.bank.services.loanservice.model.PaymentResponse;
import com.bank.services.loanservice.service.CustomerService;
import com.bank.services.loanservice.service.LoanInstallmentService;
import com.bank.services.loanservice.service.LoanService;
import com.bank.services.loanservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanInstallmentService loanInstallmentService;
    private final PaymentService paymentService;
    private final CustomerService customerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@RequestBody LoanRequest loanRequest) {
        return loanService.createLoan(loanRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Loan> listLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/{customerId}/loans")
    @ResponseStatus(HttpStatus.OK)
    public List<Loan> getCustomerLoans(@PathVariable String customerId) {
        return loanService.getLoansByCustomer(customerId);
    }

    @GetMapping("/{loanId}/installments")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanInstallment> getInstallmentsForLoan(@PathVariable String loanId) {
        return loanInstallmentService.getInstallments(loanId);
    }

    @PostMapping("/payment")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse makePayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.payment(paymentRequest);
    }

    @GetMapping("/customers")
    @ResponseStatus(HttpStatus.OK)
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }
}