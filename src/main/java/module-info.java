module com.example.javaprojecttictactoe {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.javaprojecttictactoe to javafx.fxml;
    exports com.example.javaprojecttictactoe;
}