import java.util.regex.Pattern;

public class Regexp extends Container {

    private String token;
    private Pattern regexp;

    public Regexp(String token, String regexp) {
        this.token = token;
        this.regexp = Pattern.compile(regexp);
    }

    /**
     *
     * @param str checks if given string apply regexp from constructor
     */
    @Override
    public boolean isIn(String str) {
        return regexp.matcher(str).matches();
    }

    /**
     * @return given name for token from constructor
     */
    @Override
    public String getToken() {
        return token;
    }
}
