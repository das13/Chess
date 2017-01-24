package chess;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import org.w3c.dom.Document;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
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
            socket = new Socket(ip, 2543);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            xmLin = new XMLin(this);
            xmLout = new XMLout(this);
            out.println(scan.nextLine());
            Resender resend = new Resender();
            resend.start();
            String str = "";
            while (!str.equals("exit")) {
                str = scan.nextLine();
                out.println(str);
                if ("FREEPLAYERS".equals(str)) {
                    Document doc = xmLin.receive();
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
            System.err.println("Threads did not close!");
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
                System.err.println("Exception when trying connection");
                e.printStackTrace();
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