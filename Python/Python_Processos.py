import multiprocessing
import time
import random

def task(num):
    print(f"Processo {num} iniciado")
    sleepTime = random.randint(1, 4)
    time.sleep(sleepTime)  # Simula tempo de processamento

    if num == 1:  # Vamos provocar um erro no processo 2
        raise Exception(f"Erro fatal no processo {num}")

    print(f"Processo {num} concluido com sucesso (dormiu por {sleepTime} Segundos)")

if __name__ == "__main__":
    processes = []
    for i in range(4):
        process = multiprocessing.Process(target=task, args=(i,))
        processes.append(process)
        process.start()

    for process in processes:
        process.join()

    print("Todos os processos finalizaram (com ou sem falhas)")
