package chess.services.xmlService;

import chess.controller.Controller;
import chess.services.PlayerService;
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
 * Created by bobnewmark on 21.01.2017
 */
public class XMLReciever {

    private DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    Document doc;
    private DataInputStream in;
    private Controller host;

    public XMLReciever(Controller controller) {
        host = controller;
    }

    private class XMLInputStream extends ByteArrayInputStream {

        public XMLInputStream() {
            super(new byte[2]);
//            host = controller;
//            this.in = host.getInput();
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
    public Document receive() throws ParserConfigurationException, TransformerConfigurationException, IOException, SAXException {

        docBuilder = docBuilderFact.newDocumentBuilder();
        XMLInputStream xmlin = new XMLInputStream();
        xmlin.recive();
        doc = docBuilder.parse(xmlin);
        doc.getDocumentElement().normalize();

        Element element = doc.getDocumentElement();
        String tag = element.getNodeName();
        NodeList nodes = element.getChildNodes();

        if ("reg".equals(tag)) {
            // РЕГИСТРАЦИЯ НОВОГО ИГРОКА
            for (int i = 0; i < nodes.getLength(); i++) {
                if ("args".equals(nodes.item(i).getNodeName())) {
                    Element el = (Element) nodes.item(i);
                    String login = el.getElementsByTagName("login").item(0).getTextContent();
                    String password = el.getElementsByTagName("password").item(0).getTextContent();
                    String ipadress = el.getElementsByTagName("ipadress").item(0).getTextContent();
                    PlayerService.reg(host, login, password, ipadress);
                }
            }



        }

        // print the text content of each child




        return doc;
    }



}