import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private String stack;
    private ArrayList<String> tokens;


    public Tokenizer(String input) {
        stack = "";
        tokens = new ArrayList<>();
        toTokens(processComments(input));
    }


    /**
     * @param input raw program code
     * @return program code without comment lines
     */
    private static String processComments(String input) {
        //Commentary regexpr
        String commentRegex = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)";
        Pattern pattern = Pattern.compile(commentRegex);

        Matcher mat = pattern.matcher(input);

        while (mat.find())
            System.out.println("recognized: COMMENT " + mat.group());

        //Return formatted input
        return input.replaceAll(commentRegex, "");
    }

    /**
     * @return next token of program source
     */
    public String getNextToken() {
        if (tokens.size() > 0)
            return tokens.remove(0);
        else
            return null;
    }

    /**
     * @return true if parsing is finished (input is empty) false otherwise
     */
    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    private void toTokens(String input) {
        String tmp;
        for (Character c : input.toCharArray()) {

            //If current token is an Annotation keep reading it until c becomes a whitespace or \n
            if (stack.length() != 0 && (stack.charAt(0) == '@' || c == '@') && !Character.isWhitespace(c)) {
                stack += c;
                continue;
            }

            //If current token starts with a quote sign keep reading it until it closes
            if (stack.length() != 0 && (stack.charAt(0) == '\"' || stack.charAt(0) == '\'')) {
                //Try to parse
                String littmp = parseString(stack + c);
                //If no closing quote sign keep reading c
                if (littmp == null) {
                    stack += c;
                    continue;
                } else {
                    //Add new token and print it
                    tokens.add(littmp + " " + stack + c + "\n");
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
                if(((stack.length() == 0 && Character.isDigit(c))) || (stack.length()!=0 && Character.isDigit(stack.charAt(0))) ) {
                    stack += c;

                    //Check all substring of the token for the presence of the syntax token
                    for (int i = 0; i < stack.length(); i++) {
                        String numtmp;
                        String substring = stack.substring(i);

                        tmp = parseSyntax(stack.substring(i));

                        //Making sure literals and DOT are not recognised as identifiers
                        if (substring.contains("h") || substring.contains("x") ||
                                substring.contains("f") || substring.contains("."))
                            continue;

                        if (tmp != null) {
                            //If the syntax token is found try to split the stack and parse numeric value
                            numtmp = parseNumeric(stack.substring(0, i));
                            if (numtmp != null) {
                                //If numeric value is recognized add it to all tokens
                                //System.out.println(String.format("recognized: %s (%s)", stack.substring(0, i), numtmp));
                                tokens.add(numtmp + " " + stack.substring(0, i) + "\n");
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
        tokens.add(EOF);
    }


    /**
     * returns Description of Token stored in @stack variable and
     * refresh @stack variable at the end.
     */
    private void flushStack() {
        if (stack != null && stack.length() > 0) {
            String tmp = parseSyntax(stack);
            if(tmp == null)
                tmp = parseNumeric(stack);
            tokens.add(tmp + " " + stack + "\n");
            System.out.println(String.format("recognized: %s (%s)", stack, tmp));
            stack = "";
        }
    }

    //Parses syntax related tokens
    private String parseSyntax(String input) {
        for (Container c : SYNTAX_PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
    }

    //Parses hex, int, oct and float tokens
    private String parseNumeric(String input) {
        for (Container c : NUMERIC_PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
    }

    //Parses string and char tokens
    private String parseString(String input) {
        for (Container c : STRING_PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
    }

    private final static String EOF = "EOF";

    private final static Container[] NUMERIC_PATTERNS = {
            new Regexp("HEX", "(0x[0-9A-F]+)|([0-9A-F]+h)"),
            new Regexp("INTEGER", "([1-9]+[0-9]*)|(0)"),
            new Regexp("OCTAL", "0[0-7]+"),
            new Regexp("FLOAT", "[0-9]*(.[0-9]+)f"),
    };


    private final static Container[] STRING_PATTERNS = {
            new Regexp("CHAR", "'[a-zA-Z]'"),
            new Regexp("STRING", "\"[^\"]*\"")
    };


    private final static Container[] SYNTAX_PATTERNS = {
            new Keywords(),
            new ConstString("OPEN_CURL_BRACKET", "{"),
            new ConstString("CLOSE_CURL_BRACKET", "}"),
            new ConstString("OPEN_BRACKET", "("),
            new ConstString("CLOSE_BRACKET", ")"),
            new ConstString("OPEN_ARROW", "<"),
            new ConstString("CLOSE_ARROW", ">"),
            new Regexp("BOOLEAN_LITERAL", "(TRUE|true|True|FALSE|false|False)"),
            new Regexp("UNAR_OPERATOR", "(\\+\\+|--)"),
            new Regexp("OPERATOR", "(\\+|-)"),
            new Regexp("OPERATOR", "(\\*|/|%)"),
            new Regexp("OPERATOR", "(>>|<<)"),
            new Regexp("OPERATOR", "(>|<|==|=|<=|>=|!=)"),
            new ConstString("IF_OPERATOR", "if"),
            new ConstString("ELSE_OPERATOR", "else"),
            new Regexp("LOGIC_OPERATOR", "[&,\\|]"),
            new Regexp("QUICK_OPERATOR", "(\\+=|-=|\\*=|/=)"),
            new Regexp("STRUCTURE_REFERENCE",
                    "^(([a-zA-Z_$])([a-zA-Z_$0-9])*.)+(([a-zA-Z_$])([a-zA-Z_$0-9])*)$"),
            new ConstString("COMMA", ","),
            new ConstString("OPEN_SQUARE_BRACKET", "["),
            new ConstString("CLOSE_SQUARE_BRACKET", "]"),
            new ConstString("SEMICOLON", ";"),
            new ConstString("COLON", ":"),
            new ConstString("DOT", "."),
            new Regexp("NULL_LITERAL", "(Null|NULL|null)"),
            new Regexp("IDENTIFIER", "^([a-zA-Z_$])([a-zA-Z_$0-9])*$"),
            new Regexp("ANNOTATION", "(@Override)|(@Deprecated)|" +
                    "(@SupressWarnings)|(@SafeVargs)|(@Retention)|(@Documented)"),
            new Regexp("ANNOTATION", "^@(([a-zA-Z_$])([a-zA-Z_$0-9])*.)+(([a-zA-Z_$])([a-zA-Z_$0-9])*)$")
    };


}
