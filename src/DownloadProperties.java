public class DownloadProperties {
    public static String FileName;
    public static long FileSize;
    public static final int CHUNK_SIZE = 32768; // chunk size
    public static final int CONNECTION_TIME_OUT = 10 * 1000; //if the connection lost more than 5000ms than close
    public static final int READ_TIME_OUT = 10 * 1000; //if there was no read for more than 5000ms than close
    public static int NumOfThreads;

    public DownloadProperties(String url, int numOfThreads){
        FileName = getFileNameByURL(url);
        FileSize = getFileSize(url);
        NumOfThreads = numOfThreads;
    }

    /**
     * This method extract from the url the file name.
     */
    private String getFileNameByURL(String URL) {
        return URL.substring(URL.lastIndexOf('/') + 1);
    }

    /**
     * This method return the size of the file which we want to download.
     */
    private long getFileSize(String fileURL) {
        return URLHandler.GetURLFileSize(fileURL);
    }
}
