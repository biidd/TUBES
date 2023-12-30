module com.example.tubes {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tubes to javafx.fxml;
    exports com.example.tubes;
}