package jogadores;

import deck.Baralho;

public class Pessoa {
    
    private Mao mao = new Mao();
    
    public Pessoa( ){
    }
    //metodo get da mao
    public Mao getMao(){ return this.mao;}
    //metodo set da mao
    public void setMao(Mao mao){ this.mao = mao;}
    //metodo onde compra as cartas do baralhos
    public void comprarCarta(Baralho deck){
        this.mao.pegarCartaDoDeck(deck);
        this.mostrarMao();
    }
    //metodo onde mostra as mãos para o jogador
    public String mostrarMao(){
        return mao.mostrarMao();
    }
}