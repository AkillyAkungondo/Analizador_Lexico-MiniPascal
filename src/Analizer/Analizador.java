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
        boolean inString = false;

        // Itera sobre as linhas do código fonte
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Remove comentários de linha e verifica se está dentro de um bloco de comentário
            line = removeComments(line);

            // Adiciona espaços em pontuações para facilitar a análise
            line = addSpacesToPunctuation(line);
            String[] words = line.split("\\s+");

            // Itera sobre as palavras da linha
            for (String word : words) {
                // Verifica estados de strings
                if (inString) {
                    handleStringToken(word, tokens, i);
                } else {
                    analyzeToken(word, tokens, i);
                }
            }
             if (!line.trim().endsWith(";")) {
                System.err.println("Erro léxico na linha " + (i + 1) + ": A linha não termina com o fechamento de um bloco.");
            }
        }
        

        return tokens;
    }

    // Remove comentários de linha e verifica se está dentro de um bloco de comentário
    private String removeComments(String line) {
        int commentIndex = line.indexOf("//");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }

        if (line.contains("/*")) {
            int startCommentIndex = line.indexOf("/*");
            if (line.contains("*/")) {
                int endCommentIndex = line.indexOf("*/");
                if (endCommentIndex > startCommentIndex) {
                    line = line.substring(0, startCommentIndex) + line.substring(endCommentIndex + 2);
                } else {
                    line = line.substring(0, startCommentIndex);
                }
            } else {
                line = line.substring(0, startCommentIndex);
            }
        }

        return line;
    }

    // Adiciona espaços antes e depois de pontuações em uma linha de código
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
        } else if (c == '(' || c == ')') { // Adicionando verificação para parênteses
            if (i > 0 && !isInQuotes(line, i) && !Character.isWhitespace(line.charAt(i - 1))) {
                sb.append(" ");
            }
            sb.append(c);
            if (i < line.length() - 1 && !isInQuotes(line, i) && !Character.isWhitespace(line.charAt(i + 1))) {
                sb.append(" ");
            }
        } else {
            sb.append(c);
        }
    }

    return sb.toString();
}


    // Verifica e manipula tokens
    private void analyzeToken(String word, List<Token> tokens, int lineNumber) {
        if (isStringStart(word)) {
            tokens.add(new Token(word.substring(1), "String", lineNumber + 1));
        } else if (isStringEnd(word)) {
            tokens.add(new Token(word.substring(0, word.length() - 1), "String", lineNumber + 1));
        } else if (isSeparator(word)) {
            tokens.add(new Token(word, "Separator", lineNumber + 1));
        } else if (isReservedKeyword(word)) {
            tokens.add(new Token(word, "Reserved Keyword", lineNumber + 1));
        } else if (isRelationalOperator(word)) {
            tokens.add(new Token(word, "Relational Operator", lineNumber + 1));
        } else if (isArithmeticOperator(word)) {
            tokens.add(new Token(word, "Arithmetic Operator", lineNumber + 1));
        } else if (isIdentifier(word)) {
            tokens.add(new Token(word, "Identifier", lineNumber + 1));
        } else if (isNumber(word)) {
            tokens.add(new Token(word, "Number", lineNumber + 1));
        }
        else{tokens.add(new Token(word,"erro",lineNumber+1));
            }
        }
    

    // Manipula o token quando está dentro de uma string
    private void handleStringToken(String word, List<Token> tokens, int lineNumber) {
    if (isStringStart(word)) {
        // Se a palavra for o início de uma string, remove o último caractere,
        // que indica o início de uma nova string na mesma palavra, e cria um token.
        String stringContent = word.substring(0, word.length() - 1);
        tokens.add(new Token(stringContent, "String", lineNumber + 1));
    } else {
        // Caso contrário, a palavra representa uma string completa e é adicionada como um token.
        tokens.add(new Token(word, "String", lineNumber + 1));
    }
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
    // Verifica se a palavra contém algum dos caracteres separadores
    return word.contains(".") || word.contains(",") || word.contains(";") ||
           word.contains(":") || word.contains("[") || word.contains("]") ||
           word.contains("(") || word.contains(")") || word.contains("{") ||
           word.contains("}");
}
}
