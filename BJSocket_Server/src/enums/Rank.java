package enums;

public enum Rank {
    UM("Ás", 1),
    DOIS("Dois", 2),
    TRES("Três", 3),
    QUATRO("Quatro", 4),
    CINCO("Cinco", 5),
    SEIS("Seis", 6),
    SETE("Sete", 7),
    OITO("Oito", 8),
    NOVE("Nove", 9),
    DEZ("Dez", 10),
    VALETE("Valete", 10),
    RAINHA("Rainha", 10),
    REI("Rei", 10);

    private String nomeRank;
    private int valorRank;
    //construtor do nome e valor das cartas
    Rank(String nome, int valor){
        this.nomeRank = nome;
        this.valorRank = valor;
    }
    //metodo get do nomeRank
    public String getNomeRank(){ return this.nomeRank; }
    //metodo get do valorRank
    public int getValorRank(){ return this.valorRank; }

    @Override
    public String toString(){
        return this.nomeRank;
    }
}
