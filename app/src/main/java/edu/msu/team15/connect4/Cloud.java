package edu.msu.team15.connect4;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Cloud {
    private static final String MAGIC = "NechAtHa6RuzeR8x";

    // Login and Create User expect user, pw, and magic as get params
    private static final String LOGIN_URL = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/login-user.php";
    private static final String CREATE_USER_URL = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/create-user.php";
    
    private static final String UTF8 = "UTF-8";

    public InputStream loginUser(final String username, final String password) {
        // Create a get query
        String query = LOGIN_URL + "?user=" + username + "&magic=" + MAGIC + "&pw=" + password;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream stream = conn.getInputStream();
            return stream;

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream createUser(final String username, final String password) {
        // Create a get query
        String query = CREATE_USER_URL + "?user=" + username + "&magic=" + MAGIC + "&pw=" + password;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream stream = conn.getInputStream();
            return stream;

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     * @param xml the parser
     * @throws IOException throws io error
     * @throws XmlPullParserException throws xml exception
     */
    public static void skipToEndTag(XmlPullParser xml)
            throws IOException, XmlPullParserException {
        int tag;
        do
        {
            tag = xml.next();
            if(tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while(tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }
}

