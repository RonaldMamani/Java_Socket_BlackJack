package Servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import deck.Baralho;
import jogadores.Jogador;
import utils.Configuracao;
import utils.NivelDificuldade;

public class Servidor {

    private static ServerSocket servidor;
    private static Socket cliente;
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static Baralho deck;

    private static double valorAposta;
    private static int ganhou;
    private static int perdeu;
    private static double dinheiroGanho;
    private static double dinheiroPerdido;
    private static boolean desistiu;

    private static Jogador jogador;
    private static Jogador dealer;

    public static void main(String[] args) throws Exception {

        // Inicia o servidor
        inicializarServidor();

        // Deixa o servidor rodando
        while (true) {
            try { // Try usado para verificar se o cliente desconectou do servidor 

                // Espera um cliente entrar no servidor
                aguardarCliente();
                output.writeObject("Você foi conectado com sucesso.");
                output.writeObject("\n--------------- Bem Vindo ao Jogo Black Jack 21 ---------------\n");
                ganhou = 0;
                perdeu = 0;
                dinheiroGanho = 0;
                dinheiroPerdido = 0;
                // Configuração inicial de todo jogo
                configurarJogo();

                // Jogo começa aqui
                while (true) {
                    // Inicializa configuração da partida
                    iniciarPartida();
                    desistiu = false;

                    // Recebe do cliente o valor da aposta
                    valorAposta = input.readDouble();
                    System.out.println("O cliente está apostando R$ " + valorAposta);

                    // Manda para o cliente a mão inicial
                    output.writeObject(mostrarMaoAtual());
                    try {
                        // Loop para verificar a ação do cliente
                        while (true) {

                            exibirAcoesDisponiveis();

                            // Recebe ação do cliente
                            String acao = (String) input.readObject();
                            System.out.println("O jogador escolheu a opção: [" + acao + "]");

                            boolean fazerOutraAcao = realizarAcaoDoCliente(acao);

                            if (!fazerOutraAcao) {
                                break;
                            }
                        }

                        if (desistiu) {
                            //CÓDIGO DA DESISTENCIA
                            valorAposta = (valorAposta / 2);
                            jogador.perderDinheiro(valorAposta);
                            perdeu++;
                            dinheiroPerdido += valorAposta;
                        } else {
                            
                            verificarVencedor();
                        }
                        
                        // Manda a quantia de dinheiro disponivel para verificar se pode jogar novamente
                        output.writeDouble(jogador.getDinheiroDisponivel());
                        output.flush();

                        if (jogador.getDinheiroDisponivel() == 0) {
                            String mensagem = "\nVocê não tem mais dinheiro para jogar.\n";
                            mensagem += mensagemFinalizacao();
                            output.writeObject(mensagem);
                            break;
                        }
                        // Mostra a opção de visualizar as cartas
                        mostrarCartasFinais();
                        // Mostra a op~]ap de visualizar o placar
                        visualizarPlacar();

                        // Receber resposta se quer jogar novamente
                        boolean jogarNovamente = input.readBoolean();
                        if (!jogarNovamente) {
                            System.out.println("O jogador decidiu parar de jogar.");
                            String mensagem = mensagemFinalizacao();
                            output.writeObject(mensagem);
                            output.flush();
                            break;
                        }
                        System.out.println("O jogador decidiu jogar novamente.");

                        reiniciarPartida();

                    } catch (Exception e) {
                        System.out.println("Cliente saiu da partida.");
                        cliente.close();
                        output.close();
                        input.close();
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("Cliente se desconectou.");
                cliente.close();
                output.close();
                input.close();
                continue;
            }
        }
    }

    private static void verificarVencedor() throws Exception{
        // Verificar quem ganhou a partida
        if (jogador.getMao().calcularPontos() > 21) {
            output.writeObject("\nVocê estourou!");
            perdeu++;
            dinheiroPerdido += valorAposta;
            output.flush();
            jogador.perderDinheiro(valorAposta);
            System.out.println("O cliente perdeu e a casa ganhou R$ " + valorAposta);
        } else {

            while (dealer.getMao().calcularPontos() < 17) {
                dealer.comprarCarta(deck);
            }

            if (dealer.getMao().calcularPontos() > 21) {
                String mensagem = "";
                mensagem += "\nO dealer estourou e você ganhou R$ " + valorAposta;
                ganhou++;
                dinheiroGanho += valorAposta;
                output.writeObject(mensagem);
                jogador.ganharDinheiro(valorAposta);
                System.out.println("O cliente venceu e conseguiu R$ " + valorAposta);
            } else if (dealer.getMao().calcularPontos() > jogador.getMao().calcularPontos()) {
                output.writeObject("\nDealer venceu com " + dealer.getMao().calcularPontos() + " pontos.");
                perdeu++;
                dinheiroPerdido += valorAposta;
                jogador.perderDinheiro(valorAposta);
                System.out.println("O cliente perdeu e a casa ganhou R$ " + valorAposta);
            } else if (jogador.getMao().calcularPontos() > dealer.getMao().calcularPontos()) {
                output.writeObject("\nVocê ganhou com " + jogador.getMao().calcularPontos() + " pontos.");
                ganhou++;
                dinheiroGanho += valorAposta;
                jogador.ganharDinheiro(valorAposta);
                System.out.println("O cliente venceu e conseguiu R$ " + valorAposta);
            } else if(jogador.getMao().calcularPontos() == dealer.getMao().calcularPontos()){
                // EMPATE
                output.writeObject("\nDeu empate, Todos os Valores Apostados serão Devolvidos");
                output.flush();
                System.out.println("Ocorreu um empate.");
            }
        }
        output.flush();
    }

    private static void visualizarPlacar() throws Exception {
        boolean visualizar = input.readBoolean();
        if (visualizar) {
            System.out.println("O jogador escolheu ver tentativas.");
            output.writeObject(mostrarPlacar());
            output.flush();
        }
    }

    private static String mostrarPlacar() {
        String mensagem = "";
        mensagem += "\nVocê ganhou " + ganhou + " partida(s) e Ganhou R$ " + dinheiroGanho + ".";
        mensagem += "\nVocê perdeu " + perdeu + " partida(s) e Perdeu R$ " + dinheiroPerdido + ".";
        return mensagem;
    }

    private static String mensagemFinalizacao() {
        String mensagem = "";
        mensagem += mostrarPlacar();
        mensagem += "\n\nObrigado por jogar.";
        System.out.println("\n--------------------------------------------");
        System.out.println("Resumo das Tentativas:");
        System.out.println("A casa ganhou " + perdeu + " partida(s) e Ganhou R$ " + dinheiroPerdido + ".");
        System.out.println("A casa perdeu " + ganhou + " partida(s) e Perdeu R$ " + dinheiroGanho + ".\n");
        return mensagem;
    }

    private static void mostrarCartasFinais() throws Exception {
        String resposta = input.readObject().toString();
        if (resposta.contains("sim")) {
            System.out.println("O jogador escolheu ver as cartas do jogo.");
            String mensagem = "";
            mensagem += "\n\nAs suas cartas eram:";
            mensagem += "\n" + jogador.mostrarMao();
            mensagem += "\n\nAs cartas do Dealer eram:";
            mensagem += "\n" + dealer.mostrarMao();
            mensagem += "\n\nAs cartas seguintes do baralho eram:";
            mensagem += "\n" + deck.mostrarCartas();
            output.writeObject(mensagem);
        }
    }

    private static void reiniciarPartida() {
        jogador.getMao().limparMao();
        dealer.getMao().limparMao();
        System.out.println("\nIniciando nova rodada");
    }

    private static boolean realizarAcaoDoCliente(String acao) throws Exception {
        // Verificar tipo de ação e realizá-la
        if (acao.equals("comprar")) {
            boolean estourou = realizarAcaoComprar();
            if(estourou){
                return false;
            }
        } else if (acao.equals("mostrar")) {
            realizarAcaoMostrar();
        } else if (acao.equals("parar")) {
            realizarAcaoParar();
            return false;
        } else if (acao.equals("dobrar aposta")) {
            realizarAcaoDobrar();
        } else if (acao.equals("desistir")) {
            realizarAcaoDesistir();
            return false;
        }
        return true;
    }

    private static boolean realizarAcaoComprar() throws Exception {
        jogador.comprarCarta(deck);
        output.writeObject(mostrarMaoAtual());
        output.flush();
        if (jogador.getMao().calcularPontos() > 21) {
            output.writeObject("estourou");
            return true;
        } else {
            output.writeObject("continuar");
            output.flush();
        }
        return false;
    }

    private static void realizarAcaoMostrar() throws Exception {
        output.writeObject(mostrarCartasDoJogo());
        output.flush();
    }

    private static void realizarAcaoDobrar() throws Exception{
        valorAposta *= 2;
        String mensagem = "";
        mensagem += "\nVocê dobrou a aposta que agora vale + R$ "+valorAposta+".";
        output.writeObject(mensagem);
        output.flush();
    }

    private static void realizarAcaoParar() throws Exception {
        output.writeObject("Você decidiu parar.");
        output.flush();
    }

    private static void realizarAcaoDesistir() throws Exception {
        String mensagem = "";
        mensagem += "\nVocê desistiu e o Dealer ganhou.";
        mensagem += "\nVocê recebeu 50% (R$ "+ (valorAposta / 2) +") de volta da sua aposta total (R$ "+valorAposta+").";
        output.writeObject(mensagem);
        output.flush();
        desistiu = true;
    }

    public static String mostrarMaoAtual() {
        String retorno = "";
        retorno += "\nSua mão atual é: ";
        retorno += "\n" + jogador.mostrarMao();
        retorno += "\n\nSua pontuação atual é: " + jogador.getMao().calcularPontos();
        return retorno;
    }

    public static String mostrarCartasDoJogo() {
        String retorno = "";
        retorno += "\nAs suas cartas são:";
        retorno += "\n" + jogador.mostrarMao();
        retorno += "\n\nAs cartas do Dealer são:";
        retorno += "\n" + dealer.mostrarMao();
        return retorno;
    }

    public static void exibirAcoesDisponiveis() throws Exception {
        String acoes = "\nO que você deseja fazer? 1) Comprar, 2) Mostrar cartas, 3) Parar, 4) Dobrar aposta ou 5) Desistir";
        output.writeObject(acoes);
        output.flush();
    }

    public static void iniciarPartida() throws IOException {
        deck = new Baralho();
        distribuirCartasIniciais();
        output.writeDouble(jogador.getDinheiroDisponivel());
        output.flush();
    }

    public static void distribuirCartasIniciais() {
        // Cartas iniciais para o jogador
        jogador.comprarCarta(deck);
        jogador.comprarCarta(deck);

        // Cartas iniciais para o Dealer
        dealer.comprarCarta(deck);
        dealer.comprarCarta(deck);
    }

    public static void configurarJogo() throws Exception {
        int nivelDificuldade = input.readInt();

        double quantiaDisponivel = NivelDificuldade.quantiaDinheiroDisponivel(nivelDificuldade);

        jogador = new Jogador(quantiaDisponivel);
        dealer = new Jogador();
    }

    public static void aguardarCliente() {
        try {
            System.out.println("Aguardando cliente se conectar.\n");
            cliente = servidor.accept();

            // Pega Input e Output do cliente
            output = new ObjectOutputStream(cliente.getOutputStream());
            ;
            input = new ObjectInputStream(cliente.getInputStream());
            System.out.println("Cliente " + cliente.getInetAddress().getHostName() + " conectado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao conectar o cliente.");
        }
    }

    public static void inicializarServidor() {
        try {
            System.out.println("Inicializando o servidor.");
            servidor = new ServerSocket(Configuracao.PORTA);
            System.out.println("Servidor inicializado.");
        } catch (IOException e) {
            System.out.println("Não foi possível inicializar o servidor.");
            System.exit(1);
        }
    }
}