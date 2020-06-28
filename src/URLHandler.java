import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class hold some static method which relate to HTTP request
 **/
public class URLHandler{
    /**
     * This method return the size of the file
     **/
    public static long GetURLFileSize(String serversURL){
        URL serverURL;
        long fileSize;
        HttpURLConnection connection = null;

        try {
            serverURL = new URL(serversURL);
            connection = (HttpURLConnection) serverURL.openConnection();
            fileSize = connection.getContentLengthLong();

        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return fileSize;
    }

    /**
     * This method build HTTP connection and return it
     **/
    public static HttpURLConnection getServerConnection(String FileURL, long startOffset, long endOffset) {
        //convert URL string to URL.
        URL serverURL;
        HttpURLConnection connection = null;

        try {
            serverURL = new URL(FileURL);

            //open a connection with server URL.
            connection = (HttpURLConnection) serverURL.openConnection();

            //set request method
            connection.setRequestMethod("GET");

            //if the connection lost for more than 5 seconds, it will be closed
            connection.setConnectTimeout(DownloadProperties.CONNECTION_TIME_OUT);

            //if the there was know ridding more than 10 second, the connection will be  closed
            connection.setReadTimeout(DownloadProperties.READ_TIME_OUT);

            connection.setRequestProperty("Range",String.format("Bytes=%d-%d",startOffset, endOffset));

        } catch (MalformedURLException ex) {
            System.err.println("malformed URL was entered");
        } catch (IOException ex) {
            System.err.println("Can't reach the remote server, please check your internet or try later.");
        }

        return connection;
    }
}