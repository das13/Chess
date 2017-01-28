package chess.services.xmlService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobnewmark on 22.01.2017
 */
public class XMLout {

    private DataOutputStream host;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private DocumentBuilder db;
    private Document doc;

    public XMLout(OutputStream out) {
        host = new DataOutputStream(out);
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
            this.out = host;
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
            //System.out.println(list.get(i));
            Element el = doc.createElement("arg");
            el.appendChild(doc.createTextNode(list.get(i)));
            args.appendChild(el);
        }
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File("output.xml"));
        Source input = new DOMSource(doc);
        try {
            transformer.transform(input, output);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        send(doc);
    }
}
