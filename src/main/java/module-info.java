module com.example.mapnavigator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.example.mapnavigator to javafx.fxml;
    exports com.example.mapnavigator;
}
