/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequest {

    private CustomerRequest customer;
    private double amount;
    private double interestRate;
    private int numberOfInstallments;
}