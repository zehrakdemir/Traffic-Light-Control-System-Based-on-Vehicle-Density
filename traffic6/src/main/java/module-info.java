module com.example.traffic6 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.traffic6 to javafx.fxml;
    exports com.example.traffic6;
}