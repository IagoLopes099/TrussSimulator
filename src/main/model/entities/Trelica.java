package main.model.entities;

import main.model.enums.EnumRestricao;
import main.model.exception.IndeterminacaoException;
import main.model.valueObject.Forca;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

public class Trelica {

    private Elemento[] elementos;
    private No[] nos;
    private int matrixAdj[][];
    int nIncognitasX;
    int nIncognitasY;
    int nElementos;


    public Trelica(No[] n, int[][] matrixAdj, int nElementos){

        this.nos = n;
        this.matrixAdj = matrixAdj;
        this.nElementos = nElementos;
        this.elementos = new Elemento[nElementos];
    }

    public void atribuirForca(Forca f, int pos){
        nos[pos].setForca(f);
    }

    public void setRestricao(EnumRestricao e, int pos){
        nos[pos].setRestricao(e);
    }

    @Override
    public String toString() {
        return "Trelica{" +
                "nos=" + Arrays.toString(nos) +
                ", matrixAdj=" + Arrays.deepToString(matrixAdj) +
                '}';
    }

    //Calculando o tamanho e os indices dos elementos
    public void CalcularElementos(){
        int cont = 0;

        for(int i =0 ; i < matrixAdj.length; i++){
            for(int j = 0; j < matrixAdj.length; j++){
                if(j > i) {
                    if (matrixAdj[i][j] != 0) {
                        Elemento e = new Elemento(i, j);
                        e.setParametros(nos, i,j);
                        elementos[cont] = e;
                        cont++;
                    }
                }
            }
        }
    }

    //----------------------------resolver treliça------------------------//
    public void ResolverTrelica() {

        CalcularElementos();

        int varIndex = nElementos;
        Integer[] rxIndex = new Integer[nos.length];
        Integer[] ryIndex = new Integer[nos.length];

        for (int i = 0; i < nos.length; i++) {
            switch (nos[i].getRestricao()) {
                case PINADO:
                    rxIndex[i] = varIndex++;
                    ryIndex[i] = varIndex++;
                    nIncognitasX++;
                    nIncognitasY++;
                    break;
                case APOIADOVERTICAL:
                    rxIndex[i] = null;
                    ryIndex[i] = varIndex++;
                    nIncognitasY++;
                    break;
                case APOIADOHORINZONTAL:
                    rxIndex[i] = varIndex++;
                    ryIndex[i] = null;
                    nIncognitasX++;
                    break;
                case ENGASTADO:
                    rxIndex[i] = varIndex++;
                    ryIndex[i] = varIndex++;
                    nIncognitasX++;
                    nIncognitasY++;
                    break;
                default:
                    rxIndex[i] = null;
                    ryIndex[i] = null;
                    break;
            }
        }

        int nReacoes = nIncognitasX + nIncognitasY;

        int checkIndeterminacao = nElementos + nReacoes - (2 * nos.length);


        if (checkIndeterminacao > 0) {
            throw new IndeterminacaoException("A treliça é indeterminada pelo método de Equilibrio de corpos rígidos!");
        }

        int nEquacoes = nos.length * 2;
        int nIncognitas = nIncognitasX + nIncognitasY + nElementos;

        double[][] A = new double[nEquacoes][nIncognitas];
        double[] B = new double[nEquacoes];


        // CARGAS
        for(int i = 0; i< nos.length ; i++){
            double somatorioX = 0.0;
            double somatorioY = 0.0;
            if(nos[i].getForca() != null){
                somatorioX = nos[i].getForca().getPx();
                somatorioY = nos[i].getForca().getPy();
            }

            B[2 * i] = -somatorioX;      // Somatorio das forças em X
            B[2 * i + 1] = -somatorioY;  // Somatorio das forças em Y
        }

        // ELEMENTOS
        for(int i =0; i < elementos.length; i++){
            A[2 * elementos[i].getA()][i] = elementos[i].getCos();
            A[2 * elementos[i].getA() + 1][i] = elementos[i].getSen();

            A[2 * elementos[i].getB()][i] = -elementos[i].getCos();
            A[2 * elementos[i].getB() + 1][i] = -elementos[i].getSen();
        }

        // REAÇÕES
        for (int i = 0; i < nos.length; i++) {
            if (rxIndex[i] != null) {
                int idx = rxIndex[i];
                A[2 * i][idx] = 1.0; // ΣFx
            }
            if (ryIndex[i] != null) {
                int idx = ryIndex[i];
                A[2 * i + 1][idx] = 1.0; // ΣFy
            }
        }



        double[] x;

        if (nEquacoes == nIncognitas) {
            x = solveLinearSystem(A, B);
        } else {
            x = solveLeastSquares(A, B);
        }

        if (x == null) {
            System.out.println("O sistema não pode ser resolvido numericaente!");
            return;
        }


        for (int i = 0; i < nos.length; i++) {
            double rx = (rxIndex[i] != null) ? x[rxIndex[i]] : 0.0;
            double ry = (ryIndex[i] != null) ? x[ryIndex[i]] : 0.0;
            if (rxIndex[i] != null || ryIndex[i] != null) {
                //System.out.println(rx + "; " +  ry);
                System.out.printf("%.2f; %.2f\n", rx, ry);
            }
        }

        // Forças internas
        for (int i = 0; i < nElementos; i++) {
            double f = x[i]; // módulo orientado
            double fx = f * elementos[i].getCos();
            double fy = f * elementos[i].getSen();
            int a = elementos[i].getA();
            int b = elementos[i].getB();
            //System.out.println(fx + "; "+ fy +"; "+ f);
            System.out.printf("F%d,%d :%.1f; %.2f; %.2f\n",a,b , fx, fy, f);
        }

    }

    //EQUAÇÕES AUXILIARES by CHAT GPT
    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        int m = A[0].length;
        if (n != m) return null;
        // construir matriz aumentada
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // eliminação com pivot parcial
        for (int k = 0; k < n; k++) {
            // pivot
            int piv = k;
            double max = Math.abs(M[k][k]);
            for (int i = k + 1; i < n; i++) {
                double val = Math.abs(M[i][k]);
                if (val > max) { max = val; piv = i; }
            }
            if (Math.abs(M[piv][k]) < 1e-12) {
                System.out.println("Matriz singular ou quase singular no pivot " + k);
                return null;
            }
            if (piv != k) {
                double[] tmp = M[k]; M[k] = M[piv]; M[piv] = tmp;
            }
            // normalize and eliminate
            double pivot = M[k][k];
            for (int j = k; j <= n; j++) M[k][j] /= pivot;
            for (int i = 0; i < n; i++) {
                if (i == k) continue;
                double factor = M[i][k];
                if (Math.abs(factor) < 1e-15) continue;
                for (int j = k; j <= n; j++) M[i][j] -= factor * M[k][j];
            }
        }

        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = M[i][n];
        return x;
    }

    // least squares via normal equations: (A^T A) x = A^T b
    private double[] solveLeastSquares(double[][] A, double[] b) {
        int rows = A.length;
        int cols = A[0].length;
        // compute AtA and Atb
        double[][] AtA = new double[cols][cols];
        double[] Atb = new double[cols];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < cols; j++) {
                double s = 0.0;
                for (int k = 0; k < rows; k++) s += A[k][i] * A[k][j];
                AtA[i][j] = s;
            }
            double sb = 0.0;
            for (int k = 0; k < rows; k++) sb += A[k][i] * b[k];
            Atb[i] = sb;
        }
        // try solving AtA * x = Atb
        return solveLinearSystem(AtA, Atb);
    }
}
