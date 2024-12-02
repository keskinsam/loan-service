/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.repository;

import com.bank.services.loanservice.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CustomerRepository extends MongoRepository<Customer, String> {

}