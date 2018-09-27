import java.io.*;


/**
 *  <lavash>
 *      <kitty/>
 *  </lavash>
 */
public class Main {
    private static StringBuilder tokens = new StringBuilder();
    ;

    public static void main(String[] args) {
        String input = readFile("in.txt");
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            while (!tokenizer.isEmpty())
                tokens.append(tokenizer.getNextToken());
            writeTokensToFile("out.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Reads file from given directory
     * Returns text from file located at fileDir
     */
    public static String readFile(String fileDir) {
        //Read file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(fileDir))) {
            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                result = result.concat(line) + "\n";
            }
            return result;
        } catch (IOException e) {
            System.out.println("Error was encountered during loading of the file: " + fileDir);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes all JAVA comments from given string
     * Returns input without comments
     */


    /**
     * Outputs current token list state to specified file
     */
    static void writeTokensToFile(String outputDir) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outputDir));
            out.write(tokens.toString());
            out.close();
        } catch (IOException e) {
            System.out.println("An error was encountered during writing to the file: " + outputDir);
            System.out.println(e.getStackTrace());
        }

    }
}
