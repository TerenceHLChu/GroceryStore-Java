//Student name: Terence Chu
//Student ID: 301220117

package com.example.GroceryStore;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
//import java.sql.DriverManager;
//import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.sql.*;

public class GroceryStoreController implements Initializable {
    private ObservableList<String> itemList = FXCollections.observableArrayList("Milk", "Cheese", "Bread", "Eggs", "Apples");
    private ObservableList<String> quantityList = FXCollections.observableArrayList("1", "2", "3", "4", "5");
    private String selectedItem;
    private String quantity;
    private String outputMessage;
    private String selectedDeliveryOption;
    private String customerName;
    private String customerPhoneNumber;
    private String customerAddress;
    private String customerCity;
    private String customerPostalCode;
    private int numberOfSelectedItems = 0;
    private String cartOverview = "Cart Overview:\n";
    private double extensionPrice[];
    private String dbItem[];
    private int dbQuantity[];
    private double dbUnitPrice[];
    private double subtotal;
    private double tax;
    private double shippingCost;
    private double grandTotal;
    private static Connection connection;
    private static PreparedStatement statement;
    private final String TABLE_NAME = "TERENCE_ORDERS";

    @FXML
    private ComboBox cmbItem;
    @FXML
    private ComboBox cmbQuantity;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfPhone;
    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfCity;
    @FXML
    private TextField tfPostal;
    @FXML
    private RadioButton rdoPickup;
    @FXML
    private RadioButton rdoHome;
    @FXML
    private ToggleGroup rdoDeliveryGroup;
    @FXML
    private Label lblOutput;
    @FXML
    private Label lblPrice;
    @FXML
    public Label lblCartOverview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String databaseURL = "jdbc:oracle:thin:@199.212.26.208:1521:SQLD"; //for remote connection
        String username = "COMP214_m23_er_9";
        String password = "password";

