package chess;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private XMLin xmLin;
    private XMLout xmLout;

    public Client() {
        Scanner scan = new Scanner(System.in);
        String ip = "localhost";
        try {
            socket = new Socket(ip, 2544);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            out.println(scan.nextLine());
            Resender resend = new Resender();
            resend.start();
            String str = "";
            while (!str.equals("exit")) {
                str = scan.nextLine();
                out.println(str);
                if ("FREEPLAYERS".equals(str)) {
                    //Document doc = xmLin.receive();
                }
            }
            resend.setStop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            ClientMain.logger.error("Threads on Client did not close", e);
        }
    }

    private class Resender extends Thread {

        private boolean stoped;
        public void setStop() {
            stoped = true;
        }

        @Override
        public void run() {
            try {
                while (!stoped) {
                    String str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                ClientMain.logger.error("Connection error on Client", e);
            }
        }
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
        return output;
    }
}