package main.model.valueObject;

public class Forca {
    private double P; // modulo
    private double angulo; //Angulo referente ao eixo X positivo no anti-horario
    private double Px; // coordenada x de P
    private double Py; // coordenada y de P

    public Forca(double x, double y) {
        this.Px = x;
        this.Py = y;
    }

    public double getP() {
        return P;
    }

    public void setP(double p) {
        this.P = p;
    }

    public double getAngulo() {
        return angulo;
    }

    public void setAngulo(double angulo) {
        this.angulo = angulo;
    }

    public double getPx() {
        return Px;
    }

    public void setPx(double px) {
        this.Px = px;
    }

    public double getPy() {
        return Py;
    }

    public void setPy(double py) {
        this.Py = py;
    }
}
