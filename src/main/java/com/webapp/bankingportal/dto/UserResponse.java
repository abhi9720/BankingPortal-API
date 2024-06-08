package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.User;

public class UserResponse {

    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    private String accountType;

    public UserResponse() {
    }

    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.accountNumber = user.getAccount().getAccountNumber();
        this.ifscCode = user.getAccount().getIfscCode();
        this.branch = user.getAccount().getBranch();
        this.accountType = user.getAccount().getAccountType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "{ \"name\": \"" + name
                + "\", \"email\": \"" + email
                + "\", \"address\": \"" + address
                + "\", \"phoneNumber\": \"" + phoneNumber
                + "\", \"accountNumber\": \"" + accountNumber
                + "\", \"ifscCode\": \"" + ifscCode
                + "\", \"branch\": \"" + branch
                + "\", \"accountType\": \"" + accountType + "\" }";
    }
}
