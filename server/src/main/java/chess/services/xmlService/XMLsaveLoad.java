package chess.services.xmlService;

import chess.Server;
import chess.model.Player;
import chess.model.Status;
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
import java.io.IOException;
import java.util.List;

/**
 * Created by bobnewmark on 24.01.2017
 */
public class XMLsaveLoad {

    private static List<Player> players = Server.getPlayers();
    static volatile File filePlayers = new File("server/src/main/java/chess/services/savedPlayers.xml");
    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();



    public XMLsaveLoad() throws ParserConfigurationException {
    }

    public static void savePlayers() throws ParserConfigurationException, TransformerException {

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("savedPlayers");
        doc.appendChild(root);

        for (Player player: players) {
            Element el = doc.createElement("player");
            root.appendChild(el);
            Element login = doc.createElement("login");
            login.appendChild(doc.createTextNode(player.getLogin()));
            el.appendChild(login);
            Element password = doc.createElement("password");
            password.appendChild(doc.createTextNode(player.getPassword()));
            el.appendChild(password);
            Element rank = doc.createElement("rank");
            rank.appendChild(doc.createTextNode(String.valueOf(player.getRank())));
            el.appendChild(rank);
            Element status = doc.createElement("status");
            status.appendChild(doc.createTextNode(String.valueOf(player.getStatus())));
            el.appendChild(status);
            Element ipadress = doc.createElement("ipadress");
            ipadress.appendChild(doc.createTextNode(player.getIpadress()));
            el.appendChild(ipadress);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(filePlayers);
        transformer.transform(source, result);
    }

    public static void loadPlayers() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(filePlayers);
        doc.getDocumentElement().normalize();

        Element element = doc.getDocumentElement();
        //String tag = element.getNodeName();
        NodeList nodes = element.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                if ("player".equals(nodes.item(i).getNodeName())) {
                    Element el = (Element) nodes.item(i);
                    String login = el.getElementsByTagName("login").item(0).getTextContent();
                    String password = el.getElementsByTagName("password").item(0).getTextContent();
                    int rank = Integer.parseInt(el.getElementsByTagName("rank").item(0).getTextContent());
                    Status status = Status.valueOf(el.getElementsByTagName("status").item(0).getTextContent());
                    String ipadress = el.getElementsByTagName("ipadress").item(0).getTextContent();
                    Player player = new Player(login, password, rank, status, ipadress);
                    players.add(player);
                }
            }
    }
}
