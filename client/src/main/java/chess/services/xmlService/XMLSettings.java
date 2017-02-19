package chess.services.xmlService;

import chess.ClientMain;
import chess.Constants;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Admin on 19.02.2017.
 */
public class XMLSettings {
    private static File filesettings = new File(System.getProperty("user.dir"), "settings.xml");
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private final static Logger logger = Logger.getLogger(XMLSettings.class.getClass());
    public static void loadSettings() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        if(!filesettings.exists()) {
            logger.warn("file with settings not found. New empty file created");
            saveSettings();
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(filesettings);
        doc.getDocumentElement().normalize();
        Element element = doc.getDocumentElement();
        NodeList nodes = element.getChildNodes();
        ClientMain.host = element.getElementsByTagName("host").item(0).getTextContent();
        ClientMain.port = Integer.parseInt(element.getElementsByTagName("port").item(0).getTextContent());
    }
    public static void saveSettings() throws ParserConfigurationException, FileNotFoundException, TransformerException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("root");
        Element host = doc.createElement("host");
        host.appendChild(doc.createTextNode(Constants.HOST));
        root.appendChild(host);
        Element port = doc.createElement("port");
        port.appendChild(doc.createTextNode(String.valueOf(Constants.PORT)));
        root.appendChild(port);
        doc.appendChild(root);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(filesettings);
        transformer.transform(source, result);
    }
}
