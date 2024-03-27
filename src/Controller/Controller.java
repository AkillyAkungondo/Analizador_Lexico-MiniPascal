package Controller;

import Analizer.Analizador;
import Analizer.Token;
import Controller.LexicalException;
import java.util.List;
import javax.swing.text.View;
import View.TableView;

public class Controller {
    private Analizador analizador;
    private TableView tableview; // Atributo TableView inicializado

    /**
     * Construtor da classe Controller.
     * 
     * @param analizador O analisador léxico a ser utilizado.
     * @param tableview A view da tabela onde os tokens serão exibidos.
     */
    public Controller(Analizador analizador, TableView tableview) {
        this.analizador = analizador;
        this.tableview = tableview; // Atribui o objeto TableView fornecido
    }

    /**
     * Analisa o código fonte fornecido.
     * 
     * @param sourceCode O código fonte a ser analisado.
     */
    public void analyzeSourceCode(String sourceCode) {
        try {
            long startTime = System.currentTimeMillis();
            List<Token> tokens = analizador.analyze(sourceCode);
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Número de Tokens: " + tokens.size());
            tableview.displayTokens(tokens, elapsedTime); // Invoca displayTokens em tableview
       
        } catch (LexicalException e) {
            tableview.displayError(e.getMessage()); // Invoca displayError em tableview
        }
    }
}
