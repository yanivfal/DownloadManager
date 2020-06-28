Yaniv Falik.
Ilai Genish.

1. Chunk:
This class represent chunk.
Each chunk contains range and bytes buffer to store the downloaded data.

2. DownloadProperties:
This class srote the download properties like: File Name, File Size, Chunk Size...
All other objects have access to this class.

3. FileWriter:
This class represent the object which in charge on writing the downloaded chunk to the file. 

4. Main.HTTPRangeGetter:
This class download chunks, each object that create get different range to download (each range is divided to chunks).

5. IdcDm:
This class contain the main function.

6. Manager:
This class mange the download. create FileWriter object, HTTPRangreGetters object, splits the work and so on.

7. Metadata:
This class manage the progress status of the writing chunks.
After each chunk is written, this object should be updated.

8. Range:
This class represent range (from byte, to byte) which using in Chunk Object and in the work splitting procedure.

9. URLHandler:
This class hold some static method which relate to HTTP request.
In this class we add the range property.


