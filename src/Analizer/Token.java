package Analizer;

public class Token {
    private final TokenType type;
    private final String lexema;
    private final int linha;

    public Token(TokenType type, String lexema, int linha) {
        this.type = type;
        this.lexema = lexema;
        this.linha = linha;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexema='" + lexema + '\'' +
                ", linha=" + linha +
                '}';
    }
}
