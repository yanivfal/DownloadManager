import java.util.ArrayList;

public class Range {
    private long From;
    private long To;
    private long length;

    public Range(long i_From, long i_To){
        this.From = i_From;
        this.To = i_To;
        this.length = this.To - this.GetFrom() + 1;
    }

    /**
     * Returns the start of the range
     */
    public long GetFrom(){
        return this.From;
    }

    /**
     * Returns the end of the range
     */
    public long GetTo(){
        return this.To;
    }

    /**
     * Returns the length of range
     */
    public long GetLength(){
        return this.length;
    }

    /**
     * This method split this range into sub ranges.
     * @param NumberOfRanges the number of sub ranges.
     * @param ChunkSize chunk size
     * @return arrayList of sub ranges
     */
    public ArrayList<Range> SplitRangeToRanges(int NumberOfRanges, int ChunkSize) {
        ArrayList<Range> threadsRanges = new ArrayList<>();

        if (NumberOfRanges == 1){
            threadsRanges.add(this);
            return threadsRanges;
        }

        int totalNumOfChunks = (int)Math.ceil(DownloadProperties.FileSize / (double)ChunkSize);
        int numOfChunksInThread = (int)Math.ceil(totalNumOfChunks / (double)NumberOfRanges);
        int sizeOfRange = numOfChunksInThread * DownloadProperties.CHUNK_SIZE;
        long from = 0;
        long to;

        for(int i = 0; i < NumberOfRanges ; i++){
            if (i == NumberOfRanges - 1){
                to = this.To;
            }
            else{
                to = from + sizeOfRange - 1;
            }
            threadsRanges.add(new Range(from, to));
            from = to + 1;
        }

        return threadsRanges;
    }

    /**
     * This method split this range into sub ranges.
     * @param NumberOfRanges the number of sub ranges.
     * @return arrayList of sub ranges
     */
    public ArrayList<Range> SplitRangeToRanges2(int NumberOfRanges) {
        ArrayList<Range> threadsRanges = new ArrayList<>();

        if (NumberOfRanges == 1){
            threadsRanges.add(this);
            return threadsRanges;
        }

        int totalNumOfChunks = (int)Math.ceil((this.GetLength()) / (double)DownloadProperties.CHUNK_SIZE);
        int numOfChunksInThread = (int)Math.ceil(totalNumOfChunks / (double)NumberOfRanges);
        int sizeOfRange = numOfChunksInThread * DownloadProperties.CHUNK_SIZE;
        long from = this.GetFrom();
        long to;

        for(int i = 0; i < NumberOfRanges ; i++){
            if (i == NumberOfRanges - 1){
                to = this.To;
            }
            else{
                to = from + sizeOfRange - 1;
            }
            threadsRanges.add(new Range(from, to));
            from = to + 1;
        }

        return threadsRanges;
    }


     /**
      * Returns the string representation of Range
      * The format of string is "From - To".
      */
    @Override
    public String toString() {
        return String.format(From + " - " + To);
    }
}

