package com.example.cliente;

import com.example.modelo.Cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppConsole implements Runnable {

    Cliente cliente;

    public static void main(String[] args) {
        AppConsole console = new AppConsole();
        console.run();
    }

    @Override
    public void run() {

        try {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Digite o endereco IP");
            String end;

            end = inReader.readLine();

            cliente = new Cliente(end);

            System.out.println("Digite seu nome de usuario: ");
            cliente.getMensagem();

            while (true) {
                String msg;

                msg = inReader.readLine();
                cliente.sendMensagem(msg);

                if (msg.startsWith("/quit")) {
                    inReader.close();
                    cliente.shutdown();
                }

            }
        } catch (IOException e) {
            System.out.println("O server esta fora do ar no momento. Tente novamente mais tarde!");
            cliente.shutdown();
        }
    }

}
