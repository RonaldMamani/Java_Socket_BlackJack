package Cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

import Cliente.Configuracao;

public class Cliente {

    private static Socket cliente;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;
    private static Scanner scanner = new Scanner(System.in);
    private static double dinheiroDisponivel;
    private static double valorAposta;
    private static int nivel;
    private static boolean desistiu;

    public static void main(String[] args) throws Exception {

        // Faz conexao com o servidor
        fazerConexao();

        // Selecionar nível de dificuldade
        selecionarDificuldade();

        // Inicio do jogo
        while (true) {

            fazerAposta();
            desistiu = false;

            // Mostra a mão inicial
            System.out.println(input.readObject());

            try {
                // Mandar comandos
                while (true) {

                    // Input das ações disponiveis
                    int decisao = escolherTipodecisao();

                    // Verificar tipo de ação
                    boolean continuarJogo = fazerDecisao(decisao);
                    if (!continuarJogo) {
                        break;
                    }
                }
                if(desistiu){
                
                } else{
                    exibirVencedor();
                
                }

                boolean possuiDinheiro = verificarDinheiroDisponivel();
                if(!possuiDinheiro){
                    break;
                }
                
                // Mostra cartas no fim do jogo
                if(visualizarCartasFinais()){
                    System.out.println(input.readObject());
                }
                
                if(visualizarPlacar()){
                    System.out.println(input.readObject());
                }

                // Perguntar se quer jogar novamente
                if (!jogarNovamente()) {
                    System.out.println(input.readObject());
                    break;
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ocorreu um erro com o Input ou Output do cliente.");
            }

        }
    }
    
    public static boolean visualizarPlacar() throws Exception{
        while(true){
            System.out.println("\nGostaria de ver o numero de tentativas e quanto gastou ou faturou? 1) Sim ou 2) Não");
            try{
                int resposta = scanner.nextInt();
                if(resposta == 1){
                    output.writeBoolean(true);
                    output.flush();
                    return true;
                } else if(resposta == 2){
                    output.writeBoolean(false);
                    output.flush();
                    return false;
                }
            }catch(Exception e){
                System.out.println("Digite um valor válido.");
                scanner.next();
            }
        }
    }
    
    public static boolean visualizarCartasFinais() throws Exception{
        while(true){
            System.out.println("\nQuer visualizar as cartas da partida?1) Sim ou 2) Não ");
            try{
                int resposta = scanner.nextInt();
                if(resposta == 1){
                    output.writeObject("sim");
                    output.flush();
                    return true;
                } else if(resposta == 2){
                    output.writeObject("nao");
                    output.flush();
                    return false;
                }
            } catch(Exception e){
                System.out.println("Digite um valor válido.");
                scanner.next();
            }
        }
    }

    public static boolean verificarDinheiroDisponivel() throws IOException, ClassNotFoundException{
        double dinheiroDisponivel = input.readDouble();
        if (dinheiroDisponivel == 0) {
            System.out.println(input.readObject());
            return false;
        } 
        return true;
    }

    public static void exibirVencedor() throws ClassNotFoundException, IOException {
        // Recebe mensagem de quem perdeu ou ganhou
        String vencedor = (String) input.readObject();
        System.out.println(vencedor);
    }

    public static boolean jogarNovamente() throws IOException {
        while (true) {

            System.out.println("\nDeseja jogar novamente? 1) Sim ou 2) Não");
            try {
                int resposta = scanner.nextInt();
                if (resposta == 1) {
                    output.writeBoolean(true);
                    output.flush();
                    return true;
                } else if (resposta == 2) {
                    output.writeBoolean(false);
                    output.flush();
                    return false;
                }
            } catch (InputMismatchException e) {
                scanner.next();
            }
        }
    }

    public static boolean fazerDecisao(int decisao) throws IOException, ClassNotFoundException {
        System.out.println("[APOSTA R$ "+ valorAposta + "] | [NIVEL " + nivel + "] | [DINHEIRO R$ " + dinheiroDisponivel+ "]\n");
        if (decisao == 1) {
            // Comprar cartas
            output.writeObject("comprar");
            System.out.println(input.readObject());
            if(input.readObject().toString().equals("estourou")){
                return false;
            }
        } else if (decisao == 2) {
            output.writeObject("mostrar");
            System.out.println(input.readObject());
        } else if (decisao == 3) {
            // Parar
            output.writeObject("parar");
            System.out.println(input.readObject());
            output.flush();
            return false;
        } else if(decisao == 4){
            output.writeObject("dobrar aposta");
            System.out.println(input.readObject());
        } else if(decisao == 5){
            output.writeObject("desistir");
            System.out.println(input.readObject());
            desistiu = true;
            return false;
        }
        output.flush();
        return true;
    }

    public static int escolherTipodecisao() throws ClassNotFoundException, IOException {
        // Input das ações disponiveis

        String acoesDisponiveis = (String) input.readObject();

        while(true){
            System.out.println(acoesDisponiveis);
            try{
                int decisao = scanner.nextInt();
                if(decisao >= 1 && decisao <= 5){
                    
                    if(decisao == 4){
                        if((valorAposta * 2) > dinheiroDisponivel){
                            System.out.println("Você não tem dinheiro suficiente para dobrar.");
                            continue;
                        } else{
                            valorAposta *= 2;
                        }
                    }
                    
                    return decisao;
                } else{
                    System.out.println("Digite um valor válido.");
                }
            } catch(InputMismatchException e){
                System.out.println("Digite apenas números.");
                scanner.next();
                }
            }        
    }

    public static void fazerAposta() throws IOException {

        dinheiroDisponivel = input.readDouble();
        valorAposta = 0;
        
        while (true) {
            System.out.println("\nVocê tem R$ " + dinheiroDisponivel + " disponível. Quanto deseja apostar?");
            try {

                valorAposta = scanner.nextDouble();

                if (valorAposta > dinheiroDisponivel) {
                    System.out.println("Você não pode apostar mais do que você tem.");
                } else if (valorAposta <= dinheiroDisponivel) {
                    output.writeDouble(valorAposta);
                    output.flush();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Digite um valor válido.");
                scanner.next();
            }
        }
    }

    public static void selecionarDificuldade() throws IOException {
        while (true) {

            System.out.println("Esolha o nível de dificuldade: 1) Fácil, 2) Médio ou 3) Difícil");

            try {
                nivel = scanner.nextInt();
                if (nivel == 1 || nivel == 2 || nivel == 3) {
                    output.writeInt(nivel);
                    output.flush();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, digite apenas um número.");
                scanner.next();
            } 
        }
    }

    public static boolean fazerConexao() throws ClassNotFoundException {
        try {
            System.out.println("Tentando fazer conexão com o servidor.");

            cliente = new Socket(
                    Configuracao.ENDERECO,
                    Configuracao.PORTA);

            // Pega o Input e Output do cliente
            input = new ObjectInputStream(cliente.getInputStream());
            output = new ObjectOutputStream(cliente.getOutputStream());

            System.out.println(input.readObject());
            System.out.println(input.readObject());
            return true;

        } catch (IOException e) {

            System.out.println("Não foi possível conectar ao servidor.");
            System.out.println(e.getMessage());
            System.exit(1);

            return false;
        }
    }

}