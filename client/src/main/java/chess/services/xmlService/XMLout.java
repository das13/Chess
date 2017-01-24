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

        public XMLOutputStream() {
            super();
            this.out = host.getOutput();
        }

        public void send() throws IOException {
            byte[] data = toByteArray();
            out.writeInt(data.length);
            out.write(data);
            flush();
            reset();
        }
    }

    public void sendMessage(String... arguments) throws ParserConfigurationException, IOException, TransformerConfigurationException {
        db = dbf.newDocumentBuilder();
        doc = db.newDocument();
        String arg0 = arguments[0];
        if ("reg".equals(arg0)) {
            if (arguments.length != 4) {
                System.out.println("WRONG INPUT!");
                // когда будет view, необходимость этой проверки, скорее всего отпадет
            } else {
                Element root = doc.createElement(arg0);
                doc.appendChild(root);
                Element args = doc.createElement("args");
                root.appendChild(args);
                Element login = doc.createElement("login");
                login.appendChild(doc.createTextNode(arguments[1]));
                args.appendChild(login);
                Element password = doc.createElement("password");
                password.appendChild(doc.createTextNode(arguments[2]));
                args.appendChild(password);
                Element ipadress = doc.createElement("ipadress");
                ipadress.appendChild(doc.createTextNode(arguments[3]));
                args.appendChild(ipadress);
            }

        } else if ("profile".equals(arg0)) {
            if (arguments.length != 4) {
                System.out.println("WRONG INPUT!");
                // когда будет view, необходимость этой проверки, скорее всего отпадет
            } else {
                Element root = doc.createElement(arg0);
                doc.appendChild(root);
                Element args = doc.createElement("args");
                root.appendChild(args);
                Element login = doc.createElement("login");
                login.appendChild(doc.createTextNode(arguments[1]));
                args.appendChild(login);
                Element password = doc.createElement("password");
                password.appendChild(doc.createTextNode(arguments[2]));
                args.appendChild(password);
                Element ipadress = doc.createElement("ipadress");
                ipadress.appendChild(doc.createTextNode(arguments[3]));
                args.appendChild(ipadress);
            }
        } else if ("auth".equals(arg0)) {
            if (arguments.length != 4) {
                System.out.println("WRONG INPUT!");
                // когда будет view, необходимость этой проверки, скорее всего отпадет
            } else {
                Element root = doc.createElement(arg0);
                doc.appendChild(root);
                Element args = doc.createElement("args");
                root.appendChild(args);
                Element login = doc.createElement("login");
                login.appendChild(doc.createTextNode(arguments[1]));
                args.appendChild(login);
                Element password = doc.createElement("password");
                password.appendChild(doc.createTextNode(arguments[2]));
                args.appendChild(password);
                Element ipadress = doc.createElement("ipadress");
                ipadress.appendChild(doc.createTextNode(arguments[3]));
                args.appendChild(ipadress);
            }
        } else if ("move".equals(arg0)) {
            if (arguments.length != 4) {
                System.out.println("WRONG INPUT!");
                // когда будет view, необходимость этой проверки, скорее всего отпадет
            } else {
                Element root = doc.createElement(arg0);
                doc.appendChild(root);
                Element args = doc.createElement("args");
                root.appendChild(args);
                Element fromCell = doc.createElement("fromCell");
                fromCell.appendChild(doc.createTextNode(arguments[1]));
                args.appendChild(fromCell);
                Element toCell = doc.createElement("toCell");
                toCell.appendChild(doc.createTextNode(arguments[2]));
                args.appendChild(toCell);
                Element timer = doc.createElement("timer");
                timer.appendChild(doc.createTextNode(arguments[3]));
                args.appendChild(timer);
            }
        } else if ("gameOver".equals(arg0)) {
            if (arguments.length != 3) {
                System.out.println("WRONG INPUT!");
                // когда будет view, необходимость этой проверки, скорее всего отпадет
            } else {
                Element root = doc.createElement(arg0);
                doc.appendChild(root);
                Element args = doc.createElement("args");
                root.appendChild(args);
                Element reason = doc.createElement("reason");
                reason.appendChild(doc.createTextNode(arguments[1]));
                args.appendChild(reason);
                Element timer = doc.createElement("timer");
                timer.appendChild(doc.createTextNode(arguments[2]));
                args.appendChild(timer);
            }
        } else if ("offerDraw".equals(arg0)) {
            if (arguments.length != 2) {
                System.out.println("WRONG INPUT!");
                // когда будет view, необходимость этой проверки, скорее всего отпадет
            } else {
                Element root = doc.createElement(arg0);
                doc.appendChild(root);
                Element args = doc.createElement("args");
                root.appendChild(args);
                Element timer = doc.createElement("timer");
                timer.appendChild(doc.createTextNode(arguments[1]));
                args.appendChild(timer);
            }
        }
        send(doc);
    }
}
