import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.IOException;

public class Java_Processes {
    public static void main(String[] args) {
        ArrayList<String[]> commands = new ArrayList<String[]>(5);

        // URLs a serem testadas via ping
        String[] URLs = {"google.com", "1236489", "youtube.com", "127.0.0.1", "sitenãoexiste321.com.br"};

        for (String url : URLs) {
            String[] command = {"ping", "-c", "4", url}; //Linux
            //String[] command = {"ping", url}; //Windows

            commands.add(command); // Cria um arraylist de Comandos PING a ser executados
        }

        try {
            // Criando os processos usando ProcessBuilder
            ArrayList<ProcessBuilder> processesBuilder = new ArrayList<ProcessBuilder>(5);
            for (String[] command : commands)
                processesBuilder.add(new ProcessBuilder(command)); // Instancia os processos
            
            ArrayList<Process> processes = new ArrayList<Process>(5);
            for(ProcessBuilder processBuilder : processesBuilder)
                processes.add(processBuilder.start()); // Inicializa os processos

            // Escuta a saida (stdout) de todos processos criados
            for (Process process : processes) {
                listenToProcess(process);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void listenToProcess(Process processo) {
        try {        
            // Captura a saída do processo
            BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()));
            String linha;

            System.out.println("\nSaída do processo:");
            // Lê e exibe a saída do comando
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha);
            }

            // Espera o processo terminar e captura o código de saída
            int codigoSaida = processo.waitFor();
            System.out.println("Processo finalizado com código: " + codigoSaida + "\n");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}