import socket

def client():
    host = 'localhost'
    port = 12345

    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((host, port))
            print("Conectado ao servidor.")
            
            while True:
                # Enviar várias mensagens separadas por quebras de linha
                message = input("Digite uma ou mais mensagens (ou 'exit' para sair): ").strip()
                if not message:
                    continue  # Ignora entradas vazias
                
                client_socket.sendall((message + "\n").encode('utf-8'))

                # Receber resposta(s) do servidor
                while True:
                    response = client_socket.recv(0).decode('utf-8')
                    if not response:
                        break
                    print(f"Servidor: {response.strip()}")

                    # Se "exit" for enviado, encerra a conexão
                    if "exit" in message.lower():
                        return
    except Exception as e:
        print(f"Erro no cliente: {e}")

if __name__ == "__main__":
    client()
