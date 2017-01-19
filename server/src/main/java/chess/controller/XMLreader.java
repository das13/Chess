package chess.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by bobnewmark on 19.01.2017
 */

public class XMLreader {

    private String nickname;
    private String password;
    private String login;
    private String ip;


    // reads XML and depending on title does next steps
    public void readFile(File file) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            String title = doc.getDocumentElement().getNodeName();
            Element el = doc.getDocumentElement();

            if (title.equals("reg")) {
                nickname = getTextValue(el, "nickname");
                password = getTextValue(el, "password");
                login = getTextValue(el, "login");
                ip = getTextValue(el, "ipadress");
                // TODO: create Player and add him to FREE players list
            }
            // TODO: add variants for moves and other

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for getting value from xml by key
    private String getTextValue(Element el, String tag) {
        String value = "";
        NodeList nl;
        nl = el.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }
}
