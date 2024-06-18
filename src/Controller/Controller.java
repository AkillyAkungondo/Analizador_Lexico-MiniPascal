package Controller;

import Analizer.Lexer;
import Analizer.Token;
import View.AnalizadorView;

import java.util.List;

/**
 * Controller responsável por gerenciar a análise léxica do código fonte.
 */
public class Controller {

    private Lexer lexer;
    private AnalizadorView simpleAnalizadorView;

    /**
     * Construtor que inicializa o Controller com um Lexer e uma AnalizadorView.
     *
     * @param lexer                O Lexer para análise léxica.
     * @param simpleAnalizadorView A view para exibir resultados e erros.
     */
    public Controller(Lexer lexer, AnalizadorView simpleAnalizadorView) {
        this.lexer = lexer;
        this.simpleAnalizadorView = simpleAnalizadorView;
    }

    /**
     * Analisa o código fonte fornecido, exibindo tokens e tempo de execução na view.
     *
     * @param sourceCode O código fonte a ser analisado.
     */
    public void analyzeSourceCode(String sourceCode) {
        if (sourceCode.trim().isEmpty()) {
            simpleAnalizadorView.displayError("O código fonte está vazio.");
            return;
        }

        long startTime = System.currentTimeMillis();
        lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.analisar();
        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Número de Tokens: " + tokens.size());

        simpleAnalizadorView.displayTokens(tokens, elapsedTime);
    }
}
