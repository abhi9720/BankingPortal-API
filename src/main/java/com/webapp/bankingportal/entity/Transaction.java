package com.webapp.bankingportal.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private String transaction_type;
    private Date transaction_date;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}

	public Date getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(Date transaction_date) {
		this.transaction_date = transaction_date;
	}

	public Account getSourceAccount() {
		return sourceAccount;
	}

	public void setSourceAccount(Account sourceAccount) {
		this.sourceAccount = sourceAccount;
	}

	public Account getTargetAccount() {
		return targetAccount;
	}

	public void setTargetAccount(Account targetAccount) {
		this.targetAccount = targetAccount;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", amount=" + amount + ", transaction_type=" + transaction_type
				+ ", transaction_date=" + transaction_date + ", sourceAccount=" + sourceAccount + ", targetAccount="
				+ targetAccount + "]";
	} 
    
}
