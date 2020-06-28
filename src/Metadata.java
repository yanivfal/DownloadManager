import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class is responsible to manage the progress status of the writing.
 */
public class Metadata implements Serializable{
    private boolean [] m_ChunksStatus; // chunk map
    private double m_NumberOfWrittenChunks = 0;
    private int m_Progress = 0; // number between 0 - 100
    private boolean m_IsWorkDone = false;

    public Metadata()
    {
        m_ChunksStatus = new boolean[(int)Math.ceil(DownloadProperties.FileSize / (double)DownloadProperties.CHUNK_SIZE)];
    }

    /**
     * This method used to update the chunk map after some chunk was written to file.
     * @param chunk the chunk which already in the file
     */
    public void updateWhenChunkWasWritten(Chunk chunk) {
        if (!m_ChunksStatus[chunk.GetIndex()]){
            m_ChunksStatus[chunk.GetIndex()] = true;
            this.m_NumberOfWrittenChunks++;
        }

        printAndUpdateProgress();

        if (this.m_NumberOfWrittenChunks == m_ChunksStatus.length) {
            m_IsWorkDone = true;
        }
    }

    /**
     * This method responsible of:
     * 1) update the progress member if there is any change
     * 2) print to the user the progress if there is any change/
     */
    private void printAndUpdateProgress(){
        int tempProcess = (int)((m_NumberOfWrittenChunks / m_ChunksStatus.length) * 100);

        if (m_Progress < tempProcess){
            m_Progress = tempProcess;
            System.out.println("Downloaded "+ m_Progress + "%");
        }
    }

    /**
     * This method return true if all the file was downloaded.
     */
    public boolean IsWorkDone(){
        return m_IsWorkDone;
    }


    /**
     * This method return true the file is already started to download
     */
    public boolean IsWorkStarted(){
        return m_NumberOfWrittenChunks != 0;
    }

    /**
     * This method responsible to convert the chunk map into list of ranges
     * which represent the ranges of the file which not downloaded yet.
     * @return ArrayList of the ranges which not download yet.
     */
    public ArrayList<Range> getNotYetDownloaded() {
        ArrayList<Range> ranges = new ArrayList<>();
        long from;
        long to = 0;

        for (int i = 0; i < m_ChunksStatus.length; i++) {
            if (!m_ChunksStatus[i]) {
                from = i * DownloadProperties.CHUNK_SIZE;
                for (int j = i + 1; j <= m_ChunksStatus.length; j++) {
                    if (j == m_ChunksStatus.length) {
                        to = DownloadProperties.FileSize -1;
                        i = j;
                        break;
                    }
                    else if (m_ChunksStatus[j]){
                        to = j * DownloadProperties.CHUNK_SIZE - 1;
                        i = j;
                        break;
                    }
                }
                ranges.add(new Range(from, to));
            }
        }

        return splitToNumOfThreads(ranges);
    }

    /**
     * This method take the ranges array and split it according to the number
     * of thread.
     * @return ArrayList of the ranges in the size of thread number
     */
    private ArrayList<Range> splitToNumOfThreads(ArrayList<Range> ranges) {
        Comparator<Range> compareByLength = (Range range1, Range range2) -> range1.GetLength() > range2.GetLength() ? 1 : -1;
        ranges.sort(compareByLength); //sort in order to split the biggest ranges first
        int numberOfThreads = DownloadProperties.NumOfThreads;

        //More threads than ranges - we split the ranges
        while (ranges.size() < numberOfThreads){
            Range rangeToSplit = ranges.get(0);
            ranges.remove(rangeToSplit);
            ArrayList<Range> res = rangeToSplit.SplitRangeToRanges2(2);
            ranges.add(res.get(0));
            ranges.add(res.get(1));
        }

        return ranges;
    }

    /**
     * This method load the metadata file.
     * And return Metadata object.
     * @exception IOException
     * @exception ClassNotFoundException
     */
    public static Metadata loadMetadata() {
        Metadata result = null;

        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(GetMetadataName());
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            result = (Metadata) in.readObject();
            result.m_NumberOfWrittenChunks = 0;
            for (boolean isWritten : result.m_ChunksStatus){
                if(isWritten){
                    result.m_NumberOfWrittenChunks++;
                }
            }
            in.close();
            file.close();

        } catch (IOException ex) {
            System.err.println("Something occur while trying to load metadata from the disk");
            System.exit(-1);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);
        }

        return result;
    }

    /**
     * This method startWriting the progress status into metadata file.
     * It should br called only if the download are paused from some reason.
     * @exception IOException
     */
    public void writeToDisk(){
        try {
            // Saving of object in a file
            FileOutputStream file = new FileOutputStream(getTempMetadataName());
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(this);

            out.close();
            file.close();
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
        
        this.renameFile();
    }

    /**
     * This method rename the metadata file from temp to orig in atomic action
     */
    private void renameFile() {
        File tmp = new File(getTempMetadataName());
        Path tmpPath = Paths.get(tmp.getAbsolutePath());
        File destination = new File(GetMetadataName()).getAbsoluteFile();
        Path destinationPath = Paths.get(destination.getAbsolutePath());
        boolean isRenamed = false;
        while(!isRenamed){
            try {
                Files.move(tmpPath, destinationPath, StandardCopyOption.ATOMIC_MOVE);
                isRenamed = true;
            } catch (IOException ignored) { }
        }
    }

    /**
     * This method return true the file is already started to download
     */
    public static String GetMetadataName(){
        String basicName = DownloadProperties.FileName.substring(0, DownloadProperties.FileName.lastIndexOf('.'));
        return "./" + basicName + ".meta";
    }

    /**
     * This method return true the file is already started to download
     */
    private static String getTempMetadataName(){
        String basicName = DownloadProperties.FileName.substring(0, DownloadProperties.FileName.lastIndexOf('.'));
        return "./" + basicName + "_temp.meta";
    }

    /**
     * This method delete the metadata file after the download was finished.
     */
    public void DeleteMetadata(){
        File metadata = new File(GetMetadataName());

        if(metadata.exists())
        {
            metadata.delete();
        }
    }
}