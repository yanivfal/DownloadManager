import java.io.*;

/**
 * Instances of this class support on writing chunks into a file.
 * In additionally it responsible to update the DowloadStatus class
 * on every chunk that was written.
 * @author  yaniv falik
 */
public class FileWriter {
    private File m_OutputFile;
    private Metadata m_Metadata;

    public FileWriter(Metadata metadata){

        m_Metadata = metadata;
        createOutputFile();

    }

    /**
     * This method responsible to create OutputFile the progress and open the file
     * which we want to startWriting the data.
     * @exception IOException
     */
    private void createOutputFile() {
        try{
            m_OutputFile = new File("./" + DownloadProperties.FileName);
            if (!m_OutputFile.isFile()) {
                m_OutputFile.createNewFile();
            }
        }
        catch (IOException ex){
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
    }

    /**
     * This method is called when all the file are written.
     * It wait for all the chunks to be written and then finish work.
     * @exception IOException
     */
    private void FinishDownload(){
        try{
            if (m_Metadata.IsWorkDone()) {
                m_Metadata.DeleteMetadata();
            }
            if (!m_Metadata.IsWorkStarted()){
                deleteOutputFile();
            }
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
    }

    private void deleteOutputFile() {
        if(m_OutputFile.exists())
        {
           m_OutputFile.delete();
        }
    }

    /**
     * This method is responsible of writing the chunks into the file.
     * The class should start writing  check if there is any chunks in the queue.
     * The work will stop only when the all the chunks have written.
     */
    public void startWriting() {
        Chunk chunkToWrite;
        RandomAccessFile fileSeeker;
        try {
            fileSeeker = new RandomAccessFile(m_OutputFile, "rw");

            while (!m_Metadata.IsWorkDone()) {
                if (!Manager.m_ChunksQueue.isEmpty()) {
                    chunkToWrite = Manager.m_ChunksQueue.take();
                    fileSeeker.seek(chunkToWrite.GetRange().GetFrom());
                    fileSeeker.write(chunkToWrite.GetData());
                    m_Metadata.updateWhenChunkWasWritten(chunkToWrite); // update that status of progress
                    m_Metadata.writeToDisk();
                }
            }

            fileSeeker.close();
            FinishDownload();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
    }
}
