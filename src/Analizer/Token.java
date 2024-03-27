package Analizer;

public class Token {
    private String lexema;
    private String tokenClass;
    private int linha;

    public Token(String lexema, String tokenClass, int linha) {
        this.lexema = lexema;
        this.tokenClass = tokenClass;
        this.linha = linha;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTokenClass() {
        return tokenClass;
    }

    public int getLinha() {
        return linha;
    }
    
    
    @Override
        public String toString() {
        return "Token [Lexema=" + lexema + ", TokenClass=" + tokenClass + ", Linha=" + linha + "]";
    }
        
}

