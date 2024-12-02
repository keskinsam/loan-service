/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.service;

import com.bank.services.loanservice.model.Loan;
import com.bank.services.loanservice.model.LoanInstallment;
import com.bank.services.loanservice.model.PaymentRequest;
import com.bank.services.loanservice.model.PaymentResponse;
import com.bank.services.loanservice.repository.LoanInstallmentRepository;
import com.bank.services.loanservice.repository.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PaymentService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanInstallmentService loanInstallmentService;

    public PaymentService(LoanRepository loanRepository, LoanInstallmentRepository loanInstallmentRepository, LoanInstallmentService loanInstallmentService) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanInstallmentService = loanInstallmentService;
    }

    public PaymentResponse payment(PaymentRequest paymentRequest) {
        Optional<Loan> loan = loanRepository.findById(paymentRequest.getLoanId());
        double amount = paymentRequest.getAmount();
        int paidInstallations = 0;
        if (loan.isPresent()) {
            List<LoanInstallment> payableLoanInstallments = new java.util.ArrayList<>(loanInstallmentRepository.getPayableLoanInstallmentsByLoanId(loan.get().getId())
                                                                                                               .stream()
                                                                                                               .filter(loanInstallment -> checkInstallmentsDueDate(loanInstallment.getDueDate()))
                                                                                                               .toList());
            Collections.sort(payableLoanInstallments);
            for (LoanInstallment loanInstallment : payableLoanInstallments) {
                log.info("Loans due date : {}", loanInstallment.getDueDate());
                double neededAmount = getNeededAmountForPayment(loanInstallment);
                if (amount > neededAmount) {
                    loanInstallment.setPaid(Boolean.TRUE);
                    loanInstallment.setPaidAmount(neededAmount);
                    loanInstallment.setPaymentDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    loanInstallmentRepository.save(loanInstallment);
                    amount -= neededAmount;
                    paidInstallations++;
                } else {
                    break;
                }
            }

            boolean isLoanPaymentsCompleted = loanInstallmentRepository.getLoanInstallmentsByLoanId(paymentRequest.getLoanId()).stream().allMatch(LoanInstallment::isPaid);
            if (isLoanPaymentsCompleted) {
                loan.get().setPaid(Boolean.TRUE);
                loanRepository.save(loan.get());
            }

            return PaymentResponse.builder()
                                  .spentAmount(paymentRequest.getAmount() - amount)
                                  .paidInstallations(paidInstallations)
                                  .loanCompleted(loanInstallmentRepository.getLoanInstallmentsByLoanId(paymentRequest.getLoanId())
                                                                                                     .stream()
                                                                                                     .allMatch(LoanInstallment::isPaid))
                                  .build();

        } else {
            log.error("Loan is not fount with id {}", paymentRequest.getLoanId());
        }

        return null;
    }

    private boolean checkInstallmentsDueDate(Date dueDate) {
        Date threeMonthsRange = Date.from(LocalDate.now().plusMonths(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return threeMonthsRange.after(dueDate);
    }

    private double getNeededAmountForPayment(LoanInstallment loanInstallment) {
        Date paymentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return loanInstallmentService.calculatePaymentForInstallment(loanInstallment, paymentDate);
    }
}