package chess.services.xmlService;

import org.w3c.dom.Document;
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
 * Created by bobnewmark on 21.01.2017
 */
public class XMLReciever {

    private DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    Document doc;

    private class XMLInputStream extends ByteArrayInputStream {

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

    // method returns Document that is built from InputStream
    public Document receive(InputStream in) throws ParserConfigurationException, TransformerConfigurationException, IOException, SAXException {

        docBuilder = docBuilderFact.newDocumentBuilder();
        XMLInputStream xmlin = new XMLInputStream(in);
        xmlin.recive();
        doc = docBuilder.parse(xmlin);
        return doc;
    }

}