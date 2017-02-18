package chess.services.xmlService;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
 * <code>XMLReceiver</code> provides receiving XML data from InputStream
 * and returns a List of String values after processing received data.
 */
public class XMLReceiver {

    private final DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
    private final InputStream input;
    private final static Logger logger = Logger.getLogger(XMLReceiver.class.getClass());

    /**
     * Creates <code>XMLReceiver</code> with given input, from
     * where expected xml data will be transferred.
     *
     * @param input input stream to use
     */
    public XMLReceiver(InputStream input) {
        this.input = input;
    }

    private class XMLInputStream extends ByteArrayInputStream {
        private final DataInputStream in;

        XMLInputStream() {
            super(new byte[2]);
            this.in = new DataInputStream(input);
        }
        void receive() throws IOException {
            int i = in.readInt();
            byte[] data = new byte[i];
            in.read(data, 0, i);
            this.buf = data;
            this.count = i;
            this.mark = 0;
            this.pos = 0;
        }
    }

    // method returns Document that is built from InputStream
    public List<String> receive() throws ParserConfigurationException, TransformerConfigurationException, IOException, SAXException {

        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        XMLInputStream xmlin = new XMLInputStream();
        xmlin.receive();
        Document doc = docBuilder.parse(xmlin);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File("input.xml"));
        Source input = new DOMSource(doc);
        try {
            transformer.transform(input, output);
        } catch (TransformerException e) {
            logger.error("Error transforming xml data", e);
        }
        doc.getDocumentElement().normalize();
        List<String> list = new ArrayList<String>();

        Element element = doc.getDocumentElement();
        String root = element.getAttribute("function");
        list.add(root);

        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if ("args".equals(nodes.item(i).getNodeName())) {
                Element el = (Element) nodes.item(i);
                for (int j = 0; j < el.getChildNodes().getLength(); j++) {
                   String str = el.getChildNodes().item(j).getTextContent();
                    list.add(str);
                }
            }
        }
        return list;
    }
}