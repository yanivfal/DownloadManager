import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Manager {
    private String[] m_ServersURL;
    private boolean m_IsNewDownload;
    Metadata m_Metadata;
    DownloadProperties m_DownloadProperties;
    public static LinkedBlockingQueue<Chunk> m_ChunksQueue = new LinkedBlockingQueue<>();

    public Manager(String [] serversURL, int numberOfThreads) {
        this.m_ServersURL = serversURL;
        this.m_DownloadProperties = new DownloadProperties(m_ServersURL[0], numberOfThreads);
        this.m_IsNewDownload = !(new File(Metadata.GetMetadataName()).exists());
    }

    public Manager(String serversURL, int threadsNumber) {
        this.m_ServersURL = new String [threadsNumber];
        for (int i = 0; i < m_ServersURL.length ; i++){
            this.m_ServersURL[i] = serversURL;
        }
        this.m_DownloadProperties = new DownloadProperties(m_ServersURL[0], threadsNumber);
        this.m_IsNewDownload = !(new File(Metadata.GetMetadataName()).exists());
    }

    /**
     * This method responsible of manage all the download threads.
     * Each thread get specific range of the file.
     * After each thread started to work, we initialize the writer.
     */
    public void DownloadFile() {
        initializeMetaData();
        ArrayList<ArrayList<Range>> rangeForEachThread = setWorkToEachThread(getRangesOfDownload());

        for (int i = 0; i < DownloadProperties.NumOfThreads; i++) {
            HTTPRangeGetter HttpGetter = new HTTPRangeGetter(rangeForEachThread.get(i),
                    m_ServersURL[i % m_ServersURL.length], i + 1, this);
            new Thread(HttpGetter).start();
        }

        new FileWriter(m_Metadata).startWriting();
    }

    /**
     * This get ArrayList of ranges and split the ranges into array in size of
     * the number of threads.
     * @return array of ArrayList which is the list of range for each thread
     */
    private ArrayList<ArrayList<Range>> setWorkToEachThread(ArrayList<Range> rangesOfWork) {
        ArrayList<ArrayList<Range>> rangeForEachThread = new ArrayList<>();

        for (int i = 0 ; i < rangesOfWork.size(); i++){
            if (rangeForEachThread.size() <= (i % DownloadProperties.NumOfThreads)){
                ArrayList<Range> temp = new ArrayList<>();
                temp.add(rangesOfWork.get(i));
                rangeForEachThread.add(temp);
            }
            else{
                rangeForEachThread.get(i % DownloadProperties.NumOfThreads).add(rangesOfWork.get(i));
            }
        }

        return rangeForEachThread;
    }

    /**
     * This method initialize the Metadata member.
     * If the downloaded is new it create new Metadata.
     * If the download is continued we load the Metadata from the metadata file.
     */
    private void initializeMetaData(){
        if (m_IsNewDownload){
            this.m_Metadata = new Metadata();
            File downloadFile = new File("./" + DownloadProperties.FileName);
            if (downloadFile.exists()){
                downloadFile.delete();
            }
        }
        else{
            this.m_Metadata = Metadata.loadMetadata();
        }
    }

    /**
     * This method return the ranges of the download.
     * @return ArrayList in length of threads number.
     */
    private ArrayList<Range> getRangesOfDownload() {
        if (m_IsNewDownload){
            Range TotalRange = new Range(0, DownloadProperties.FileSize - 1);
            return TotalRange.SplitRangeToRanges2(DownloadProperties.NumOfThreads);
        }
        else{
            return m_Metadata.getNotYetDownloaded();
        }
    }
}