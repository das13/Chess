package chess.model;

import java.io.*;
import java.net.Socket;

/**
 * Created by Admin on 19.01.2017.
 */
public class MiniServer extends Thread {
    private Socket socket = null;

    public MiniServer(Socket socket) {

        super("MiniServer");
        this.socket = socket;

    }

    public void run(){
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            DataInputStream ds = new DataInputStream(is);
            DataOutputStream out= new DataOutputStream(os);
            String line = ds.readUTF(); // ожидаем пока клиент пришлет строку текста.
            System.out.println(line);
            System.out.println("The dumb client just sent me this line : " + line);
            System.out.println("I'm sending it back...");
            out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
            out.flush(); // заставляем поток закончить передачу данных.
            System.out.println("Waiting for the next line...");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
