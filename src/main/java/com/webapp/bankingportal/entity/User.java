package com.webapp.bankingportal.entity;

import javax.persistence.*;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    @Column(unique = true)
    private String email;
    private String address;
    private String phone_number;
    
    // Establishing a one-to-one relationship with the account
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	
	public Account getAccount() {
		return account;
	}
	// Convenience method to set the user's account
    public void setAccount(Account account) {
        this.account = account;
        account.setUser(this);
    }
	
    

	

  
}
