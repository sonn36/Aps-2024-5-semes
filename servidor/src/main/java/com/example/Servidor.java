package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor implements Runnable {

    private ArrayList<ConnectionHandler> listaConexoes;
    private ServerSocket servidor;
    private boolean pronto;
    private ExecutorService pool;

    public Servidor() {
        listaConexoes = new ArrayList<ConnectionHandler>();
        pronto = false;
    }

    @Override
    public void run() {

        try {

            servidor = new ServerSocket(3636);
            pool = Executors.newCachedThreadPool();

            System.out.println("---------------------------------------");
            System.out.println("----Servidor iniciado na porta 3636----");
            System.out.println("---------------------------------------");

            while (!pronto) {
                Socket cliente = servidor.accept();
                ConnectionHandler handler = new ConnectionHandler(cliente);
                listaConexoes.add(handler);
                pool.execute(handler);
            }

        } catch (IOException e) {
            shutdown();
        }

    }

    public void broadcast(String msg) {
        for (ConnectionHandler ch : listaConexoes) {
            if (ch != null) {
                ch.sendMensagem(msg);
            }
        }
    }

    private void shutdown() {

        try {
            if (!servidor.isClosed()) {
                servidor.close();
                pool.shutdown();
            }
            pronto = true;

            for (ConnectionHandler ch : listaConexoes) {
                ch.shutdown();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class ConnectionHandler implements Runnable {

        private Socket cliente;

        private BufferedReader entrada;
        private PrintWriter saida;

        private String nickname;

        public ConnectionHandler(Socket cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {

            try {
                saida = new PrintWriter(cliente.getOutputStream(), true);
                entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                nickname = entrada.readLine();

                System.out.println(nickname + " Entrou.");
                broadcast(nickname + " entrou no chat");

                String msg;
                while ((msg = entrada.readLine()) != null) {

                    if (msg.startsWith("/nick ")) {

                        String[] msgSplit = msg.split(" ", 2);
                        if (msgSplit.length == 2 && !msgSplit[1].isEmpty() && msgSplit[1].length() > 3) {
                            broadcast(nickname + " mudou seu nome para " + msgSplit[1]);
                            nickname = msgSplit[1];
                        } else {
                            saida.println("nome invalido, tente outro.");
                        }
                    } else if (msg.startsWith("/quit")) {
                        shutdown();
                    } else {
                        broadcast(nickname + ": " + msg);
                    }
                }
            } catch (Exception e) {
                shutdown();
            }

        }

        public String getNickname() {
            return nickname;
        }

        public void sendMensagem(String msg) {
            saida.println(msg);
        }

        public void shutdown() {

            try {
                broadcast(nickname + " saiu do chat.");
                entrada.close();
                saida.close();
                if (!cliente.isClosed()) {
                    cliente.close();
                }
            } catch (Exception e) {
            }

        }

    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.run();
    }

}
