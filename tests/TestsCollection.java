import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TestsCollection {

    String testAllTokens(String input) {
        StringBuilder sb = new StringBuilder();
        assertTrue(input != null);
        assertTrue(input.length() > 0);

        Tokenizer tok = new Tokenizer(input);
        while (!tok.isEmpty())
            sb.append(tok.getNextToken());

        sb.toString().trim().endsWith("EOF");

        return sb.toString();
    }


    @Test
    void codeWithSyntaxErrors() throws Exception {
        String testInp1 = Main.readFile("tests\\test1.txt");
        testAllTokens(testInp1);
    }

    @Test
    void myOldCode() throws Exception {
        String testInp1 = Main.readFile("tests\\test2.txt");
        testAllTokens(testInp1);
    }

    @Test
    void oracleCode() throws Exception {
        String testInp1 = Main.readFile("tests\\test3.txt");
        testAllTokens(testInp1);
    }


    @Test
    void psvmInput() throws Exception {
        String tmp = testAllTokens("public static void main");
//        System.out.println(tmp);
        assertEquals("KEYWORD public\n" +
                "KEYWORD static\n" +
                "KEYWORD void\n" +
                "STRUCTURE_REFERENCE main\n" +
                "EOF", tmp.trim());
    }

    @Test
    void intInput() throws Exception {
        String tmp = testAllTokens("1000;");
//        System.out.println(tmp);
        assertEquals("INTEGER 1000\n" +
                "SEMICOLON ;\n" +
                "EOF", tmp.trim());
    }

    @Test
    void emptyInput() {
        Tokenizer tok = new Tokenizer("");
        assertEquals("EOF", tok.getNextToken());
    }
}