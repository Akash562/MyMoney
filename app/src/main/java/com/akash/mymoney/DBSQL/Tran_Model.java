package com.akash.mymoney.DBSQL;

public class Tran_Model {

    private String Name;
    private String Rate;
    private String Amount;
    private String GDate;
    private int id;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getGDate() {
        return GDate;
    }

    public void setGDate(String GDate) {
        this.GDate = GDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public Tran_Model(String Name, String Rate, String Amount, String GDate) {
        this.Name = Name;
        this.Rate = Rate;
        this.Amount = Amount;
        this.GDate = GDate;
    }

}
