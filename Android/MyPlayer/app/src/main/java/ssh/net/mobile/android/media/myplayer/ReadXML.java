package ssh.net.mobile.android.media.myplayer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ReadXML extends DefaultHandler {
    List<PlaylistObj> plyList;
    String listXmlFileName;
    String tmpValue;
    PlaylistObj lstTmp;
    //private XMLStreamReader streamReader;
    private int eventType;
    private String lastElement;
    private String attribvalue;


    public ReadXML() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void readXML2(Playlists pList) {
        listXmlFileName = Util.getPlaylistpath();
        plyList = new ArrayList<PlaylistObj>();
        try {
            parseDocument();
            printDatas(pList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return pList;
    }

    private void parseDocument() {
        // parse
        File f = new File(listXmlFileName);
        if (f.exists()) {
            int gj = 0;
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(f, this);
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfig error");
        } catch (SAXException e) {
            System.out.println("SAXException : xml not well formed");
        } catch (IOException e) {
            System.out.println("IO error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printDatas(Playlists pList) {
        // System.out.println(bookL.size());
        //Playlists pList = new Playlists();
        for (PlaylistObj tmpB : plyList) {
            System.out.println(tmpB.toString());
            pList.add(tmpB.getName(), tmpB.getFileNames().toString().substring(1, tmpB.getFileNames().toString().length() - 1));
        }
        //return pList;
    }

    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
        // if current element is book , create new book
        // clear tmpValue on start of element

        if (elementName.equalsIgnoreCase("item")) {
            lstTmp = new PlaylistObj();
            lstTmp.setName(attributes.getValue("name"));
        }
        // if current element is publisher

    }

    @Override
    public void endElement(String s, String s1, String element) throws SAXException {
        // if end of book element add to list
        if (element.equals("item")) {
            plyList.add(lstTmp);
        }


        if (element.equalsIgnoreCase("file")) {
            lstTmp.getFileNames().add(tmpValue);
        }

    }

    @Override
    public void characters(char[] ac, int i, int j) throws SAXException {
        tmpValue = new String(ac, i, j);
    }


}
