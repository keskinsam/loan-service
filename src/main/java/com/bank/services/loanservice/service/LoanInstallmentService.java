/**
 * @author keskinsam@gmail.com
 */

package com.bank.services.loanservice.service;

import com.bank.services.loanservice.model.Loan;
import com.bank.services.loanservice.model.LoanInstallment;
import com.bank.services.loanservice.repository.LoanInstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;

    public void createInstallments(Loan loan) {
        int numberOfInstallments = loan.getNumberOfInstallments();
        double installmentAmount = getInstallmentAmount(loan, numberOfInstallments);
        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallment loanInstallment = LoanInstallment.builder()
                .loanId(loan.getId())
                .amount(getInstallmentAmount(loan, numberOfInstallments))
                .paidAmount(0.0d)
                .dueDate(setDueDateForInstallment(i))
                .build();

            loanInstallmentRepository.save(loanInstallment);
        }
    }

    private Date setDueDateForInstallment(int i) {
        return Date.from(LocalDate.now().plusMonths(i).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private double getInstallmentAmount(Loan loan, int numberOfInstallments) {
        return loan.getLoanAmount() / numberOfInstallments;
    }

    public List<LoanInstallment> getInstallments(String loanId) {
        return loanInstallmentRepository.getLoanInstallmentsByLoanId(loanId);
    }

    public double calculatePaymentForInstallment(LoanInstallment loanInstallment, Date paymentDate) {
        if (loanInstallment.getDueDate().after(paymentDate)) {
            return loanInstallment.getAmount() - (loanInstallment.getAmount() * 0.001 * calculateDaysBetweenTwoDates(loanInstallment.getDueDate(), paymentDate));
        } else if (loanInstallment.getDueDate().before(paymentDate)) {
            return loanInstallment.getAmount() + (loanInstallment.getAmount() * 0.001 * calculateDaysBetweenTwoDates(loanInstallment.getDueDate(), paymentDate));
        }

        return loanInstallment.getAmount();
    }

    private long calculateDaysBetweenTwoDates(Date paymentDay, Date dueDate) {
        long diffInMillies = Math.abs(paymentDay.getTime() - dueDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}