package com.example.modelo;

import com.example.interfaces.frmPrincipal;
import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Cliente {

    private Socket cliente;
    private BufferedReader entrada;
    private BufferedWriter saida;

    public Cliente(String enderecoIp) {
        try {
            cliente = new Socket(enderecoIp, 3636);

            saida = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

        } catch (Exception e) {
            shutdown();
        }
    }

    public void setNome(String nome) {

        try {
            saida.write("/nick " + nome);
            saida.newLine();
            saida.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getMensagem() {
        new Thread(() -> {
            while (cliente.isConnected()) {
                try {

                    String msg = entrada.readLine();
                    System.out.println(msg);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void getMensagemToPanel(JPanel panel) {
        new Thread(() -> {
            while (cliente.isConnected()) {
                try {

                    String[] info = entrada.readLine().split(": ", 2);
                    if (info.length > 1 && !info[1].isEmpty()) {
                        frmPrincipal.addMensagem(info, panel);

                    } else {
                        frmPrincipal.addNota(info[0], panel);
                    }

                } catch (IOException e) {
                    System.out.println("O server esta fora do ar no momento. Tente novamente mais tarde!");
                }
            }
        }).start();
    }

    public void sendMensagem(String mensagem) {
        try {
            saida.write(mensagem);
            saida.newLine();
            saida.flush();
        } catch (IOException e) {
            System.out.println("O server esta fora do ar no momento. Tente novamente mais tarde!");
        }
    }

    public void shutdown() {
        try {
            entrada.close();
            saida.close();
            if (!cliente.isClosed()) {
                cliente.close();
            }
        } catch (Exception ignored) {
        }
    }

}
