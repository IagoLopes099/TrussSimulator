package main.model.entities;

import java.util.ArrayList;

public class Trelica {

    private Elemento[] elementos;
    private No[] nos;


    public Trelica(int numeroNos, int numeroElementos){
        nos = new No[numeroNos];
        elementos = new Elemento[numeroElementos];
    }


}
