package com.imaginetventures.masterkey.modelClasses;

/**
 * Created by IM028 on 4/4/16.
 */
public class EmployeePayRollData {
    private String employeeId;
    private String employeeName;
    private int todayWorking;
    private int daysWorked;
    private int basicSal;
    private int travelAllowance;
    private int refreshmentAllowance;
    private int bonus;
    private int loanPayment;
    private int advancePayment;
    private int total;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getTodayWorking() {
        return todayWorking;
    }

    public void setTodayWorking(int todayWorking) {
        this.todayWorking = todayWorking;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(int daysWorked) {
        this.daysWorked = daysWorked;
    }

    public int getBasicSal() {
        return basicSal;
    }

    public void setBasicSal(int basicSal) {
        this.basicSal = basicSal;
    }

    public int getTravelAllowance() {
        return travelAllowance;
    }

    public void setTravelAllowance(int travelAllowance) {
        this.travelAllowance = travelAllowance;
    }

    public int getRefreshmentAllowance() {
        return refreshmentAllowance;
    }

    public void setRefreshmentAllowance(int refreshmentAllowance) {
        this.refreshmentAllowance = refreshmentAllowance;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getLoanPayment() {
        return loanPayment;
    }

    public void setLoanPayment(int loanPayment) {
        this.loanPayment = loanPayment;
    }

    public int getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(int advancePayment) {
        this.advancePayment = advancePayment;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
