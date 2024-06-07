module com.example.imo_lab12 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.imo_lab12 to javafx.fxml;
    exports com.example.imo_lab12;
}