package main.model.valueObject;

import main.model.enums.EnumRestricao;

public class Reacao {
    int nReacao = 0;
    private double Rx;
    private double Ry;
    private double M;

    public Reacao(EnumRestricao e){
        switch (e){
            case P:
                this.M = 0;
                nReacao += 2;
                break;
            case X:
                this.M = 0;
                this.Rx =0;
                nReacao +=  1;
                break;
            case Y:
                this.M = 0;
                this.Ry = 0;
                nReacao +=  1;
            case N:
                this.M =0;
                this.Ry =0;
                this.Rx =0;
            case E:
                nReacao +=3;
                break;
        }
    }

    public int getnReacao(){
        return nReacao;
    }

    public void setnReacao(int nReacao) {
        this.nReacao = nReacao;
    }

    public double getRx() {
        return Rx;
    }

    public void setRx(double rx) {
        Rx = rx;
    }

    public double getRy() {
        return Ry;
    }

    public void setRy(double ry) {
        Ry = ry;
    }

    public double getM() {
        return M;
    }

    public void setM(double m) {
        M = m;
    }
}
