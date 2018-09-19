public class Main {


    //TODO: предобработка комментариев

    public static void main(String[] args) {
        String input = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World!\");\n" +
                "    }\n" +
                "}";

//        String input = ");";

        try {
            doAllWork(input);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Stack: " + stack);
        }

    }

    private static String stack = "";
    private static StringBuilder tokens = new StringBuilder();

    private static void doAllWork(String input) {
        String tmp;
        for (Character c : input.toCharArray()) {
            // Token ended with space or \n or tab -> get token from @stack
            if (Character.isWhitespace(c))
                flushStack();
                // token probably continues, so try to add new char and find relevant pattern
            else {
                tmp = parse(stack + c);
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
            String tmp = parse(stack);
            tokens.append(tmp + " " + stack);
            System.out.println(String.format("recognized: %s (%s)", stack, tmp));
            stack = "";
        }
    }

    private static String parse(String input) {
        for (Container c : PATTERNS) {
            if (c.isIn(input)) {
                return c.getToken();
            }
        }
        return null;
//        System.out.println("stack: " + stack);
//        throw new Exception("No such token");
    }

    private static Container[] PATTERNS = {
            new Keywords(),
            new ConstString("OPEN_CURL_BRACKET", "{"),
            new ConstString("CLOSE_CURL_BRACKET", "}"),
            new ConstString("OPEN_BRACKET", "("),
            new ConstString("CLOSE_BRACKET", ")"),
//            new Regexp("INTEGER", ), //TODO
//            new Regexp("FLOAT", ),//TODO
//            new Regexp("HEX", ),//TODO
//            new Regexp("OCT", ),//TODO
            new Regexp("BOOLEAN_LITERAL", "(TRUE|true|True|FALSE|false|False)"),
//            new Regexp("CHAR", ),//TODO
//            new Regexp("STRING", ),//TODO
            new Regexp("OPERATOR", "(\\+|-)"),
            new Regexp("OPERATOR", "(\\*|/|%)"),
            new Regexp("OPERATOR", "(>|<|==|=|<=|>=|!=)"),
            new Regexp("OPERATOR", "(>>|<<)"),
            new Regexp("UNAR_OPERATOR", "(\\+\\+|--)"),
            //TODO: ternary operand
            new Regexp("QUICK_OPERATOR", "(\\+=|-=|\\*=|/=)"),
            new Regexp("DIFFICULT_TO_PARSE_OPERATOR", ""),
            new Regexp("STRUCTURE_REFERENCE", "^(([a-zA-Z_$])([a-zA-Z_$0-9])*.)+(([a-zA-Z_$])([a-zA-Z_$0-9])*)$"),
            new ConstString("OPEN_ARROW", "<"),
            new ConstString("CLOSE_ARROW", ">"),
            new ConstString("DOT", "."),
            new ConstString("COMMA", ","),
            new ConstString("OPEN_SQUARE_BRACKET", "["),
            new ConstString("CLOSE_SQUARE_BRACKET", "]"),
            new ConstString("DOT_COMMA", ";"),
            new Regexp("NULL_LITERAL", "(Null|NULL|null)"),
            new Regexp("IDENTIFIER", "^([a-zA-Z_$])([a-zA-Z_$0-9])*$"),
    };


}
