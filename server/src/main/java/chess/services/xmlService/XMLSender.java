package chess.services.xmlService;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * <code>XMLSender</code> provides sending List of String values with OutPutStream
 * after processing the List into XML data.
 */
public class XMLSender {

    private final OutputStream output;
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private final static Logger logger = Logger.getLogger(XMLSender.class.getClass());

    public XMLSender(OutputStream output) {
        this.output = output;
    }
    private class XMLOutputStream extends ByteArrayOutputStream {

        private final DataOutputStream out;

        XMLOutputStream() {
            super();
            this.out = new DataOutputStream(output);
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
            logger.error("Error while transforming xml data", ex);
        }
        out.send();
    }

    public void send(List<String> message) throws ParserConfigurationException, IOException, TransformerConfigurationException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("root");
        root.setAttribute("function", message.get(0));
        doc.appendChild(root);
        Element args1 = doc.createElement("args");
        root.appendChild(args1);
        for (int i = 1; i < message.size(); i++) {
            Element el = doc.createElement("arg");
            el.appendChild(doc.createTextNode(message.get(i)));
            args1.appendChild(el);
        }
        send(doc);
    }
}