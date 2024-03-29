package Controller;

import Analizer.Analizador;
import Analizer.Token;
import View.AnalizadorView;
import java.util.List;

public class Controller {
    private Analizador analizador;
    private AnalizadorView analizadorView;

    /**
     * Construtor da classe Controller.
     * 
     * @param analizador O analisador léxico a ser utilizado.
     * @param analizadorView A view onde os tokens serão exibidos.
     */
    public Controller(Analizador analizador, AnalizadorView analizadorView) {
        this.analizador = analizador;
        this.analizadorView = analizadorView; // Atribui o objeto AnalizadorView fornecido
    }

    /**
     * Analisa o código fonte fornecido.
     * 
     * @param sourceCode O código fonte a ser analisado.
     */
    public void analyzeSourceCode(String sourceCode) {
        // Verifica se o código fonte está vazio ou contém apenas espaços em branco
        if (sourceCode.trim().isEmpty()) {
            // Se o código fonte estiver vazio, exibe uma mensagem de erro na tabela
            analizadorView.displayError("O código fonte está vazio.");
            return;
        }

        try {
            // Inicia a contagem do tempo de análise
            long startTime = System.currentTimeMillis();

            // Realiza a análise léxica do código fonte para gerar a lista de tokens
            List<Token> tokens = analizador.analyze(sourceCode);

            // Calcula o tempo decorrido durante a análise
            long elapsedTime = System.currentTimeMillis() - startTime;

            // Exibe os tokens na tabela, juntamente com o tempo de análise
            analizadorView.displayTokens(tokens, elapsedTime);
        } catch (LexicalException e) {
            // Se ocorrer uma exceção durante a análise léxica, exibe a mensagem de erro na tabela
            analizadorView.displayError(e.getMessage());
        }
    }
}
