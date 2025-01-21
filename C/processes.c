#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>

int main() {
    pid_t pid = fork();  // Cria um novo processo

    if (pid == 0) {
        // Processo Filho
        printf("Este é o processo filho com PID: %d\n", getpid());
    } else if (pid > 0) {
        // Processo Pai
        printf("Este é o processo pai com PID: %d, e o PID do filho é: %d\n", getpid(), pid);
    } else {
        // Se o fork falhar
        perror("Falha ao criar o processo");
    }

    return 0;
}