package chess.services.xmlService;

import chess.Client;
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
 * Created by bobnewmark on 22.01.2017
 */
public class XMLin {

    private DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
    private DocumentBuilder docBuilder;
    private Document doc;

    private Client host;

    public XMLin(Client client) {
        host = client;
    }

    private class XMLInputStream extends ByteArrayInputStream {

        private DataInputStream in;

        XMLInputStream() {
            super(new byte[2]);
            this.in = host.getInput();
        }

        void recive() throws IOException {
            int i = in.readInt();
            byte[] data = new byte[i];
            in.read(data, 0, i);
            this.buf = data;
            this.count = i;
            this.mark = 0;
            this.pos = 0;
        }
    }

    public List<String> receive() throws ParserConfigurationException, TransformerConfigurationException, IOException, SAXException {

        docBuilder = docBuilderFact.newDocumentBuilder();
        XMLInputStream xmlin = new XMLInputStream();
        xmlin.recive();
        doc = docBuilder.parse(xmlin);

        List<String> list = new ArrayList<String>();

        Element element = doc.getDocumentElement();
        String root = element.getAttribute("function");
        list.add(root);

        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if ("args".equals(nodes.item(i).getNodeName())) {
                Element el = (Element) nodes.item(i);
                for (int j = 0; j < el.getElementsByTagName("arg").getLength(); j++) {
                    String str = el.getElementsByTagName("arg").item(j).getTextContent();
                    list.add(str);
                }
            }
        }
        return list;
    }
}