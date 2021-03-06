package edu.msu.team15.connect4;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Cloud {
    private static final String MAGIC = BuildConfig.KEY;

    // Login and Create User expect user, pw, and magic as get params
    private static final String LOGIN_URL = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/login-user.php";
    private static final String CREATE_USER_URL = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/create-user.php";
    private static final String ADD_TO_GAME_URL = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/add-user.php";
    private static final String CHECK_OPPONENT = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/check-opponent.php";
    private static final String DISCONNECT = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/disconnect-users.php";
    private static final String SAVE_STATE = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/save-state.php";
    private static final String LOAD_STATE = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/load-state.php";
    private static final String CHECK_TURN_URL = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/check-turn.php";
    private static final String UPDATE_WIN = "https://webdev.cse.msu.edu/~dennis57/cse476/project2/update-win.php";

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

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public boolean addToGame(String user){
        String query = ADD_TO_GAME_URL + "?user=" + user + "&magic=" + MAGIC;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /*
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "connect4");

                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return true;
    }

    public InputStream checkForOpponent(String username){
        String query = CHECK_OPPONENT + "?user=" + username + "&magic=" + MAGIC;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream Disconnect(){
        String query = DISCONNECT + "?magic=" + MAGIC;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


    public InputStream createUser(final String username, final String password) {
        // Create a get query
        String query = CREATE_USER_URL + "?user=" + username + "&magic=" + MAGIC + "&pw=" + password;
        InputStream stream = null;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public boolean saveToCloud(Integer currPlayer, String user, String boardString){
        String query = SAVE_STATE + "?currplayer=" + currPlayer.toString() + "&user=" + user + "&magic=" + MAGIC + "&boardstate='" + boardString + "'";
        InputStream stream = null;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /*
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "connect4");

                String status = xml2.getAttributeValue(null, "status");
                if(!status.equals("yes")) {
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public InputStream loadFromCloud(){
        String query = LOAD_STATE + "?magic=" + MAGIC;
        InputStream stream = null;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream checkTurn(String user) {
        String query = CHECK_TURN_URL + "?magic=" + MAGIC;
        InputStream stream = null;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public boolean updateWin(Integer winner, String user) {
        String query = UPDATE_WIN + "?user=" + user + "&magic=" + MAGIC + "&winner=" + winner;
        InputStream stream = null;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /*
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "connect4");

                String status = xml2.getAttributeValue(null, "status");
                if(!status.equals("yes")) {
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     * @param xml the parser
     * @throws IOException throws io error
     * @throws XmlPullParserException throws xml exception
     */
    private static void skipToEndTag(XmlPullParser xml)
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