        //Establish a connection based on the supplied databaseURL, username, and password
        //Initialize the prepareStatement with INSERT command
        try {
            //1. Download the ojdbc6.jar (oracle driver) and add as project library through project structure
            //2. Register the driver
            Class.forName("oracle.jdbc.OracleDriver");

            //3. Open a connection to the database
            connection = DriverManager.getConnection(databaseURL, username, password);
            System.out.println("Database connection successfully established.");

            statement = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?)");

        } catch (ClassNotFoundException cnfe) {
            System.out.println("Cannot establish connection with database : " + cnfe);
        } catch (SQLException sqle) {
            System.out.println("Cannot establish connection with database : " + sqle);
        }

        try {

            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, TABLE_NAME, null);

            //If the TERENCE_ORDERS table exists, delete the contents of the table with TRUNCATE command (this ensures the receipt will only display items from the current purchase)
            //If the TERENCE_ORDERS tables does not exist, create it.
            if (resultSet.next()) {
                System.out.println("Table " + TABLE_NAME +  " exists in the DB");

                String query = "TRUNCATE TABLE " + TABLE_NAME;
                System.out.println("Query : " + query);

                if (!connection.isClosed()) {
                    //4. Create a statement object to perform a query
                    Statement statement = connection.createStatement();

                    //5. Execute the statement object and return a query result
                    statement.execute(query);
                    System.out.println(TABLE_NAME + " truncated.");

                    if (!statement.isClosed()) {
                        statement.close();
                    }
                } else {
                    System.out.println("Cannot truncate " + TABLE_NAME + " as the connection is closed.");
                }
            } else {
                System.out.println("Table " + TABLE_NAME + " DOES NOT exist in the DB");

                //CREATE TABLE TERENCE_ORDERS (ITEM VARCHAR(50), QUANTITY NUMBER(1), PRICE NUMBER(4,2))
                //NUMBER(4,2) is xx.xx. For example, 12.34.
                String query = "CREATE TABLE " + TABLE_NAME + " (ITEM VARCHAR(50), QUANTITY NUMBER(1), PRICE NUMBER(4,2))";
                System.out.println("Query : " + query);
                if (!connection.isClosed()) {
                    //4. Create a statement object to perform a query
                    Statement statement = connection.createStatement();

                    //5. Execute the statement object and return a query result
                    int n = statement.executeUpdate(query);
                    System.out.println(TABLE_NAME + " table is created.");

                    if (!statement.isClosed()) {
                        statement.close();
                    }
                } else {
                    System.out.println("Cannot create statement as the connection is closed.");
                }
            }
        } catch(SQLException sqle) {
            System.out.println("Cannot create employee table: " + sqle);
        }

        cmbItem.setItems(itemList);
        cmbQuantity.setItems(quantityList);

        rdoDeliveryGroup = new ToggleGroup();

        rdoPickup.setToggleGroup(rdoDeliveryGroup);
        rdoHome.setToggleGroup(rdoDeliveryGroup);

        rdoPickup.setSelected(true);
        selectedDeliveryOption = "Pick-up";

        lblPrice.setText(
                "Price List:\n" +
                        "Milk : $2.00\n" +
                        "Bread : $4.00\n" +
                        "Cheese : $6.00\n" +
                        "Eggs : $8.00\n" +
                        "Apples : $10.00\n"
        );
    }

    @FXML
    public void itemSelected(ActionEvent actionEvent) {
        selectedItem = cmbItem.getValue().toString();
    }

    @FXML
    public void quantitySelected(ActionEvent actionEvent) {
        quantity = cmbQuantity.getValue().toString();
    }

    public double getPrice(String item) {
        double price = 0;

        if (item == "Milk") {
            price = 2.00;
        } else if (item == "Bread") {
            price = 4.00;
        } else if (item == "Cheese") {
            price = 6.00;
        } else if (item == "Eggs") {
            price = 8.00;
        } else if (item == "Apples") {
            price = 10.00;
        }
        return price;
    }

    public double getDeliveryCost(String selectedDeliveryOption) {
        double cost = 0;

        if (selectedDeliveryOption.equals("Pick-up")){
            cost = 0.00;
        } else if (selectedDeliveryOption.equals("Delivery")) {
            cost = 5.00;
        }
        return cost;
    }

    public void onShowClick(ActionEvent actionEvent) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        subtotal = 0;

        if (tfName.getText().isEmpty()) {
            customerName = "NA";
        } else {
            customerName = tfName.getText();
        }

        if (tfPhone.getText().isEmpty()) {
            customerPhoneNumber = "NA";
        } else {
            customerPhoneNumber = tfPhone.getText();
        }

        if (tfAddress.getText().isEmpty()) {
            customerAddress = "NA";
        } else {
            customerAddress = tfAddress.getText();
        }

        if (tfCity.getText().isEmpty()) {
            customerCity = "NA";
        } else {
            customerCity = tfCity.getText();
        }

        if (tfPostal.getText().isEmpty()) {
            customerPostalCode = "NA";
        } else {
            customerPostalCode = tfPostal.getText();
        }

        //Define size of the arrays based on the number of items added to the cart
        extensionPrice = new double[numberOfSelectedItems];
        dbItem = new String[numberOfSelectedItems];
        dbQuantity = new int[numberOfSelectedItems];
        dbUnitPrice = new double[numberOfSelectedItems];

        outputMessage = "";
        outputMessage += "RECEIPT" + "\n----------\n";
        outputMessage += "Store Name: A LOCAL GROCERY STORE\n\n";

        outputMessage += "Customer name : " + customerName + "\n" +
                "Phone number : " + customerPhoneNumber + "\n" +
                "Address : " + customerAddress + "\n" +
                "City : " + customerCity + "\n" +
                "Postal code : " + customerPostalCode + "\n\n";

        retrieveAllRecords();

        for (int i = 0; i < numberOfSelectedItems; i++) {
            extensionPrice[i] = dbQuantity[i] * dbUnitPrice[i];
            subtotal += extensionPrice[i];
            outputMessage += dbItem[i] + "-" + dbQuantity[i] + " unit(s) -- " + formatter.format(extensionPrice[i]) + "\n";
        }

        tax = subtotal * 0.1;
        shippingCost = getDeliveryCost(selectedDeliveryOption);
        grandTotal = subtotal + tax + shippingCost;

        outputMessage += "-----------\n" +
        "Subtotal : " + formatter.format(subtotal) + "\n" +
        "Tax (10%) : " + formatter.format(tax) + "\n" +
        selectedDeliveryOption + " cost : " + formatter.format(shippingCost) + "\n" +
        "Total : " + formatter.format(grandTotal);

        lblOutput.setText(outputMessage);
    }

    public void onDeliverySelection(ActionEvent actionEvent) {
        RadioButton selectedRadioButton = (RadioButton) actionEvent.getSource();
        selectedDeliveryOption = selectedRadioButton.getText();
    }

    public void onAddToCartClick(ActionEvent actionEvent) {
        numberOfSelectedItems++;

        cartOverview = cartOverview + selectedItem + " : " + quantity + "\n";
        lblCartOverview.setText(cartOverview);

        insertPrepared(selectedItem, Integer.parseInt(quantity), getPrice(selectedItem));
    }

    public void insertPrepared(String item, int quantity, double price) {
        try{
            if (!connection.isClosed()){
                statement.setString(1, item);
                statement.setInt(2, quantity);
                statement.setDouble(3, price);

                int n = statement.executeUpdate();
                System.out.println(n + " record added into " + TABLE_NAME);

            } else {
                System.out.println("Cannot create statement as the connection is closed");
            }
        }catch (SQLException sqle){
            System.out.println("Cannot create record in the database : " + sqle);
        }
    }

    public void retrieveAllRecords() {
        int index = 0;

        //SELECT * FROM TERENCE_ORDERS
        String query = "SELECT * FROM " + TABLE_NAME;
        System.out.println(query);

        try{
            if (!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                int columnCount = resultSetMetaData.getColumnCount();

                //Outer loop iterates through the records
                //Inner loop iterates through the record's columns
                while (resultSet.next()) {
                    for (int col = 1; col <= columnCount; col++) {
                        if (col == 1) {
                            dbItem[index]  = resultSet.getString(col);
                        }else if (col == 2) {
                            dbQuantity[index] = Integer.parseInt(resultSet.getString(col));
                        }else if (col == 3) {
                            dbUnitPrice[index] = Double.parseDouble(resultSet.getString(col));
                        }
                    }
                    index++;
                }

                if (!statement.isClosed()) {
                    statement.close();
                }
            } else {
                System.out.println("Cannot create statement as the connection is closed");
            }
        }catch (SQLException sqle){
            System.out.println("Cannot retrieve records from the database : " + sqle);
        }
    }

    public void closeConnection() {
        try {
            if (!connection.isClosed()){

                if (!statement.isClosed()) {
                    statement.close();
                }

                connection.close();
                System.out.println("Database connection terminated successfully.");
            }
        } catch(SQLException sqle){
            System.out.println("Cannot terminate connection with database : " + sqle);
        }
    }
    public void onApplicationClose() {
        closeConnection();
        System.out.println("Application closing");
    }
}