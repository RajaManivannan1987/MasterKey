package com.imaginetventures.masterkey.modelClasses;

/**
 * Created by IM028 on 6/7/16.
 */
public class InterBranchDues {
    private String account_name;
    private String credit = "0";
    private String debit = "0";

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }
}
