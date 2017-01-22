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

/**
 * Created by bobnewmark on 22.01.2017
 */
public class XMLin {

    public Document receive(InputStream input) throws ParserConfigurationException, TransformerConfigurationException, IOException, SAXException {

        DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document doc;

        XMLInputStream xmlin = new XMLInputStream(input);
        xmlin.recive();
        doc = docBuilder.parse(xmlin);

        // get the first element
        Element element = doc.getDocumentElement();

        // get all child nodes
        NodeList nodes = element.getChildNodes();

        // print the text content of each child
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println("" + nodes.item(i).getTextContent());
        }

        return doc;
    }

    class XMLInputStream extends ByteArrayInputStream {

        private DataInputStream in;

        public XMLInputStream(InputStream in) {
            super(new byte[2]);
            this.in = new DataInputStream(in);
        }

        public void recive() throws IOException {
            int i = in.readInt();
            byte[] data = new byte[i];
            in.read(data, 0, i);
            this.buf = data;
            this.count = i;
            this.mark = 0;
            this.pos = 0;
        }
    }
}