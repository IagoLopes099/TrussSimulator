package main.model.entities;

import main.model.enums.EnumRestricao;

public class No {
    private double x;
    private double y;
    private EnumRestricao restricao;


    public No(double x, double y, EnumRestricao restricao){
        this.x = x;
        this.y = y;
        this.restricao = restricao;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
