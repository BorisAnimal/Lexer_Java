public class Keywords extends Container {
    static final String[] keywords = {
            "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};

    static final String token = "KEYWORD";

    @Override
    public boolean isIn(String input) {
        for (String str : keywords) {
            if (input != null && input.equals(str))
                return true;
        }
        return false;
    }

    @Override
    public String getToken() {
        return token;
    }
}
