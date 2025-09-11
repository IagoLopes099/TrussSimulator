package main.model.entities;

import main.model.enums.EnumRestricao;
import main.model.valueObject.Forca;
import main.model.valueObject.Reacao;

public class No {
    private String letra;
    private int posicao;
    private double x;
    private double y;
    private Forca forca;
    private EnumRestricao restricao;
    private Reacao rec;


    public No(double x, double y, String letra, int p){
        this.x = x;
        this.y = y;
        this.letra = letra;
        this.posicao = p;
        this.restricao = EnumRestricao.LIVRE; // estou setando livre como padrão para garantir que foi colcoado uma restrição
        rec = new Reacao(restricao);
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
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

    public void setLetra(String l) {
        this.letra = l;
    }

    public String getLetra(){
        return letra;
    }

    public Forca getForca() {
        return forca;
    }

    public void setForca(Forca f) {
        this.forca = f;
    }

    public EnumRestricao getRestricao() {
        return restricao;
    }

    public void setRestricao(EnumRestricao restricao) {
        this.restricao = restricao;
    }

    public Reacao getRec() {
        return rec;
    }

    public void setRec(Reacao rec) {
        this.rec = rec;
    }

    @Override
    public String toString() {
        return "No{" +
                "letra='" + letra + '\'' +
                ", posicao=" + posicao +
                ", x=" + x +
                ", y=" + y +
                ", Px=" + forca.getPx() +
                ", Py=" + forca.getPy() +
                ", P=" + forca.getP() +
                ", Restrição=" + restricao +
                '}' + "\n";
    }
}
