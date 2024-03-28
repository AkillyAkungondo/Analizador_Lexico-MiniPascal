package Analizer;

import Controller.LexicalException;
import java.util.ArrayList;
import java.util.List;

public class Analizador {

    // Palavras-chave reservadas na linguagem
    private static final String[] RESERVED_KEYWORDS = {
        "program", "var", "begin", "end", "integer", "real", "if", "then", "else", "while", "do", "write", "writeln", "read", "readln", "case", "of"
    };

    // Operadores relacionais permitidos
    private static final String[] RELATIONAL_OPERATORS = {
        "=", "<>", "<", "<=", ">=", ">", "or", "and"
    };

    // Operadores aritméticos permitidos
    private static final String[] ARITHMETIC_OPERATORS = {
        "+", "-", "*", "/"
    };

    /**
     * Método responsável por analisar o código fonte e gerar a lista de tokens.
     *
     * @param sourceCode O código fonte a ser analisado.
     * @return Uma lista de tokens gerados a partir do código fonte.
     * @throws LexicalException Se ocorrer um erro léxico durante a análise.
     */
    public List<Token> analyze(String sourceCode) throws LexicalException {
        List<Token> tokens = new ArrayList<>();
        String[] lines = sourceCode.split("\\r?\\n");

        // Variáveis para controle de estados
        boolean inComment = false;
        boolean inString = false;
        StringBuilder stringToken = new StringBuilder();

        // Itera sobre as linhas do código fonte
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Remove comentários de linha
            int commentIndex = line.indexOf("//");
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex);
            }

            // Verifica se está dentro de um bloco de comentário
            if (inComment) {
                int endCommentIndex = line.indexOf("*/");
                if (endCommentIndex != -1) {
                    inComment = false;
                    line = line.substring(endCommentIndex + 2);
                } else {
                    continue;
                }
            }

            // Adiciona espaços em pontuações para facilitar a análise
            line = addSpacesToPunctuation(line);
            String[] words = line.split("\\s+");

            // Itera sobre as palavras da linha
            for (String word : words) {
                // Verifica estados de strings e comentários
                if (inString) {
                    if (isStringEnd(word)) {
                        // Fim da string
                        inString = false;
                        stringToken.append(" ").append(word, 0, word.length() - 1); // Anexar palavra sem aspas de fechamento
                        tokens.add(new Token(stringToken.toString(), "String", i + 1));
                        stringToken.setLength(0);
                    } else {
                        // Esta palavra faz parte da string, anexar sem processamento adicional
                        stringToken.append(" ").append(word);
                        continue; // Pular processamento adicional
                    }
                } else if (isStringStart(word)) {
                    inString = true;
                    if (word.length() > 1 && word.charAt(0) == word.charAt(word.length() - 1)) {
                        // Se a string tem apenas um caractere, então o início e o fim são iguais
                        tokens.add(new Token(word.substring(1, word.length() - 1), "String", i + 1));
                    } else {
                        stringToken.append(word.substring(1)); // Anexar palavra sem aspas de abertura
                    }
                } else if (isSeparator(word)) {
                    tokens.add(new Token(word, "Punctuation", i + 1));
                } else if (isReservedKeyword(word)) {
                    tokens.add(new Token(word, "Reserved Keyword", i + 1));
                } else if (isRelationalOperator(word)) {
                    tokens.add(new Token(word, "Relational Operator", i + 1));
                } else if (isArithmeticOperator(word)) {
                    tokens.add(new Token(word, "Arithmetic Operator", i + 1));
                } else if (isIdentifier(word)) {
                    tokens.add(new Token(word, "Identifier", i + 1));
                } else if (isNumber(word)) {
                    tokens.add(new Token(word, "Number", i + 1));
                } else if (isCommentStart(word)) {
                    tokens.add(new Token(word, "Comment", i + 1));
                } else {
                    //   tokens.add(new Token(word, "Token não reconhecido" + (i + 1)));
                }
            }
        }

        return tokens;
    }

    // Adiciona espaços antes e depois de pontuações em uma linha de código.
    private String addSpacesToPunctuation(String line) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isPunctuation(c)) {
                if (i > 0 && !isInQuotes(line, i) && !isPunctuation(line.charAt(i - 1)) && !Character.isWhitespace(line.charAt(i - 1))) {
                    sb.append(" ");
                }
                sb.append(c);
                if (i < line.length() - 1 && !isInQuotes(line, i) && !isPunctuation(line.charAt(i + 1)) && !Character.isWhitespace(line.charAt(i + 1))) {
                    sb.append(" ");
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
    // Métodos auxiliares para análise léxica

    private boolean isInQuotes(String line, int index) {
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;

        for (int i = 0; i < index; i++) {
            char c = line.charAt(i);
            if (c == '\'') {
                inSingleQuotes = !inSingleQuotes;
            } else if (c == '"') {
                inDoubleQuotes = !inDoubleQuotes;
            }
        }

        return inSingleQuotes || inDoubleQuotes;
    }

    private boolean isPunctuation(char c) {
        return Character.getType(c) == Character.OTHER_PUNCTUATION;
    }

    private boolean isReservedKeyword(String word) {
        for (String keyword : RESERVED_KEYWORDS) {
            if (keyword.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRelationalOperator(String word) {
        for (String operator : RELATIONAL_OPERATORS) {
            if (operator.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean isArithmeticOperator(String word) {
        for (String operator : ARITHMETIC_OPERATORS) {
            if (operator.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIdentifier(String word) {
        // Pular verificação se a palavra está entre aspas
        if (word.startsWith("'") || word.startsWith("\"")) {
            return false;
        }
        return word.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private boolean isNumber(String word) {
        return word.matches("\\d+(\\.\\d+)?");
    }

    //Verifica se uma palavra é o início de uma string.
    private boolean isStringStart(String word) {
        return (word.startsWith("'") && word.endsWith("'")) || (word.startsWith("\"") && word.endsWith("\"")) || (word.startsWith("\"") && word.endsWith("'")) || (word.startsWith("'") && word.endsWith("\""));
    }

    // Verifica se uma palavra é o fim de uma string.
    private boolean isStringEnd(String word) {
        return word.endsWith("'") || word.endsWith("\"");
    }

    //verifica se e inicio de comentario
    private boolean isCommentStart(String word) {
        return word.startsWith("/*") && !word.endsWith("*/");
    }

    //verifica se a palavra e um separador
    private boolean isSeparator(String word) {
        return word.matches("[.,;:\\[\\](){}]");
    }
}
