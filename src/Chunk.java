/**
 * Instances of this class represent a chunk of data which will be written in the file.
 * Each instance holds array of bytes which is the data and the range of the data.
 */
public class Chunk {
    private Range m_Range;
    private byte [] m_BytesArr;
    private int m_ChunkIndex; // the serial number of the chunk (needed for the status)

    public Chunk (Range i_Range){
        this.m_Range = i_Range;
        this.m_BytesArr = new byte[(int)(i_Range.GetTo() - i_Range.GetFrom()) + 1];
        this.m_ChunkIndex = setChunkID();
    }

    private int setChunkID() {
        return (int)(m_Range.GetFrom() / DownloadProperties.CHUNK_SIZE);
    }

    public Range GetRange(){
        return this.m_Range;
    }

    public byte [] GetData(){
        return this.m_BytesArr;
    }

    public int GetIndex(){
        return this.m_ChunkIndex;
    }

    public long GetLength(){
        return this.m_Range.GetLength();
    }
}
