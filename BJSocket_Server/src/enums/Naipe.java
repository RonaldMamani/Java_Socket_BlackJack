package enums;

public enum Naipe {
    ESPADAS("Espadas"),
    PAUS("Paus"),
    OUROS("Ouros"),
    COPAS("Copas");

    private String naipe;
    //construtor de naipe
    Naipe(String naipe){
        this.naipe = naipe;
    }
    //metodo get para imprimir o naipe
    public String getNaipe(){ return this.naipe; }

    @Override
    public String toString(){
        return this.naipe;
    }
}
