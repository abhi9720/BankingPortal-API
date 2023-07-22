package com.webapp.bankingportal.entity;

import javax.persistence.*;

@Entity
public class Account {
   
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true)
	    private String accountNumber;
	    private double balance;
	    private String account_type="Saving";
	    private String branch="Bhind";
	    private String IFSC_code="BHI001";
	    private String Pin;
	    private String accountstatus;
	    

	    @OneToOne
	    @JoinColumn(name = "user_id")
	    private User user;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		

		public String getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public double getBalance() {
			return balance;
		}

		public void setBalance(double balance) {
			this.balance = balance;
		}

		public String getAccount_type() {
			return account_type;
		}

		public void setAccount_type(String account_type) {
			this.account_type = account_type;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public String getIFSC_code() {
			return IFSC_code;
		}

		public void setIFSC_code(String iFSC_code) {
			IFSC_code = iFSC_code;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		@Override
		public String toString() {
			return "Account [id=" + id + ", accountNumber=" + accountNumber + ", balance=" + balance + ", account_type="
					+ account_type + ", branch=" + branch + ", IFSC_code=" + IFSC_code + ", user=" + user + "]";
		}

		public String getPin() {
			return Pin;
		}

		public void setPin(String pin) {
			this.Pin = pin;
		}
	    
	    
    
}
