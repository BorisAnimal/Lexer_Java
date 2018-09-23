import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String input = readFile("in.txt");
        try {
            lexer(processComments(input));
            writeTokensToFile("out.txt");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Stack: " + stack);
        }
    }

    //Reads all files in the specified directory into list of strings
    //where each element contains the text of the corresponding file. It can also ignore all files
    //that don't have .java extensions by specifying javaOnly parameter
    //Returns ArrayList of strings with source codes of the files located at inputDir
    public static ArrayList<String> getTestFiles(String inputDir, boolean javaOnly)
    {
        //Get directory file
         File folder = new File(inputDir);
         ArrayList<String> parsedFiles = new ArrayList<String>();
         File[] files = folder.listFiles();
         //For all files in the folder
         for(int i = 0; i < files.length; i++)
         {
             //Skip if not .java file
             if(!files[i].getName().endsWith(".java") && javaOnly)
                 continue;

             //Get cwd to generate file path for BufferedReader
             String fileDir = System.getProperty("user.dir")+"/" + inputDir +"/"+files[i].getName();
             parsedFiles.add(readFile(fileDir));
         }
         return parsedFiles;
    }

    //Reads file from given directory
    //Returns text from file located at fileDir
    public static String readFile(String fileDir)
    {
        //Read file line by line
        try(BufferedReader reader = new BufferedReader(new FileReader(fileDir)))
        {
            String result = "";
            String line = "";
            while((line = reader.readLine()) != null)
            {
                result = result.concat(line) + "\n";
            }
            //Output parsed file
            System.out.println(result);
            return result;
        }
        catch (IOException e)
        {
            System.out.println("Error was encountered during loading of the file: " + fileDir);
            e.printStackTrace();
        }
        return null;
    }


    //Removes all JAVA comments from given string
    //Returns input without comments
    private static String processComments(String input)
    {
        //Commentary regexpr
        String commentRegex = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)";
        Pattern pattern = Pattern.compile(commentRegex);

        Matcher mat = pattern.matcher(input);

        while (mat.find())
            System.out.println("recognized: COMMENT " + mat.group());

        //Return formatted input
        return input.replaceAll(commentRegex,"");
    }

    //Outputs current token list state to specified file
    private static void writeTokensToFile(String outputDir)
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outputDir));
            String[] tkns = tokens.toString().split("\n");
            for(String t : tkns)
            {
                out.write(t);
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("An error was encountered during writing to the file: " + outputDir );
            System.out.println(e.getStackTrace());
        }

    }

    private static String stack = "";
    private static StringBuilder tokens;

    private static void lexer(String input) {
        stack = "";
        tokens = new StringBuilder();

        String tmp;
        for (Character c : input.toCharArray()) {

            //If current token is an Annotation keep reading it until c becomes a whitespace or \n
            if(stack.length()!=0 && (stack.charAt(0) == '@'|| c == '@') && !Character.isWhitespace(c))
            {
                stack += c;
                continue;
            }

            //If current token starts with a quote sign keep reading it until it closes
            if(stack.length()!=0 && (stack.charAt(0) == '\"' || stack.charAt(0)=='\'')) {
                //Try to parse
                String littmp = parseString(stack+c);
                //If no closing quote sign keep reading c
                if (littmp == null){
                    stack += c;
                    continue;
                }
                else
                {
                    //Add new token and print it
                    tokens.append(littmp + " " + stack + c + "\n");
                    System.out.println(String.format("recognized: %s (%s)", stack + c, littmp));
                    stack = "";
                    continue;
                }

            }

            // Token ended with space or \n or tab -> get token from @stack
            if (Character.isWhitespace(c))
                flushStack();
                // token probably continues, so try to add new char and find relevant pattern
            else {
                //If token is a numeric value
                if(stack.length()!=0 && Character.isDigit(stack.charAt(0)) )
                {
                    stack += c;

                    //Check all substring of the token for the presence of the syntax token
                    for(int i = 0 ;i<stack.length(); i++)
                    {
                        String numtmp;
                        String substring = stack.substring(i);

                        tmp = parseSyntax(stack.substring(i));

                        //Making sure literals and DOT are not recognised as identifiers
                        if(substring.contains("h")||substring.contains("x")||substring.contains("f")||substring.contains("."))
                            continue;

                        if(tmp == null)
                            continue;
                        else
                        {
                            //If the syntax token is found try to split the stack and parse numeric value
                            numtmp = parseNumeric(stack.substring(0,i));
                            if(numtmp!=null)
                            {
                                //If numeric value is recognized add it to all tokens
                                System.out.println(String.format("recognized: %s (%s)", stack.substring(0,i), numtmp));
                                tokens.append(numtmp + " " + stack.substring(0,i)  + "\n");
                                //Now leave only syntax token in the stack
                                stack = stack.substring(i);
                            }
                            //And flush it
                            flushStack();
                        }
                    }
                    //Keep parsing till the stack is flushed and both numeric and next syntax values are found
                    continue;
                }

                tmp = parseSyntax(stack + c);
                // No such pattern, so undo and try parse from previous token
                if (tmp == null) {
                    flushStack();
                    assert stack.length() == 0;
                    stack = c + "";
                }

                // Still token continues, append symbol and go to next iteration
                else
                    stack += c;
            }
        }

        if (stack != null && stack.length() > 0)
            flushStack();
    }

    private static void flushStack() {
        if (stack != null && stack.length() > 0) {
            String tmp = parseSyntax(stack);
            tokens.append(tmp + " " + stack  + "\n");
            System.out.println(String.format("recognized: %s (%s)", stack, tmp));
            stack = "";
        }
    }

    //Parses syntax related tokens
    private static String parseSyntax(String input) {
        for (Container c : SYNTAX_PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
    }

    //Parses hex, int, oct and float tokens
    private static String parseNumeric(String input) {
        for (Container c : NUMERIC_PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
    }

    //Parses string and char tokens
    private static String parseString(String input) {
        for (Container c : STRING_PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
    }

    private static Container[] NUMERIC_PATTERNS = {
            new Regexp("HEX", "(0x[0-9A-F]+)|([0-9A-F]+h)"),
            new Regexp("INTEGER", "([1-9]+[0-9]*)|(0)"),
            new Regexp("OCTAL", "0[0-7]+"),
            new Regexp("FLOAT", "[0-9]*(.[0-9]+)f"),
    };


    private static Container[] STRING_PATTERNS = {
            new Regexp("CHAR", "'[a-zA-Z]'"),
            new Regexp("STRING", "\"[^\"]*\"")
    };

    private static Container[] SYNTAX_PATTERNS = {
            new Keywords(),
            new ConstString("OPEN_CURL_BRACKET", "{"),
            new ConstString("CLOSE_CURL_BRACKET", "}"),
            new ConstString("OPEN_BRACKET", "("),
            new ConstString("CLOSE_BRACKET", ")"),
            new Regexp("BOOLEAN_LITERAL", "(TRUE|true|True|FALSE|false|False)"),
            new Regexp("OPERATOR", "(\\+|-)"),
            new Regexp("OPERATOR", "(\\*|/|%)"),
            new Regexp("OPERATOR", "(>|<|==|=|<=|>=|!=)"),
            new Regexp("OPERATOR", "(>>|<<)"),
            new Regexp("UNAR_OPERATOR", "(\\+\\+|--)"),
            new ConstString("IF_OPERATOR", "if"),
            new ConstString("ELSE_OPERATOR", "if"),
            new Regexp("TERNARY_OPERATOR", "[&,\\|]"),
            new Regexp("QUICK_OPERATOR", "(\\+=|-=|\\*=|/=)"),
            new Regexp("DIFFICULT_TO_PARSE_OPERATOR", ""),
            new Regexp("STRUCTURE_REFERENCE", "^(([a-zA-Z_$])([a-zA-Z_$0-9])*.)+(([a-zA-Z_$])([a-zA-Z_$0-9])*)$"),
            new ConstString("OPEN_ARROW", "<"),
            new ConstString("CLOSE_ARROW", ">"),
            new ConstString("COMMA", ","),
            new ConstString("OPEN_SQUARE_BRACKET", "["),
            new ConstString("CLOSE_SQUARE_BRACKET", "]"),
            new ConstString("SEMICOLON", ";"),
            new ConstString("COLON", ":"),
            new ConstString("DOT", "."),
            new Regexp("NULL_LITERAL", "(Null|NULL|null)"),
            new Regexp("IDENTIFIER", "^([a-zA-Z_$])([a-zA-Z_$0-9])*$"),
            new Regexp("ANNOTATION", "(@Override)|(@Deprecated)|(@SupressWarnings)|(@SafeVargs)|(@Retention)|(@Documented)")
    };


}
