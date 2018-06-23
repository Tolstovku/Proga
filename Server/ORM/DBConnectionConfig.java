package Server.ORM;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;




public class DBConnectionConfig {
    public static String url;
    public static String login;
    public static String password;

    public static void getData(){
        try {
            try {

                try {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Path path = Paths.get("./src/Common/Resources/ORM.conf.tld");
                    Document document = builder.parse(path.toFile());

                    Node root = document.getDocumentElement();
                    NodeList nodeList = root.getChildNodes();
                    for(int i=0; i<nodeList.getLength(); i++){
                        Node node = nodeList.item(i);
                        switch (node.getNodeName()){
                            case "protocol":
                                url=node.getTextContent();
                                break;
                            case "host":
                                url+=node.getTextContent();
                                break;
                            case "database":
                                url+=node.getTextContent();
                                break;
                            case "login":
                                login=node.getTextContent();
                                break;
                            case "password":
                                password=node.getTextContent();
                                break;
                        }

                    }
                } catch (SAXException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (ParserConfigurationException e) {
            System.out.println("Файл отсутствует");
        }


    }
}
