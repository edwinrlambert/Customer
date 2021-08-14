import javax.swing.*;
import java.sql.SQLException;

public class Java extends Savings implements  Compound_Interest{
    public Java(String custNum, String custName, String initDeposit, String numYears, String savingType) {
        super(custNum, custName, initDeposit, numYears, savingType);
    }

    @Override
    public void generateTable() throws SQLException, ClassNotFoundException {

        int numYears = Integer.parseInt(getNumYears());
        double initDeposit = Double.parseDouble(getInitDeposit());
        double startDeposit = 0.0;
        double interest = 0.0;
        double endDeposit = 0.0;
        String[][] compoundArray = new String[numYears][4];

        // Calculating the saving chart.
        int j = 0;
        while (j < numYears) {
            compoundArray[j][0] = j + ".0";

            startDeposit = initDeposit + interest;
            compoundArray[j][1] = startDeposit + ".0";

            interest += startDeposit * .10;
            compoundArray[j][2] = interest + ".0";

            endDeposit += initDeposit + interest;
            compoundArray[j][3] = endDeposit + ".0";
            ++j;
        }
        String[] cols = {"Year", "Starting", "Interest", "Ending Value"};
        CompoundInterest form = new CompoundInterest();
        form.setCompoundTable(compoundArray, cols);
    }
}
