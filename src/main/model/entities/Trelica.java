package main.model.entities;

import main.model.enums.EnumRestricao;
import main.model.exception.IndeterminacaoException;
import main.model.valueObject.Forca;

import java.util.Arrays;

public class Trelica {

    private Elemento[] elementos;
    private No[] nos;
    private int matrixAdj[][];
    int nIncognitasX;
    int nIncognitasY;
    int nElementos;

    private double areaSecao;
    private double limiteElasticidade;
    private double moduloElasticidade = 200e9;

    // Nova propriedade para rigidez direta
    private double[] deslocamentosExatos;

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

    public Elemento[] getElementos() {
        return elementos;
    }

    public void setElementos(Elemento[] elementos) {
        this.elementos = elementos;
    }

    public No[] getNos() {
        return nos;
    }

    public void setNos(No[] nos) {
        this.nos = nos;
    }

    public int getnIncognitasX() {
        return nIncognitasX;
    }

    public void setnIncognitasX(int nIncognitasX) {
        this.nIncognitasX = nIncognitasX;
    }

    public int getnIncognitasY() {
        return nIncognitasY;
    }

    public void setnIncognitasY(int nIncognitasY) {
        this.nIncognitasY = nIncognitasY;
    }

    public int getnElementos() {
        return nElementos;
    }

    public void setnElementos(int nElementos) {
        this.nElementos = nElementos;
    }

    public double getAreaSecao() {
        return areaSecao;
    }

    public void setAreaSecao(double areaSecao) {
        this.areaSecao = areaSecao;
    }

    public double getLimiteElasticidade() {
        return limiteElasticidade;
    }

    public void setLimiteElasticidade(double limiteElasticidade) {
        this.limiteElasticidade = limiteElasticidade;
    }

    public double getModuloElasticidade() {
        return moduloElasticidade;
    }

    public void setModuloElasticidade(double moduloElasticidade) {
        this.moduloElasticidade = moduloElasticidade;
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
                case P:
                    rxIndex[i] = varIndex++;
                    ryIndex[i] = varIndex++;
                    nIncognitasX++;
                    nIncognitasY++;
                    break;
                case Y:
                    rxIndex[i] = null;
                    ryIndex[i] = varIndex++;
                    nIncognitasY++;
                    break;
                case X:
                    rxIndex[i] = varIndex++;
                    ryIndex[i] = null;
                    nIncognitasX++;
                    break;
                case E:
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

            B[2 * i] = -somatorioX;
            B[2 * i + 1] = -somatorioY;
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
                A[2 * i][idx] = 1.0;
            }
            if (ryIndex[i] != null) {
                int idx = ryIndex[i];
                A[2 * i + 1][idx] = 1.0;
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

        //REAÇÕES NOS NÓS NÃO LIVRES
        for (int i = 0; i < nos.length; i++) {
            double rx = (rxIndex[i] != null) ? x[rxIndex[i]] : 0.0;
            double ry = (ryIndex[i] != null) ? x[ryIndex[i]] : 0.0;
            if (rxIndex[i] != null || ryIndex[i] != null) {
                System.out.printf("%.2f; %.2f\n", rx, ry);
            }
        }

        // FORÇAS INTERNAS com deformações e tensões
        for (int i = 0; i < nElementos; i++) {
            double f = x[i];
            double fx = f * elementos[i].getCos();
            double fy = f * elementos[i].getSen();
            int a = elementos[i].getA();
            int b = elementos[i].getB();

            // Armazenar a força axial no elemento
            elementos[i].setForcaAxial(f);

            // Calcular tensão e deformação para este elemento
            elementos[i].calcularTensaoDeformacao(areaSecao, moduloElasticidade);
            double deformacao = elementos[i].getDeformacao();
            double tensao = elementos[i].getTensao();

            // Nova saída formatada com deformação e tensão
            System.out.printf("F%d,%d :%.1f; %.2f; %.2f; %.6f; %.2f\n",
                    a, b, fx, fy, f, deformacao, tensao);
        }
    }

    // NOVO MÉTODO: Resolver treliça com método da rigidez direta
    public void ResolverTrelicaDeformada() {
        // Primeiro calcula as forças pelo método original
        ResolverTrelica();

        // Agora calcula os deslocamentos exatos pelo método da rigidez direta
        calcularRigidezDireta();

        // Exibe deslocamentos
        exibirDeslocamentosFormatados();
    }

    // MÉTODO DA RIGIDEZ DIRETA
    private void calcularRigidezDireta() {
        int nNos = nos.length;
        int nGL = 2 * nNos;

        double[][] K = new double[nGL][nGL];
        double[] F = new double[nGL];
        double[] U = new double[nGL];

        // Inicializar vetor de forças
        for (int i = 0; i < nNos; i++) {
            F[2*i] = nos[i].getForca().getPx();
            F[2*i + 1] = nos[i].getForca().getPy();
        }

        // Montar matriz de rigidez global
        for (Elemento elem : elementos) {
            if (elem != null) {
                adicionarRigidezElemento(K, elem);
            }
        }

        // Aplicar condições de contorno
        int[] glLivre = aplicarCondicoesContornoRigidez(K, F);

        // Resolver sistema
        double[] U_livre = resolverSistemaRigidez(K, F, glLivre);

        // Montar vetor de deslocamentos completo
        U = montarVetorDeslocamentosCompleto(U_livre, glLivre);

        // Armazenar deslocamentos
        this.deslocamentosExatos = U;
    }

