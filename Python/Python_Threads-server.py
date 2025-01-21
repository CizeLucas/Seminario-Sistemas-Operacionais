import socket
import threading
import time
from queue import PriorityQueue
from threading import Semaphore

class EchoRequestHandler(threading.Thread):
    def __init__(self, semaphore, request_count, priority_queue):
        super().__init__()
        self.semaphore = semaphore
        self.request_count = request_count
        self.priority_queue = priority_queue

    def run(self):
        while True:
            # Obtém a mensagem de menor tamanho da fila
            try:
                priority, (arrival_order, client_socket, client_address, message) = self.priority_queue.get(timeout=1)
            except:
                continue  # Evita bloqueio caso a fila esteja vazia

            try:
                print(f"Processando mensagem de {client_address} (Chegada #{arrival_order}): {message} (prioridade {priority})")

                # Incrementa o contador de requisições de forma protegida
                with self.semaphore:
                    self.request_count[0] += 1
                    request_id = self.request_count[0]

                # Simula processamento demorado
                time.sleep(2)

                # Envia a resposta ao cliente
                response = f"Requisicao {request_id} (Chegada #{arrival_order})) Eco: {message}\n"
                client_socket.sendall(response.encode('utf-8'))

                # Fecha a conexão se a mensagem for "exit"
                if message.lower() == "exit":
                    print(f"Cliente desconectado: {client_address}")
                    client_socket.close()
            except Exception as e:
                print(f"Erro ao processar mensagem: {e}")

class PythonThreadServer:
    def __init__(self, port):
        self.port = port
        self.semaphore = Semaphore(1)  # Semáforo para proteger o contador de requisições
        self.request_count = [0]  # Contador de requisições (passagem por referência simulada)
        self.priority_queue = PriorityQueue()  # Fila de prioridade para mensagens
        self.arrival_counter = 0  # Contador global para ordem de chegada

    def start(self):
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
                server_socket.bind(('', self.port))
                server_socket.listen()
                print(f"Servidor escutando na porta {self.port}")

                # Inicia a thread responsável por processar mensagens
                processor_thread = EchoRequestHandler(self.semaphore, self.request_count, self.priority_queue)
                processor_thread.daemon = True
                processor_thread.start()

                while True:
                    client_socket, client_address = server_socket.accept()
                    print(f"Cliente conectado: {client_address}")

                    # Lê as mensagens do cliente
                    threading.Thread(target=self.handle_client, args=(client_socket, client_address)).start()
        except Exception as e:
            print(f"Erro no servidor: {e}")

    def handle_client(self, client_socket, client_address):
        buffer = ""
        try:
            while True:
                data = client_socket.recv(1024).decode('utf-8')
                if not data:
                    break

                buffer += data
                while '\n' in buffer:
                    message, buffer = buffer.split('\n', 1)
                    message = message.strip()

                    if not message:
                        continue

                    # Incrementa o contador de chegada
                    self.arrival_counter += 1
                    arrival_order = self.arrival_counter

                    # Adiciona a mensagem na fila de prioridade (prioridade = tamanho da mensagem)
                    self.priority_queue.put((len(message), (arrival_order, client_socket, client_address, message)))
        except Exception as e:
            print(f"Erro na comunicação com {client_address}: {e}")
        finally:
            client_socket.close()

if __name__ == "__main__":
    server = PythonThreadServer(12345)
    server.start()
