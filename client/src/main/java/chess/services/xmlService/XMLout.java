package chess.services.xmlService;

import chess.Client;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bobnewmark on 22.01.2017
 */
public class XMLout {

    private Client host;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private DocumentBuilder db;
    private Document doc;

    public XMLout(Client client) {
        host = client;
    }

    public void send(Document tosend) throws TransformerConfigurationException, IOException {
        XMLOutputStream out = new XMLOutputStream();

        StreamResult sr = new StreamResult(out);
        DOMSource ds = new DOMSource(tosend);
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        try {
            tf.transform(ds, sr);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        out.send();
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
            flush();
            reset();
        }
    }

    public void sendMessage(List<String> message) throws ParserConfigurationException, IOException, TransformerConfigurationException {
        db = dbf.newDocumentBuilder();
        doc = db.newDocument();

        ArrayList<String> list = (ArrayList<String>) message;

        Element root = doc.createElement("root");
        root.setAttribute("function", list.get(0));
        doc.appendChild(root);
        Element args = doc.createElement("args");
        root.appendChild(args);
        for (int i = 1; i < list.size(); i++) {
            Element el = doc.createElement(String.valueOf(i));
            el.appendChild(doc.createTextNode(list.get(i)));
            args.appendChild(el);
        }
        send(doc);
    }
}
