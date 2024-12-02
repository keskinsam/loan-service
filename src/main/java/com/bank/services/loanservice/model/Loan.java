/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    @Id
    private String id;
    private String customerId;
    private double loanAmount;
    private int numberOfInstallments;
    private Date createDate;
    private boolean isPaid;
}