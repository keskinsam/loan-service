/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanResponse {

    private boolean success;
    private String loanId;
}