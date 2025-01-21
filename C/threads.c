#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

int contador = 0;  // Variável compartilhada

pthread_mutex_t lock;

void* incrementar(void* arg) {
    // Adquire o bloqueio (mutex) para garantir acesso exclusivo
    pthread_mutex_lock(&lock);

    int id =  *(int*)arg;
    for (int i = 0; i < 5; i++) {
        contador++;  // Incrementa o contador compartilhado
        printf("Thread %d, contador: %d\n", id, contador);
    }

    // Libera o bloqueio
    pthread_mutex_unlock(&lock);
    return NULL;
}

int main() {
    pthread_t threads[2];
    int thread_id[2] = {1, 2};
    
    // Inicializa o mutex
    pthread_mutex_init(&lock, NULL);

    // Cria duas threads
    for (int i = 0; i < 2; i++) {
        pthread_create(&threads[i], NULL, incrementar, &thread_id[i]);
    }

    // Aguarda as threads terminarem
    for (int i = 0; i < 2; i++) {
        pthread_join(threads[i], NULL);
    }

    // Finaliza o mutex
    pthread_mutex_destroy(&lock);

    printf("Contador final: %d\n", contador);
    return 0;
}