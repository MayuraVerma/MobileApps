package ssh.net.mobile.android.media.myplayer;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

/*import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;*/

public class WriteXML {

    public WriteXML() {
        super();
        // TODO Auto-generated constructor stub
    }

    /*public void write(Playlists plyList) throws XMLStreamException, IOException {
          XMLOutputFactory factory      = XMLOutputFactory.newInstance();

          try {
              XMLStreamWriter writer = factory.createXMLStreamWriter(
                      new FileWriter(Common.getPlaylistpath()));

              writer.writeStartDocument();
              writer.writeStartElement("playlists");

              String next;
              Set<String> s = plyList.getListNames();
                Iterator<String> it = s.iterator();
                while(it.hasNext()){
                    next = it.next();
                      writer.writeStartElement("item");
                    writer.writeAttribute("name", next);
                    //System.out.println(next+" --> "+ for( String Ls :plyList.getPlist(next).toString().split(",")) System.out.print(Ls));
                    String Ls[] = plyList.getPlist(next).toString().split(",");
                    for( String ls :Ls){
                        writer.writeStartElement("file");
                        writer.writeCharacters(ls.replace('[', ' ').replace(']', ' '));
                        System.out.println(next+" --> "+ls.replace('[', ' ').replace(']', ' '));
                        writer.writeEndElement();
                    }
                    writer.writeEndElement();
                }

              writer.writeEndElement();//item
              writer.writeEndDocument();//playlists

              writer.flush();
              writer.close();

          } catch (XMLStreamException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
*/
    public void write2(Playlists plyList) throws IOException {
        FileWriter f;
        try {
            XmlSerializer xs = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xs.setOutput(writer);
            xs.startDocument("UTF-8", true);
            xs.startTag("", "Playlists");

            String next;
            Set<String> s = plyList.getListNames();
            Iterator<String> it = s.iterator();
            while (it.hasNext()) {
                next = it.next();
                xs.startTag("", "item");
                xs.attribute("", "name", next);
                //System.out.println(next+" --> "+ for( String Ls :plyList.getPlist(next).toString().split(",")) System.out.print(Ls));
                String Ls[] = plyList.getPlist(next);//.toString().split(",");
                for (String ls : Ls) {
                    if (ls != null && ls.length() > 0 && !ls.equals("")) {
                        xs.startTag("", "file");
                        xs.text(ls/*.replace('[', ' ').replace(']', ' ')*/);
                        //System.out.println(next+" --> "+ls.replace('[', ' ').replace(']', ' '));
                        xs.endTag("", "file");
                    }
                }
                xs.endTag("", "item");
            }

            xs.endTag("", "Playlists");//playlists
            xs.flush();

            f = new FileWriter(new File(Util.getPlaylistpath()));
            f.write(writer.getBuffer().toString());
            f.flush();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //f.close();
        }

    }

}
