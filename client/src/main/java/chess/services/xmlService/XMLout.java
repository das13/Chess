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
 * <code>XMLout</code> provides sending List of String values with OutPutStream
 * after processing the List into XML data, class <code>XMLin</code> can
 * handle such XML message and restore List of String values from it.
 * @author Dmytro Symonenko
 */
public class XMLout {

    /**
     * DataOutputStream is used to send XML data.
     */
    private final DataOutputStream host;

    /**
     * DocumentBuilderFactory is created once the instance of the class
     * is created and is used while the instance is used.
     */
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    /**
     * Creates <code>XMLout</code> that is used with specified
     * underlying OutputStream.
     *
     * @param out the specified OutputStream.
     */
    public XMLout(OutputStream out) {
        host = new DataOutputStream(out);
    }

    /**
     * The <code>send</code> method sends Document type data
     * through DataOutputStream.
     *
     * @param tosend Document made of List of String values for sending.
     * @throws TransformerConfigurationException in case of error while transformation process.
     * @throws IOException in case of failed or interrupted I/O operations.
     */
    private void send(Document tosend) throws TransformerConfigurationException, IOException {

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

    /**
     * Serving class that sends arrays of bytes to
     * DataOutputStream specified in outer class.
     */
    private class XMLOutputStream extends ByteArrayOutputStream {

        private final DataOutputStream out;

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

    /**
     * The <code>sendMessage</code> method transforms given List of String values
     * into Document type and sends it through <code>XMLOutputStream</code>
     *
     * @param message List of String values to send
     * @throws ParserConfigurationException in case of configuration error.
     * @throws IOException in case of failed or interrupted I/O operations.
     * @throws TransformerConfigurationException in case of error while transformation process.
     */
    public void sendMessage(List<String> message) throws ParserConfigurationException, IOException, TransformerConfigurationException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

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
        send(doc);
    }
}
