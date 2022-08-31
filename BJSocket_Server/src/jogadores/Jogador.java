package jogadores;

public class Jogador extends Pessoa{

    private double dinheiroDisponivel;
    
    public Jogador(){}
    //construtor do dinheiroDisponivel
    public Jogador(double dinheiroDisponivel){
        this.dinheiroDisponivel = dinheiroDisponivel;
    }
    //metodo para calcular o quanto perde de dinheiro
    public void perderDinheiro(double quantia){
        this.dinheiroDisponivel -= quantia;
    }
    //metodo para calcular o quanto ganhou de dinheiro
    public void ganharDinheiro(double quantia){
        this.dinheiroDisponivel += quantia;
    }
    //metodo get do dinheiroDisponivel
    public double getDinheiroDisponivel(){ return this.dinheiroDisponivel;}

}