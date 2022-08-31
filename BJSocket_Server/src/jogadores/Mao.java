package jogadores;

import java.util.ArrayList;
import java.util.List;

import deck.Baralho;
import deck.Carta;
    
public class Mao {
    private List<Carta> mao = new ArrayList<>();
    //metodo para limpar mão utilizando o clear
    public void limparMao(){
        this.mao.clear();
    }
    //metodo para adicionar cartas para a mão
    public void pegarCartaDoDeck(Baralho deck){
        mao.add(deck.pegarCarta());
    }
    //metodo onde calcula os pontos das cartas do baralho
    public int calcularPontos(){
        int pontos = 0;
        for(Carta carta : this.mao){
            pontos += carta.getValor();
        }
        return pontos;
    }
    //metodo onde mostra as mãos
    public String mostrarMao(){
        String output = "";
        for(Carta carta : mao){
            output += "\n" + carta;
        }
        return output;
    }
}