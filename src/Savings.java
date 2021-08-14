public class Savings {

    private String custNum;
    private String custName;
    private String initDeposit;
    private String numYears;
    private String savingType;

    public Savings(String custNum, String custName, String initDeposit, String numYears, String savingType) {
        this.custNum = custNum;
        this.custName = custName;
        this.initDeposit = initDeposit;
        this.numYears = numYears;
        this.savingType = savingType;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getInitDeposit() {
        return initDeposit;
    }

    public void setInitDeposit(String initDeposit) {
        this.initDeposit = initDeposit;
    }

    public String getNumYears() {
        return numYears;
    }

    public void setNumYears(String numYears) {
        this.numYears = numYears;
    }

    public String getSavingType() {
        return savingType;
    }

    public void setSavingType(String savingType) {
        this.savingType = savingType;
    }
}
