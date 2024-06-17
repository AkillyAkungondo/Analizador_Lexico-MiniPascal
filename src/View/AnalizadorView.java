package View;

import Analizer.Lexer;
import Analizer.Token;
import Controller.Controller;
import Controller.Controller;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AnalizadorView extends JFrame {

    private JTextArea sourceCodeTextArea;
    private JButton analyzeButton;
    private JTable tokenTable;
    private JLabel elapsedTimeLabel;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    private LineNumberPanel lineNumberPanel;  // Painel para números de linha

    public AnalizadorView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Analizador Lexico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Label "Analizador Lexico"
        JLabel analizadorLexicoLabel = new JLabel("Analizador Lexico");
        analizadorLexicoLabel.setBounds(20, 20, 200, 30);
        add(analizadorLexicoLabel);

        // JTextArea para o código fonte
        sourceCodeTextArea = new JTextArea();
        lineNumberPanel = new LineNumberPanel(sourceCodeTextArea); // Inicializa o painel de números de linha
        scrollPane1 = new JScrollPane(sourceCodeTextArea);
        scrollPane1.setRowHeaderView(lineNumberPanel);  // Define o painel de números de linha como row header
        scrollPane1.setBounds(40, 60, 400, 300);
        add(scrollPane1);

        // Botão "Analisar"
        analyzeButton = new JButton("Analisar");
        analyzeButton.setBounds(20, 370, 100, 30);
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeButtonActionPerformed(e);
            }
        });
        add(analyzeButton);

        // JTable para exibir tokens
        tokenTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Lexema", "Token", "Linha"}
        ));
        scrollPane2 = new JScrollPane(tokenTable);
        scrollPane2.setBounds(460, 60, 400, 300);
        add(scrollPane2);

        // JLabel para exibir o tempo de execução
        elapsedTimeLabel = new JLabel("Tempo de Execução: 0 ms");
        elapsedTimeLabel.setBounds(20, 410, 200, 30);
        add(elapsedTimeLabel);

        // Listener para atualizar números de linha ao redimensionar a JTextArea
        sourceCodeTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });

        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    private void analyzeButtonActionPerformed(ActionEvent evt) {
        String sourceCode = sourceCodeTextArea.getText();

        if (sourceCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o código fonte.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Controller controller = new Controller(new Lexer(sourceCode), this);
        controller.analyzeSourceCode(sourceCode);
    }

    public void displayTokens(List<Token> tokens, long elapsedTime) {
        DefaultTableModel model = (DefaultTableModel) tokenTable.getModel();
        model.setRowCount(0);

        for (Token token : tokens) {
            model.addRow(new Object[]{token.getLexema(), token.getType(), token.getLinha()});
        }

        elapsedTimeLabel.setText("Tempo de Execução: " + elapsedTime + " ms");
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro Léxico", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnalizadorView().setVisible(true));
    }

    // Classe interna para desenhar números de linha na JTextArea
    private static class LineNumberPanel extends JPanel {
        private JTextArea textArea;

        public LineNumberPanel(JTextArea textArea) {
            this.textArea = textArea;
            FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
            int lineHeight = fontMetrics.getHeight();
            setPreferredSize(new Dimension(calculateWidth(), textArea.getHeight()));
        }

        private static int calculateWidth() {
            int lines = 0;
            int maxLines = 999; // Número máximo de linhas para exibir (ajuste conforme necessário)
            while (lines < maxLines) {
                lines++;
            }
            return 10 + 7 * String.valueOf(lines).length();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setFont(textArea.getFont());
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int lineHeight = fontMetrics.getHeight();

            Rectangle clip = g2d.getClipBounds();
            int startOffset = textArea.viewToModel(new Point(0, clip.y));
            int endOffset = textArea.viewToModel(new Point(0, clip.y + clip.height));

            String text = textArea.getText();
            int startLineNumber = lineNumberAtOffset(text, startOffset);
            int endLineNumber = lineNumberAtOffset(text, endOffset);

            int y = 0;
            for (int lineNumber = startLineNumber; lineNumber <= endLineNumber; ++lineNumber) {
                String line = String.valueOf(lineNumber);
                int x = getWidth() - fontMetrics.stringWidth(line) - 5;
                y += lineHeight;
                g2d.drawString(line, x, y);
            }
            g2d.dispose();
        }

        private int lineNumberAtOffset(String text, int offset) {
            int lines = 1;
            for (int i = 0; i < offset && i < text.length(); ++i) {
                if (text.charAt(i) == '\n') {
                    ++lines;
                }
            }
            return lines;
        }
    }

    // Método para atualizar os números de linha
    private void updateLineNumbers() {
        lineNumberPanel.repaint();
    }
}
