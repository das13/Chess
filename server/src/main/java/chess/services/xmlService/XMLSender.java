package chess.services.xmlService;

import chess.Server;
import chess.ServerMain;
import chess.controller.Controller;
import chess.model.Player;
import chess.model.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobnewmark on 21.01.2017
 */
public class XMLSender {

    private Controller host;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private DocumentBuilder db;
    private Document doc;

    public XMLSender(Controller controller) {
        host = controller;
    }
    private class XMLOutputStream extends ByteArrayOutputStream {

        private DataOutputStream out;

        XMLOutputStream() {
            super();
            this.out = host.getOutput();
        }

        void send() throws IOException {
            byte[] data = toByteArray();
            out.writeInt(data.length);
            out.write(data);
            reset();
        }
    }


    //sends Document to the OutputStream
    private void send(Document doc) throws TransformerConfigurationException, IOException {

        XMLOutputStream out = new XMLOutputStream();
        StreamResult sr = new StreamResult(out);
        DOMSource ds = new DOMSource(doc);
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        try {
            tf.transform(ds, sr);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        out.send();
    }

    public void send(List<String> message) throws ParserConfigurationException, IOException, TransformerConfigurationException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        List<String> list = message;

        Element root = doc.createElement("root");
        root.setAttribute("function", list.get(0));
        doc.appendChild(root);
        Element args1 = doc.createElement("args");
        root.appendChild(args1);
        for (int i = 1; i < list.size(); i++) {
            Element el = doc.createElement("arg");
            el.appendChild(doc.createTextNode(list.get(i)));
            args1.appendChild(el);
        }
        send(doc);
    }

//    public void sendFreePlayers() throws ParserConfigurationException, IOException, TransformerConfigurationException {
//        db = dbf.newDocumentBuilder();
//        doc = db.newDocument();
//        Element root = doc.createElement("freePlayers");
//        doc.appendChild(root);
//        Element memberList = doc.createElement("players");
//        root.appendChild(memberList);
//        List<Player> temp = ServerMain.getFreePlayers();
//        for (int i = 0; i < temp.size(); i++) {
//            Player player = temp.get(i);
//            if (player.getStatus() == Status.FREE) {
//                Element member = doc.createElement("player");
//                memberList.appendChild(member);
//                Element nickname = doc.createElement("nickname");
//                nickname.appendChild(doc.createTextNode(player.getNickname()));
//                member.appendChild(nickname);
//                Element rank = doc.createElement("rank");
//                rank.appendChild(doc.createTextNode(String.valueOf(player.getRank())));
//                member.appendChild(rank);
//            }
//        }
//        send(doc);
//    }
}