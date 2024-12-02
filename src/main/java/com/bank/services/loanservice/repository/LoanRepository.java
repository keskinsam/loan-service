/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.repository;

import com.bank.services.loanservice.model.Customer;
import com.bank.services.loanservice.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LoanRepository extends MongoRepository<Loan, String> {

    @Query(value= "{customerId : ?0}")
    List<Loan> getLoansByCustomerId(String customerId);
}