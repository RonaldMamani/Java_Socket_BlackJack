package deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import enums.Naipe;
import enums.Rank;

public class Baralho {
    //Lista de Cartas
    private List<Carta> deck = new ArrayList<>();;
    //Metodo onde da valor e nome para as cartas do Baralho
    public Baralho(){
        for(Naipe naipe : Naipe.values()){
            for(Rank rank : Rank.values()){
                this.deck.add(new Carta(naipe, rank));
            }
        }
        //shuffle é um metdo para embaralhar os cartas
        Collections.shuffle(this.deck);
    }
    
    @Override
    public String toString(){
        String output = "";
        for(Carta carta : this.deck){
            output += carta + "\n";
        }
        return output;
    }
    //Metodo para pegar cartas do bartalho
    public Carta pegarCarta(){
        Carta cartaParaPegar = this.deck.get(0);
        this.deck.remove(0);
        return cartaParaPegar;
    }
    //metodo para mostrar as cartas
    public String mostrarCartas(){
        String output = "";
        for(int i = 0; i < 3; i++){
            output += "\n" + deck.get(i);
        }
        return output;
    }
}