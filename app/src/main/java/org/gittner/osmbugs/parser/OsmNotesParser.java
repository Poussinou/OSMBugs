package org.gittner.osmbugs.parser;

import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.common.OsmNoteComment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/* Parser for Openstreetmap Notes bug lists retrieved from notes */
public class OsmNotesParser
{
    public static ArrayList<OsmNote> parse(InputStream stream)
    {
        ArrayList<OsmNote> bugs = new ArrayList<>();
        try
        {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            doc.getDocumentElement().normalize();

            DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss zzz");

            NodeList nList = doc.getElementsByTagName("note");
            for (int i = 0; i != nList.getLength(); ++i)
            {
                Element wpt = (Element) nList.item(i);
                double lat = Double.parseDouble(wpt.getAttribute("lat"));
                double lon = Double.parseDouble(wpt.getAttribute("lon"));

                OsmNote.STATE state = OsmNote.STATE.CLOSED;
                if (wpt.getElementsByTagName("status").item(0).getTextContent().equals("open"))
                {
                    state = OsmNote.STATE.OPEN;
                }

                long id = Long.parseLong(wpt.getElementsByTagName("id").item(0).getTextContent());

                NodeList nListComments = wpt.getElementsByTagName("comment");
                ArrayList<OsmNoteComment> comments = new ArrayList<>();
                for (int n = 0; n != nListComments.getLength(); ++n)
                {
                    OsmNoteComment comment = new OsmNoteComment();
                    comment.setText(((Element) nListComments.item(n)).getElementsByTagName("text").item(0).getTextContent());

                    NodeList element = ((Element) nListComments.item(n)).getElementsByTagName("user");
                    if (element.getLength() != 0)
                    {
                        comment.setUsername(element.item(0).getTextContent());
                    }

                    DateTime creationDate = formatter.parseDateTime(((Element) nListComments.item(n)).getElementsByTagName("date").item(0).getTextContent());
                    comment.setCreationDate(creationDate);

                    comments.add(comment);
                }

                String text = "";
                String username = "";
                DateTime creationDate = DateTime.now();

                /* The first comment is the Bugs main Info (Description, date and user) */
                if (comments.size() > 0)
                {
                    username = comments.get(0).getUsername();
                    text = comments.get(0).getText();
                    creationDate = comments.get(0).getCreationDate();

                    comments.remove(0);
                }


                bugs.add(new OsmNote(lat, lon, creationDate, id, text, username, comments, state));
            }
        }
        catch (ParserConfigurationException | IOException | SAXException e)
        {
            e.printStackTrace();
        }
        return bugs;
    }
}
