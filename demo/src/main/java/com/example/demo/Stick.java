package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import java.time.chrono.AbstractChronology;

public class Stick implements displayable {
    private static Stick instance = null;

    private Rectangle rect;
    private boolean locked = false;
    private boolean[] conditions = { true, false };
    private Rotate rotate;
    private double width;
    private double height;
    private double increase_rate = 7;

    // Private constructor to restrict instantiation from outside
    private Stick(double x, double y) {
        width = 5;
        height = 10;
        this.rect = new Rectangle(x, y, width, height);
        rotate = new Rotate(3, rect.getX() + (rect.getWidth() / 2), rect.getY() + rect.getHeight());
    }

    // Static method to provide access to the instance
    public static Stick getInstance(double x, double y) {
        if (instance == null) {
            instance = new Stick(x, y);
        }
        return instance;
    }

    public void activity(Runnable afterfall) {
        if (locked) {
            return;
        }
        if (conditions[1]) {
            if (conditions[0]) {
                rect.setHeight(rect.getHeight() + increase_rate);
                rect.setY(rect.getY() - increase_rate);
            } else if (rect.getTransforms().size() < 90 / rotate.getAngle()) {
                rect.getTransforms().add(rotate);
            } else {

                lock();
                conditions[1] = false;
                afterfall.run();

            }
        }
    }

    public void reset(double x,double y,AnchorPane pane, Rectangle[] trashstick){
        locked=false;
        //rect.setY(y);
        if (trashstick[0]!=null){
            pane.getChildren().remove(trashstick[0]);
        }
        trashstick[0]=rect;
        this.rect=new Rectangle(x,y,width,height);
        //rect.setFill(Color.BURLYWOOD);
        rotate= new Rotate(3, rect.getX() + (rect.getWidth() / 2), rect.getY() + rect.getHeight());
        conditions[0]=true;
        conditions[1]=false;
        pane.getChildren().add(rect);


    }

    public void lock(){
        locked=true;
    }


    public boolean isLocked(){
        return locked;
    }

    public boolean[] getConditions() {
        return conditions;
    }
    public Rectangle getRect(){
        return rect;
    }

    @Override
    public void display(AnchorPane pane) {
        pane.getChildren().add(rect);
    }

    @Override
    public void remove(AnchorPane pane) {
        pane.getChildren().remove(rect);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
