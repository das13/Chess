package chess.services.xmlService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>XMLin</code> provides receiving XML data from InputStream
 * and returns a List of String values after processing received data.
 * @author Dmytro Symonenko
 */
public class XMLin {

    /**
     * DocumentBuilderFactory is created once the instance of the class
     * is created and is used while the instance is used.
     */
    private final DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();

    /**
     * InputStream that is used to get XML data to process.
     */
    private final InputStream input;

    /**
     * Creates a XMLin that is used with specified
     * underlying InputStream.
     *
     * @param input the specified InputStream
     */
    public XMLin(InputStream input) {
        this.input = input;
    }

    /**
     * Serving class that operates with array of bytes from
     * InputStream specified in outer class.
     */
    private class XMLInputStream extends ByteArrayInputStream {

        /**
         * DataInputStream that specifies InputStream of outer class
         * for building arrays of received data.
         */
        private final DataInputStream in;

        /**
         * Creates a <code>XMLInputStream</code> with initializing
         * DataInputStream with InputStream of outer class.
         */
        XMLInputStream() {
            super(new byte[2]);
            this.in = new DataInputStream(input);
        }

        /**
         * The <code>receive</code> method reads data from DataInputStream
         * by arrays of received bytes.
         * @throws IOException if the first byte cannot be read, stream
         * is closed or other IO error occurs.
         */
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


    /**
     * Recieves XML from InputStream specified in class constructor
     * and transforms it into List of String values.
     * @return List of String values from received XML nodes.
     * @throws ParserConfigurationException in case of configuration error.
     * @throws IOException in case of failed or interrupted I/O operations.
     * @throws SAXException when there are errors or warnings in parsing XML data.
     */
    public List<String> receive() throws ParserConfigurationException,  IOException, SAXException {

        /**
         *
         */
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        XMLInputStream xmlin = new XMLInputStream();
        xmlin.receive();
        Document doc = docBuilder.parse(xmlin);

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