    // Método para adicionar rigidez de um elemento à matriz global
    private void adicionarRigidezElemento(double[][] K, Elemento elem) {
        int i = elem.getA();
        int j = elem.getB();

        double c = elem.getCos();
        double s = elem.getSen();
        double L = elem.getTamanho();
        double EA_L = (moduloElasticidade * areaSecao) / L;

        // Matriz de rigidez local do elemento
        double k11 = c * c * EA_L;
        double k12 = c * s * EA_L;
        double k13 = -c * c * EA_L;
        double k14 = -c * s * EA_L;
        double k22 = s * s * EA_L;
        double k23 = -c * s * EA_L;
        double k24 = -s * s * EA_L;
        double k33 = c * c * EA_L;
        double k34 = c * s * EA_L;
        double k44 = s * s * EA_L;

        // Posições na matriz global
        int gl_i_x = 2 * i;
        int gl_i_y = 2 * i + 1;
        int gl_j_x = 2 * j;
        int gl_j_y = 2 * j + 1;

        // Adicionar à matriz global K
        K[gl_i_x][gl_i_x] += k11;
        K[gl_i_x][gl_i_y] += k12;
        K[gl_i_y][gl_i_x] += k12;
        K[gl_i_y][gl_i_y] += k22;

        K[gl_i_x][gl_j_x] += k13;
        K[gl_i_x][gl_j_y] += k14;
        K[gl_i_y][gl_j_x] += k23;
        K[gl_i_y][gl_j_y] += k24;

        K[gl_j_x][gl_i_x] += k13;
        K[gl_j_x][gl_i_y] += k14;
        K[gl_j_y][gl_i_x] += k23;
        K[gl_j_y][gl_i_y] += k24;

        K[gl_j_x][gl_j_x] += k33;
        K[gl_j_x][gl_j_y] += k34;
        K[gl_j_y][gl_j_x] += k34;
        K[gl_j_y][gl_j_y] += k44;
    }

    // Método para aplicar condições de contorno
    private int[] aplicarCondicoesContornoRigidez(double[][] K, double[] F) {
        int nGL = K.length;
        int nNos = nos.length;

        boolean[] glLivre = new boolean[nGL];
        Arrays.fill(glLivre, true);

        for (int i = 0; i < nNos; i++) {
            No no = nos[i];
            int glX = 2 * i;
            int glY = 2 * i + 1;

            switch (no.getRestricao()) {
                case P:
                    glLivre[glX] = false;
                    glLivre[glY] = false;
                    for (int j = 0; j < nGL; j++) {
                        K[glX][j] = 0;
                        K[j][glX] = 0;
                        K[glY][j] = 0;
                        K[j][glY] = 0;
                    }
                    K[glX][glX] = 1;
                    K[glY][glY] = 1;
                    F[glX] = 0;
                    F[glY] = 0;
                    break;
                case Y:
                    glLivre[glY] = false;
                    for (int j = 0; j < nGL; j++) {
                        K[glY][j] = 0;
                        K[j][glY] = 0;
                    }
                    K[glY][glY] = 1;
                    F[glY] = 0;
                    break;
                case X:
                    glLivre[glX] = false;
                    for (int j = 0; j < nGL; j++) {
                        K[glX][j] = 0;
                        K[j][glX] = 0;
                    }
                    K[glX][glX] = 1;
                    F[glX] = 0;
                    break;
            }
        }

        int nLivre = 0;
        for (boolean livre : glLivre) {
            if (livre) nLivre++;
        }

        int[] indicesLivres = new int[nLivre];
        int count = 0;
        for (int i = 0; i < nGL; i++) {
            if (glLivre[i]) {
                indicesLivres[count++] = i;
            }
        }

        return indicesLivres;
    }

    // Resolver sistema reduzido
    private double[] resolverSistemaRigidez(double[][] K, double[] F, int[] glLivre) {
        int nLivre = glLivre.length;
        if (nLivre == 0) return new double[0];

        double[][] K_red = new double[nLivre][nLivre];
        double[] F_red = new double[nLivre];

        for (int i = 0; i < nLivre; i++) {
            int gl_i = glLivre[i];
            F_red[i] = F[gl_i];
            for (int j = 0; j < nLivre; j++) {
                int gl_j = glLivre[j];
                K_red[i][j] = K[gl_i][gl_j];
            }
        }

        return solveLinearSystem(K_red, F_red);
    }

    // Montar vetor de deslocamentos completo
    private double[] montarVetorDeslocamentosCompleto(double[] U_livre, int[] glLivre) {
        int nGL = 2 * nos.length;
        double[] U = new double[nGL];

        for (int i = 0; i < glLivre.length; i++) {
            U[glLivre[i]] = U_livre[i];
        }

        return U;
    }

    // Exibir deslocamentos no formato solicitado
    private void exibirDeslocamentosFormatados() {
        System.out.println("Deslocamentos dos nós (em metros):");
        System.out.println("Nó\tDeslocamento X (m)\tDeslocamento Y (m)");
        for (int i = 0; i < nos.length; i++) {
            double ux = deslocamentosExatos[2*i];
            double uy = deslocamentosExatos[2*i + 1];
            System.out.printf("%s\t%.6f\t\t%.6f\n", nos[i].getLetra(), ux, uy);
        }
    }

    // REMOVA completamente o método CalcularTrelicaDeformada() para evitar duplicação

    //EQUAÇÕES AUXILIARES
    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        int m = A[0].length;
        if (n != m) return null;

        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        for (int k = 0; k < n; k++) {
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

    private double[] solveLeastSquares(double[][] A, double[] b) {
        int rows = A.length;
        int cols = A[0].length;

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

        return solveLinearSystem(AtA, Atb);
    }
}