module com.example.traffic6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.traffic6 to javafx.fxml;
    exports com.example.traffic6;
}