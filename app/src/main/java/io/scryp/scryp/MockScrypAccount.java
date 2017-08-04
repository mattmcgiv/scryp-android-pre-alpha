package io.scryp.scryp;

/**
 * Created by mattmcgivney on 8/4/17.
 */

class MockScrypAccount {
    private static final MockScrypAccount ourInstance = new MockScrypAccount(20.00);
    private double balance;

    static MockScrypAccount getInstance() {
            return ourInstance;
    }

    private MockScrypAccount(double balance) {
        this.balance = balance;
    }

    protected double getBalance() {
        return this.balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }
}
