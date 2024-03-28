package Controller;

import Analizer.Analizador;
import Analizer.Token;
import Controller.LexicalException;
import java.util.List;
import View.TableView;

public class Controller {
    private Analizador analizador;
    private TableView tableView; // Atributo TableView inicializado

    /**
     * Construtor da classe Controller.
     * 
     * @param analizador O analisador léxico a ser utilizado.
     * @param tableView A view da tabela onde os tokens serão exibidos.
     */
    public Controller(Analizador analizador, TableView tableView) {
        this.analizador = analizador;
        this.tableView = tableView; // Atribui o objeto TableView fornecido
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
            tableView.displayError("O código fonte está vazio.");
            return;
        }

        try {
            // Inicia a contagem do tempo de análise
            long startTime = System.currentTimeMillis();

            // Realiza a análise léxica do código fonte para gerar a lista de tokens
            List<Token> tokens = analizador.analyze(sourceCode);

            // Calcula o tempo decorrido durante a análise
            long elapsedTime = System.currentTimeMillis() - startTime;

            // Exibe o número de tokens encontrados na saída padrão
            System.out.println("Número de Tokens: " + tokens.size());

            // Exibe os tokens na tabela, juntamente com o tempo de análise
            tableView.displayTokens(tokens, elapsedTime);
        } catch (LexicalException e) {
            // Se ocorrer uma exceção durante a análise léxica, exibe a mensagem de erro na tabela
            tableView.displayError(e.getMessage());
        }
    }
}
