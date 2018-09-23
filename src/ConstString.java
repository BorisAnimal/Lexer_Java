import com.sun.istack.internal.NotNull;

public class ConstString extends Container {

    private String token;
    private String expr;

    public ConstString(@NotNull String token, @NotNull String expr) {
        this.token = token;
        this.expr = expr;
    }

    /**
     * @return true if @str exactly matches @expr from constructor
     */
    @Override
    public boolean isIn(String str) {
        return expr.equals(str);
    }

    /**
     * @return given name for token from constructor
     */
    @Override
    public String getToken() {
        return token;
    }
}
