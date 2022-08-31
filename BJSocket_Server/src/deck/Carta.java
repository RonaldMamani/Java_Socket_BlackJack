package deck;

import enums.Naipe;
import enums.Rank;

public class Carta {

    //variaveis
    private Naipe naipe;
    private Rank rank;

    //Cria as cartas para dar valor para as cartas
    public Carta(Naipe naipe, Rank rank){
        this.naipe = naipe;
        this.rank = rank;
    }
    //metodo para imprimir o rank do pacote emum.Rank
    public int getValor(){
        return rank.getValorRank();
    }
    //metodo para imprimir o naipe
    public Naipe getSuit(){
        return naipe;
    }
    //metodo para imprimir o rank
    public Rank getRank(){
        return rank;
    }
    //metodo para mostrar o nome e valor da carta
    public String toString(){
        return ("["+rank+" de "+ naipe + "] ("+this.getValor()+")");

    }

}