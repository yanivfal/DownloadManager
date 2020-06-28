import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IdcDm {
    public static void main(String[] args) {
        int numberOfThreads;

        //check if number of thread insert as input
        if (args.length == 1){
            numberOfThreads = 1;
        }
        else{
            numberOfThreads = Integer.parseInt(args[1]);
        }

        //Check if the mode is multi-server download
        String UrlArg = args[0];
        boolean isUrlList = !UrlArg.startsWith("http://") && !UrlArg.startsWith("https://");

        if (isUrlList){
            new Manager(readAllLinesFromFile(UrlArg) , numberOfThreads).DownloadFile();
        }
        else{
            new Manager(UrlArg, numberOfThreads).DownloadFile();
        }
    }

    private static String [] readAllLinesFromFile(String input){
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            System.err.println("File: '" + input + "' doesn't exist - add it to build file");
            System.exit(1);
        }

        String line;
        ArrayList<String> lines = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // declaration and initialise String Array
        String results[] = new String[lines.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < lines.size(); j++) {

            // Assign each value to String array
            results[j] = lines.get(j);
        }

        return results;
    }
}
