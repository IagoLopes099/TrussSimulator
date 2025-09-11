package main.model.entities;

public class Elemento {
    private int a;
    private int b;
    private double cos;
    private double sen;
    private double tamanho;

    public Elemento(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public double getCos() {
        return cos;
    }

    public void setCos(double cos) {
        this.cos = cos;
    }

    public double getSen() {
        return sen;
    }

    public void setSen(double sen) {
        this.sen = sen;
    }

    public double getTamanho() {
        return tamanho;
    }

    public void setParametros(No[] no, int i, int j) {
        double dx = no[j].getX() - no[i].getX();
        double dy = no[j].getY() - no[i].getY();
        this.tamanho = Math.hypot(dx,dy);

        try {
            this.cos = dx / tamanho;
            this.sen = dy / tamanho;

        }catch (ArithmeticException e){
            throw new ArithmeticException("Elemento de tamanho 0!");
        }
    }
}