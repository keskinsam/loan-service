/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice;

import com.bank.services.loanservice.model.Customer;
import com.bank.services.loanservice.model.CustomerRequest;
import com.bank.services.loanservice.model.Loan;
import com.bank.services.loanservice.model.LoanRequest;
import com.bank.services.loanservice.model.LoanResponse;
import com.bank.services.loanservice.repository.LoanRepository;
import com.bank.services.loanservice.service.CustomerService;
import com.bank.services.loanservice.service.LoanInstallmentService;
import com.bank.services.loanservice.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private CustomerService customerService;

    @Mock
    private LoanInstallmentService loanInstallmentService;

    @Mock
    private LoanRepository loanRepository;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId("customer123");
        customer.setUsedCreditLimit(10000);
    }

    @Test
    public void testCreateLoan_Success() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCustomer(createCustomerRequest(customer));
        loanRequest.setAmount(5000);
        loanRequest.setNumberOfInstallments(12);
        loanRequest.setInterestRate(0.2);

        when(customerService.setCustomerForLoan(loanRequest)).thenReturn(customer);
        when(customerService.customerAvailableLimit(customer)).thenReturn(10000.0);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = loanService.createLoan(loanRequest);

        assertTrue(response.isSuccess());
        verify(loanRepository).save(any(Loan.class));
        verify(customerService).updateCustomerLimits(eq(customer), any(Loan.class));
        verify(loanInstallmentService).createInstallments(any(Loan.class));
    }

    private CustomerRequest createCustomerRequest(Customer customer) {
        return CustomerRequest.builder()
            .id(customer.getId())
            .name(customer.getName())
            .lastName(customer.getLastName())
            .creditLimit(customer.getCreditLimit())
            .usedCreditLimit(customer.getUsedCreditLimit())
            .build();
    }

    @Test
    public void testCreateLoan_Failure_ExceedsLimit() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCustomer(createCustomerRequest(customer));
        loanRequest.setAmount(15000);
        loanRequest.setNumberOfInstallments(12);
        loanRequest.setInterestRate(0.2);

        when(customerService.setCustomerForLoan(loanRequest)).thenReturn(customer);
        when(customerService.customerAvailableLimit(customer)).thenReturn(10000.0);

        LoanResponse response = loanService.createLoan(loanRequest);

        assertFalse(response.isSuccess());
        verify(loanRepository, never()).save(any());
    }

    @Test
    public void testCheckNumberOfInstallments_Valid() {
        assertTrue(loanService.checkNumberOfInstallments(6));
        assertTrue(loanService.checkNumberOfInstallments(9));
        assertTrue(loanService.checkNumberOfInstallments(12));
        assertTrue(loanService.checkNumberOfInstallments(24));
    }

    @Test
    public void testCheckNumberOfInstallments_Invalid() {
        assertFalse(loanService.checkNumberOfInstallments(5));
        assertFalse(loanService.checkNumberOfInstallments(10));
    }

}