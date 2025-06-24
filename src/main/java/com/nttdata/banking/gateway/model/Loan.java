package com.nttdata.banking.gateway.model;

import lombok.*;

/**
 * Class Loan.
 * Client microservice class Loan.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Loan {

    private String idLoan;
    private Client client;
    private Integer loanNumber;
    private String loanType;
    private Double loanAmount;
    private String currency;
    private Integer numberQuotas;
    private String status;
    private Double debtBalance;
}
