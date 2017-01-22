package chess.services.xmlService;

import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by bobnewmark on 22.01.2017
 */
public class XMLout {

    public void send(Document tosend, OutputStream output) throws TransformerConfigurationException, IOException {
        XMLOutputStream out = new XMLOutputStream(output);

        StreamResult sr = new StreamResult(out);
        DOMSource ds = new DOMSource(tosend);
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        try {
            tf.transform(ds, sr);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        out.send();
    }

    private class XMLOutputStream extends ByteArrayOutputStream {

        private DataOutputStream out;

        public XMLOutputStream(OutputStream out) {
            super();
            this.out = new DataOutputStream(out);
        }

        public void send() throws IOException {
            byte[] data = toByteArray();
            out.writeInt(data.length);
            out.write(data);
            reset();
        }
    }
}