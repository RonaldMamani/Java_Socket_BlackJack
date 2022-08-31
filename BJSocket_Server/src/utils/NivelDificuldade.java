package utils;

public abstract class NivelDificuldade {
    //Classe onde estara a quantidade de dinheiro para cada dificuldade que são: Facil(100), Normal(25) e Dificil(1)
    public static double quantiaDinheiroDisponivel(int nivel){
        if(nivel == 1){
            return 100;
        } else if(nivel == 2){
            return 25;
        } else {
            return 1;
        }
    }
}
