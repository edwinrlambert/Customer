import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
 * Created by JFormDesigner on Sat Aug 14 02:59:12 IST 2021
 */



/**
 * @author unknown
 */
public class CompoundInterest extends JFrame {

    //Calling the Connection to the JBDC Class.
    JDBConnection connection = new JDBConnection();
    Connection conObj = connection.connect();

    // Creating an arraylist to hold data.
    ArrayList<Savings> savingList = new ArrayList<>();

    public CompoundInterest() throws SQLException, ClassNotFoundException {
        initComponents();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        CompoundInterest form = new CompoundInterest();
        form.setDefaultCloseOperation(EXIT_ON_CLOSE);
        form.updateTable();
        form.setVisible(true);
    }

    public void updateTable() throws SQLException {
        // Selecting all data from the database.
        String selectQuery = "select * from savingstable";
        PreparedStatement query = conObj.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet selectResult = query.executeQuery();

        // Checking if values are present in the database.
        selectResult.last();
        int numRows = selectResult.getRow();
        selectResult.beforeFirst();

        // Creating size as per the row count.
        String[][] array = new String[0][];
        if (numRows > 0) {
            array = new String[numRows][5];
        }

        // Retrieving values from the database.
        int j = 0;
        while(selectResult.next()) {
            array[j][0] = selectResult.getString("custno");
            array[j][1] = selectResult.getString("custname");
            array[j][2] = selectResult.getString("cdep");
            array[j][3] = selectResult.getString("nyears");
            array[j][4] = selectResult.getString("savtype");
            ++j;
        }

        // Creating the table model.
        String[] cols = {"Number", "Name", "Deposit", "Years", "Type of Savings"};
        DefaultTableModel model = new DefaultTableModel(array, cols);
        tblData.setModel(model);

    }

