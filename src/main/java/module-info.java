module com.example.terence_16_lab5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.GroceryStore to javafx.fxml;
    exports com.example.GroceryStore;
}