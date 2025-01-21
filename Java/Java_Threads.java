import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

class EchoRequestHandler extends Thread {
    private Socket socket;
    private Semaphore semaphore;
    private int[] requestCount;  // Usando array para simular passagem por referência

    public EchoRequestHandler(Socket socket, Semaphore semaphore, int[] requestCount) {
        this.socket = socket;
        this.semaphore = semaphore;
        this.requestCount = requestCount;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String inputLine;
            System.out.println("Cliente conectado: " + socket.getRemoteSocketAddress());

            // Lê cada linha enviada pelo cliente
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recebido: " + inputLine);
                System.out.println("Processando...");

                 // Protege o incremento do contador usando o semáforo
                 try {
                    semaphore.acquire();  // Adquire o semáforo
                    requestCount[0]++;       // Incrementa o contador de requisições recebidas
                    System.out.println("Requisições recebidas até agora: " + requestCount[0]);
                } finally {
                    semaphore.release();  // Libera o semáforo
                }

                // Simulando processamento demorado (2 segundos)
                try {
                    Thread.sleep(2000); // Dorme por 2 segundos
                } catch (InterruptedException e) {
                    System.err.println("Thread interrompida: " + e.getMessage());
                }

                // Envia a resposta (eco) após a simulação de I/O e Processamento custoso
                out.println(String.format("Requisicao %d) Eco: %s", requestCount[0], inputLine));

                // Se o cliente enviar "exit", encerra a conexão
                if ("exit".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }

            System.out.println("Cliente desconectado: " + socket.getRemoteSocketAddress());
        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println("Erro ao fechar o socket: " + e.getMessage());
            }
        }
    }
}

public class Java_Threads {
    public static void main(String[] args) {
        int porta = 12345;
        Semaphore semaphore = new Semaphore(1);  // Semáforo para controlar acesso
        int[] requestCount = { 0 };  // Variável compartilhada, usada com array para passagem por referência

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Servidor escutando na porta " + porta);

            // Loop infinito para aceitar conexões de clientes
            while (true) {
                Socket clienteSocket = serverSocket.accept();  // Aguarda um cliente se conectar
                new EchoRequestHandler(clienteSocket, semaphore, requestCount).start(); // Cria uma nova thread para cada requisição
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}