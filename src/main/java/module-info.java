module com.example.sorting_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.example.sorting_project.engine to javafx.base;
    opens com.example.sorting_project to javafx.fxml;
    exports com.example.sorting_project;
}