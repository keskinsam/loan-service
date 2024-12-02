/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanInstallment implements Comparable<LoanInstallment> {

    private String id;
    private String loanId;
    private double amount;
    private double paidAmount;
    private Date dueDate;
    private Date paymentDate;
    private boolean isPaid;

    @Override
    public int compareTo(LoanInstallment loanInstallment) {
        return this.dueDate.compareTo(loanInstallment.getDueDate());
    }
}