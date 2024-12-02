/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.repository;


import com.bank.services.loanservice.model.LoanInstallment;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LoanInstallmentRepository extends MongoRepository<LoanInstallment, String> {

    @Query(value= "{loanId : ?0}")
    List<LoanInstallment> getLoanInstallmentsByLoanId(String loanId);

    @Query(value= "{loanId : ?0, isPaid :  false}")
    List<LoanInstallment> getPayableLoanInstallmentsByLoanId(String loanId);

}