    private void btnAddActionPerformed(ActionEvent e) throws SQLException {
        int custNum = 0;
        String custName = "";
        double initDeposit = 0.0;
        int numYears = 0;
        String savingType = "";
        boolean flag = true;

        // Validating and getting the customer number.
        if(!txtCustNum.getText().equals("")) {
            try {
                custNum = Integer.parseInt(txtCustNum.getText());
            } catch (NumberFormatException numEx) {
                JOptionPane.showMessageDialog(null, "Customer Number should be a number.");
                flag = false;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Customer Number shouldn't be empty.");
            flag = false;
        }

        // Validating and getting the customer name.
        if (flag) {
            custName = txtCustName.getText();
            if (custName.equals("")) {
                JOptionPane.showMessageDialog(null, "Customer name should not be empty.");
                flag = false;
            }
        }

        // Validating and getting the initial deposit.
        if (flag) {
            if(!txtInitDeposit.getText().equals("")) {
                try {
                    initDeposit = Double.parseDouble(txtInitDeposit.getText());
                } catch (NumberFormatException numEx) {
                    JOptionPane.showMessageDialog(null, "Initial Deposit should be a number.");
                    flag = false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Initial Deposit shouldn't be empty.");
                flag = false;
            }
        }

        // Validating and getting the number of years.
        if (flag) {
            if(!txtNumYears.getText().equals("")) {
                try {
                    numYears = Integer.parseInt(txtNumYears.getText());
                } catch (NumberFormatException numEx) {
                    JOptionPane.showMessageDialog(null, "Number of Years should be a number.");
                    flag = false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Number of Years shouldn't be empty.");
                flag = false;
            }
        }

        // Getting value from savings type.
        if (flag) {
            savingType = cbSavingType.getSelectedItem().toString();
        }

        if (flag) {
            // Saving the list in the arraylist.
            savingList.add(new Savings(String.valueOf(custNum), custName, String.valueOf(initDeposit), String.valueOf(numYears), savingType));

            // Getting the contents from the database.
            String selectDuplicate = "select * from savingstable where custno = ?";
            PreparedStatement query = conObj.prepareStatement(selectDuplicate);
            query.setString(1, savingList.get(savingList.size() - 1).getCustNum());
            ResultSet rs = query.executeQuery();

            // Checking for duplicate records.
            if (rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "The record is existing.");
                txtCustNum.setText("");
                txtCustName.setText("");
                txtInitDeposit.setText("");
                txtNumYears.setText("");
                return;
            }

            // Inserting data into the database.
            String insertQuery = "insert into savingstable values (?, ?, ?, ?, ?)";
            query = conObj.prepareStatement(insertQuery);
            query.setString(1, savingList.get(savingList.size() - 1).getCustNum());
            query.setString(2, savingList.get(savingList.size() - 1).getCustName());
            query.setString(3, savingList.get(savingList.size() - 1).getInitDeposit());
            query.setString(4, savingList.get(savingList.size() - 1).getNumYears());
            query.setString(5, savingList.get(savingList.size() - 1).getSavingType());
            query.executeUpdate();

            JOptionPane.showMessageDialog(null, "Record added.");
            updateTable();
            clearTextFields();
        }
    }

    private void btnEditActionPerformed(ActionEvent e) throws SQLException {
        DefaultTableModel df = (DefaultTableModel) tblData.getModel();
        int index = tblData.getSelectedRow();

        String custNum = txtCustNum.getText();
        String custName = txtCustName.getText();
        String initDeposit = txtInitDeposit.getText();
        String numYears = txtNumYears.getText();
        String savingType = cbSavingType.getSelectedItem().toString();

        // Checking if the value is present and removing it.
        String oldValue = df.getValueAt(index, 0).toString();
        for (int i = 0; i < savingList.size(); ++i) {
            if (oldValue.equals(savingList.get(i).getCustNum())) {
                savingList.remove(i);
            }
        }

        savingList.add(new Savings(custNum, custName, initDeposit, numYears, savingType));

        // Updating the new data.
        String updateQuery = "update savingstable set custno = ?, custname = ?, cdep = ?, nyears = ?, savtype = ? where custno = ?";
        PreparedStatement query = conObj.prepareStatement(updateQuery);

        query.setString(1, savingList.get(savingList.size() - 1).getCustNum());
        query.setString(2, savingList.get(savingList.size() - 1).getCustName());
        query.setString(3, savingList.get(savingList.size() - 1).getInitDeposit());
        query.setString(4, savingList.get(savingList.size() - 1).getNumYears());
        query.setString(5, savingList.get(savingList.size() - 1).getSavingType());
        query.setString(6, oldValue);
        query.executeUpdate();

        JOptionPane.showMessageDialog(null,"Record edited.");

        updateTable();
        clearTextFields();
    }

    private void btnDeleteActionPerformed(ActionEvent e) throws SQLException {

        // Ask user if they want to delete the record.
        int reply = JOptionPane.showConfirmDialog(null,
                "Do you really want to delete this record?", "Delete", JOptionPane.YES_NO_OPTION);
        // Delete the record if the user selects YES.
        if (reply == JOptionPane.YES_OPTION) {
            String custNum = txtCustNum.getText();

            String deleteQuery = "delete from savingstable where custno = ?";
            PreparedStatement query = conObj.prepareStatement(deleteQuery);

            query.setString(1, custNum);
            query.executeUpdate();
            updateTable();
            clearTextFields();
        }
    }

    // Method to clear the textbox.
    public void clearTextFields() {
        txtCustNum.setText("");
        txtCustName.setText("");
        txtInitDeposit.setText("");
        txtNumYears.setText("");
    }

    private void tblDataMouseClicked(MouseEvent e) throws SQLException, ClassNotFoundException {
        DefaultTableModel df = (DefaultTableModel) tblData.getModel();
        int index = tblData.getSelectedRow();

        // Getting the contents to the textfields.
        txtCustNum.setText(df.getValueAt(index, 0).toString());
        txtCustName.setText(df.getValueAt(index, 1).toString());
        txtInitDeposit.setText(df.getValueAt(index, 2).toString());
        txtNumYears.setText(df.getValueAt(index, 3).toString());
        cbSavingType.setSelectedItem(df.getValueAt(index, 4).toString());

        String custNum = txtCustNum.getText();
        String custName = txtCustName.getText();
        String initDeposit = txtInitDeposit.getText();
        String numYears = txtNumYears.getText();
        String savingType = cbSavingType.getSelectedItem().toString();

        // Checking the combobox and generating the second Jtable.
        if (savingType.equals("Savings-Deluxe")) {
            Deluxe deluxe = new Deluxe(custNum, custName, initDeposit, numYears, savingType);
            deluxe.generateTable();
        } else if (savingType.equals("Savings-Regular")) {
            Java regular = new Java(custNum, custName, initDeposit, numYears, savingType);
            regular.generateTable();
        }
    }

    public void setCompoundTable(String[][] row, String[] col) {
        DefaultTableModel model = new DefaultTableModel(row, col);
        tblCompound.setModel(model);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        lblCustNum = new JLabel();
        txtCustNum = new JTextField();
        lblCustName = new JLabel();
        txtCustName = new JTextField();
        lblInitDeposit = new JLabel();
        txtInitDeposit = new JTextField();
        lblNumYears = new JLabel();
        txtNumYears = new JTextField();
        lblSavingType = new JLabel();
        cbSavingType = new JComboBox<>();
        scrollPane1 = new JScrollPane();
        tblData = new JTable();
        scrollPane2 = new JScrollPane();
        tblCompound = new JTable();
        btnAdd = new JButton();
        btnEdit = new JButton();
        btnDelete = new JButton();

        //======== this ========
        setTitle("Compound Interest");
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[fill]" +
            "[fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- lblCustNum ----
        lblCustNum.setText("Enter the Customer Number: ");
        contentPane.add(lblCustNum, "cell 0 0");

        //---- txtCustNum ----
        txtCustNum.setColumns(30);
        contentPane.add(txtCustNum, "cell 1 0");

        //---- lblCustName ----
        lblCustName.setText("Enter the Customer Name:");
        contentPane.add(lblCustName, "cell 0 1");

        //---- txtCustName ----
        txtCustName.setColumns(30);
        contentPane.add(txtCustName, "cell 1 1");

        //---- lblInitDeposit ----
        lblInitDeposit.setText("Enter the initial Deposit:");
        contentPane.add(lblInitDeposit, "cell 0 2");

        //---- txtInitDeposit ----
        txtInitDeposit.setColumns(30);
        contentPane.add(txtInitDeposit, "cell 1 2");

        //---- lblNumYears ----
        lblNumYears.setText("Enter the number of years: ");
        contentPane.add(lblNumYears, "cell 0 3");

        //---- txtNumYears ----
        txtNumYears.setColumns(30);
        contentPane.add(txtNumYears, "cell 1 3");

        //---- lblSavingType ----
        lblSavingType.setText("Choose the type of savings");
        contentPane.add(lblSavingType, "cell 0 4");

        //---- cbSavingType ----
        cbSavingType.setModel(new DefaultComboBoxModel<>(new String[] {
            "Savings-Deluxe",
            "Savings-Regular"
        }));
        contentPane.add(cbSavingType, "cell 1 4");

        //======== scrollPane1 ========
        {

            //---- tblData ----
            tblData.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        tblDataMouseClicked(e);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            scrollPane1.setViewportView(tblData);
        }
        contentPane.add(scrollPane1, "cell 0 5");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(tblCompound);
        }
        contentPane.add(scrollPane2, "cell 1 5");

        //---- btnAdd ----
        btnAdd.setText("Add");
        btnAdd.addActionListener(e -> {
            try {
                btnAddActionPerformed(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        contentPane.add(btnAdd, "cell 0 6");

        //---- btnEdit ----
        btnEdit.setText("Edit");
        btnEdit.addActionListener(e -> {
            try {
                btnEditActionPerformed(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        contentPane.add(btnEdit, "cell 0 6");

        //---- btnDelete ----
        btnDelete.setText("Delete");
        btnDelete.addActionListener(e -> {
            try {
                btnDeleteActionPerformed(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        contentPane.add(btnDelete, "cell 0 6");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JLabel lblCustNum;
    private JTextField txtCustNum;
    private JLabel lblCustName;
    private JTextField txtCustName;
    private JLabel lblInitDeposit;
    private JTextField txtInitDeposit;
    private JLabel lblNumYears;
    private JTextField txtNumYears;
    private JLabel lblSavingType;
    private JComboBox<String> cbSavingType;
    private JScrollPane scrollPane1;
    private JTable tblData;
    private JScrollPane scrollPane2;
    private JTable tblCompound;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
