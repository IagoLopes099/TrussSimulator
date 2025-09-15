package main.Java;

import main.model.entities.No;
import main.model.entities.Trelica;
import main.model.enums.EnumRestricao;
import main.model.valueObject.Forca;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //operações de input do usuário
        Scanner sc = new Scanner(System.in);
        String linha = sc.nextLine();
        String[] partes = linha.split(";");

        //capturando o numero de nós e de elementos
        int numeroNos = Integer.parseInt(partes[0].trim());
        int numeroElementos = Integer.parseInt(partes[1].trim());

        //capturando os nós
        double x, y;
        String letra;

        No[] listaNos = new No[numeroNos];

        String[] partes2;

        for(int i=0; i < numeroNos ; i++){

            String linha2 = sc.nextLine();
            partes2 = linha2.split(";");
            letra = partes2[0].trim();
            x = Double.parseDouble(partes2[1].trim());
            y = Double.parseDouble(partes2[2].trim());
            No n = new No(x,y,letra, i);
            listaNos[i] = n;
        }

        // RECEBENDO A MATRIZ DE ADJACÊNCIA
        String[] partes3;
        int matrixAdj[][] = new int[numeroNos][numeroNos];

        for(int i = 0 ; i < numeroNos ; i++){

            String linhaAdj = sc.nextLine();
            partes3 = linhaAdj.split(";");

            for(int j = 0; j< numeroNos ; j++){

                matrixAdj[i][j] = Integer.parseInt(partes3[j].trim());
            }
        }

        //criando a trelica e exibindo seus parâmetros
        Trelica trelica = new Trelica(listaNos, matrixAdj, numeroElementos);


        String[] parteForc;
        for(int i = 0 ; i < numeroNos ; i++){
            String linhaForc = sc.nextLine();
            parteForc = linhaForc.split(";");

            double px = Double.parseDouble(parteForc[0].trim());
            double py = Double.parseDouble(parteForc[1].trim());

            Forca f = new Forca(px,py);
            trelica.atribuirForca(f,i);
        }

        for(int i =0; i< numeroNos; i++){
            String Rest = sc.nextLine();
            EnumRestricao e;
            switch (Rest){
                case "P":
                    e = EnumRestricao.PINADO;
                    trelica.setRestricao(e, i);
                    break;
                case "X":
                    e = EnumRestricao.APOIADOVERTICAL;
                    trelica.setRestricao(e, i);
                    break;
                case "Y":
                    e = EnumRestricao.APOIADOHORINZONTAL;
                    trelica.setRestricao(e, i);
                    break;
                case "N":
                    e = EnumRestricao.LIVRE;
                    trelica.setRestricao(e, i);
                    break;
                default:
                    e = EnumRestricao.ENGASTADO;
                    trelica.setRestricao(e, i);
                    break;
            }

        }

        System.out.println(trelica);
        try{
            trelica.ResolverTrelica();

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }




        sc.close();
    }
}