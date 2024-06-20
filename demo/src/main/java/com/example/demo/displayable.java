package com.example.demo;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public interface displayable{
    public Rectangle getRect();

    public void display(AnchorPane pane);
    void remove(AnchorPane pane);
}