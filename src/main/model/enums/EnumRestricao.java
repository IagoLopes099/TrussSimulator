package main.model.enums;

public enum EnumRestricao {
    ENGASTADO, //restrição em X,Y e momento Z
    PINADO,    //restrição em X e Y
    APOIADOVERTICAL, //restrição em Y vulgo rolete
    APOIADOHORINZONTAL, // restrição em X vulto apoio lateral
    LIVRE       //sem restrição em X e Y e momento
}
