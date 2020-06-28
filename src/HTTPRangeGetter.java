import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class HTTPRangeGetter implements Runnable{

    private ArrayList<Range> Ranges;
    private String URL;
    private int WorkerID;
    private Manager Manager;

    HTTPRangeGetter(ArrayList<Range> ranges, String url, int ID, Manager manager) {
        this.URL = url;
        this.Ranges = ranges;
        this.WorkerID = ID;
        this.Manager = manager;
    }

    @Override
    public void run(){
        this.downloadRange();
    }

    /**
     * This method responsible of bring the data in the relevant range
     * It split the range into small chunks and read the bytes from the inputStream of the connection
     * @exception Exception
     */
    private void downloadRange() {
        for (Range range : Ranges){
            long startOffset = range.GetFrom();
            long endOffset;
            try{

                // initialize inputStream and jump to the relevant offset
                HttpURLConnection connection = URLHandler.getServerConnection(URL, range.GetFrom(), range.GetTo());
                InputStream inputStream = connection.getInputStream();

                //check if the server support range requests
                if (!(connection.getResponseCode() ==  connection.HTTP_PARTIAL)){
                    inputStream.skip(startOffset);
                }

                System.out.println("[" + WorkerID + "] Start downloading range (" +
                        range.toString() + ")" + " from: " + URL);

                while (startOffset < range.GetTo()){
                    //calc the end offset of chunk
                    endOffset = getEndOffset(startOffset, range);

                    //create chunk in the relevant offset
                    Chunk currChunk = new Chunk(new Range(startOffset, endOffset));

                    //read the the relevant bytes from inputStream
                    inputStream.readNBytes(currChunk.GetData(), 0, currChunk.GetData().length);

                    //insert the chunk to the queue
                    Manager.m_ChunksQueue.put(currChunk);

                    //calc the start offset of chunk
                    startOffset = endOffset + 1;
                }
            } catch (IOException ex){
                System.err.println("Something wrong with your internet connection, please check it.");
                System.exit(1);
            }
            catch (InterruptedException ex){
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
        System.out.println("[" + WorkerID + "] finished downloading");
    }

    /**
     * This method calculate the end offset of chunk
     * @param start the start offset of the chunk
     */
    private long getEndOffset(long start, Range currRange) {
        long end = start + DownloadProperties.CHUNK_SIZE - 1;

        return (end >= currRange.GetTo() - 1) ? currRange.GetTo() : end;
    }
}
