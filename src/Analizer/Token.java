package Analizer;

/**
 * A classe Token representa um token léxico gerado durante a análise do código fonte.
 * Um token contém um lexema, uma classe de token e a linha em que foi encontrado no código fonte.
 */
public class Token {
    private String lexema;
    private String tokenClass;
    private int linha;

    /**
     * Construtor da classe Token.
     *
     * @param lexema O lexema associado ao token.
     * @param tokenClass A classe do token.
     * @param linha O número da linha onde o token foi encontrado no código fonte.
     */
    public Token(String lexema, String tokenClass, int linha) {
        this.lexema = lexema;
        this.tokenClass = tokenClass;
        this.linha = linha;
    }

    /**
     * Obtém o lexema associado ao token.
     *
     * @return O lexema do token.
     */
    public String getLexema() {
        return lexema;
    }

    /**
     * Obtém a classe do token.
     *
     * @return A classe do token.
     */
    public String getTokenClass() {
        return tokenClass;
    }

    /**
     * Obtém o número da linha onde o token foi encontrado no código fonte.
     *
     * @return O número da linha do token.
     */
    public int getLinha() {
        return linha;
    }
    
    /**
     * Retorna uma representação em string do token, incluindo seu lexema, classe e número da linha.
     *
     * @return Uma string representando o token.
     */
    @Override
    public String toString() {
        return "Token [Lexema=" + lexema + ", TokenClass=" + tokenClass + ", Linha=" + linha + "]";
    }
}
