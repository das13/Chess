package chess;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientMain
{
    public static void main( String[] args ) throws IOException {
        System.out.println("vvvvvv");
        String sentence = "sxsdfsd";
        String modifiedSentence;
        Socket clientSocket=null;
        int i=0;
        while(i<2){
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            clientSocket = new Socket("localhost", 2543);
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Ready");
            //sentence = in.readLine();
            System.out.println(sentence);
            out.writeUTF(sentence);
            //modifiedSentence = in.readLine();

            i++;
        }
        clientSocket.close();
    }
}
