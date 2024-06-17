package Analizer;

import Analizer.Token;
import Analizer.TokenType;
import Controller.LexicalException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private String codigo;
    private int posicao;
    private int linhaAtual;
    private static final Map<String, TokenType> KEYWORDS;

    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("program", TokenType.PROGRAM);
        KEYWORDS.put("var", TokenType.VAR);
        KEYWORDS.put("begin", TokenType.BEGIN);
        KEYWORDS.put("end", TokenType.END);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("then", TokenType.THEN);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("do", TokenType.DO);
        KEYWORDS.put("integer", TokenType.INTEGER);
      
    }

    public Lexer(String codigo) {
        this.codigo = codigo;
        this.posicao = 0;
        this.linhaAtual = 1;
    }

    public List<Token> analisar() throws LexicalException {
        List<Token> tokens = new ArrayList<>();
        while (hasNext()) {
            tokens.add(nextToken());
        }
        tokens.add(new Token(TokenType.EOF, "", linhaAtual));
        return tokens;
    }

    private boolean hasNext() {
        return posicao < codigo.length();
    }

    private Token nextToken() throws LexicalException {
        skipWhitespace();
        if (!hasNext()) {
            return new Token(TokenType.EOF, "", linhaAtual);
        }

        char current = codigo.charAt(posicao);

        if (Character.isDigit(current)) {
            return extractNumber();
        } else if (Character.isLetter(current)) {
            return extractIdentifierOrKeyword();
        } else {
            switch (current) {
                case '+':
                    posicao++;
                    return new Token(TokenType.PLUS, "+", linhaAtual);
                case '-':
                    posicao++;
                    return new Token(TokenType.MINUS, "-", linhaAtual);
                case '*':
                    posicao++;
                    return new Token(TokenType.MULTIPLY, "*", linhaAtual);
                case '/':
                    if (posicao + 1 < codigo.length() && codigo.charAt(posicao + 1) == '/') {
                        skipLineComment();
                        return nextToken(); // Recomeça a análise após o comentário de linha
                    } else if (posicao + 1 < codigo.length() && codigo.charAt(posicao + 1) == '*') {
                        skipBlockComment();
                        return nextToken(); // Recomeça a análise após o comentário de bloco
                    } else {
                        posicao++;
                        return new Token(TokenType.DIVIDE, "/", linhaAtual);
                    }
                case '=':
                    posicao++;
                    return new Token(TokenType.EQUAL, "=", linhaAtual);
                case '(':
                    posicao++;
                    return new Token(TokenType.LPAREN, "(", linhaAtual);
                case ')':
                    posicao++;
                    return new Token(TokenType.RPAREN, ")", linhaAtual);
                case '{':
                    skipBlockComment();
                    return nextToken(); // Recomeça a análise após o comentário de bloco
                case ';':
                    posicao++;
                    return new Token(TokenType.SEMICOLON, ";", linhaAtual);
                case ':':
                    if (posicao + 1 < codigo.length() && codigo.charAt(posicao + 1) == '=') {
                        posicao += 2;
                        return new Token(TokenType.ASSIGN, ":=", linhaAtual);
                    } else {
                        posicao++;
                        return new Token(TokenType.COLON, ":", linhaAtual);
                    }
                case ',':
                    posicao++;
                    return new Token(TokenType.COMMA, ",", linhaAtual);
                case '<':
                    posicao++;
                    if (posicao < codigo.length() && codigo.charAt(posicao) == '=') {
                        posicao++;
                        return new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", linhaAtual);
                    } else {
                        return new Token(TokenType.LESS_THAN, "<", linhaAtual);
                    }
                case '>':
                    posicao++;
                    if (posicao < codigo.length() && codigo.charAt(posicao) == '=') {
                        posicao++;
                        return new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", linhaAtual);
                    } else {
                        return new Token(TokenType.GREATER_THAN, ">", linhaAtual);
                    }
                case '.':
                    posicao++;
                    return new Token(TokenType.PERIOD, ".", linhaAtual);
                case '\'':
                case '"':
                    return extractStringLiteral();
                default:
                    throw new LexicalException("Caractere inesperado: " + current + " na posição " + posicao);
            }
        }
    }

    private void skipWhitespace() {
        while (hasNext() && Character.isWhitespace(codigo.charAt(posicao))) {
            if (codigo.charAt(posicao) == '\n') {
                linhaAtual++;
            }
            posicao++;
        }
    }

    private void skipLineComment() {
        posicao++; // Pula o primeiro '/'
        posicao++; // Pula o segundo '/'
        while (hasNext() && codigo.charAt(posicao) != '\n') {
            posicao++;
        }
        linhaAtual++;
        posicao++; // Pula o '\n'
    }

    private void skipBlockComment() throws LexicalException {
        posicao++; // Pula o '{' ou o '*' após '/'
        if (codigo.charAt(posicao) == '*') {
            posicao++; // Pula o '*'
            while (hasNext()) {
                if (codigo.charAt(posicao) == '\n') {
                    linhaAtual++;
                }
                if (codigo.charAt(posicao) == '*' && posicao + 1 < codigo.length() && codigo.charAt(posicao + 1) == '/') {
                    posicao += 2; // Pula o '*' e o '/'
                    return;
                }
                posicao++;
            }
            throw new LexicalException("Comentário de bloco não fechado corretamente na linha " + linhaAtual);
        } else {
            while (hasNext() && codigo.charAt(posicao) != '}') {
                if (codigo.charAt(posicao) == '\n') {
                    linhaAtual++;
                }
                posicao++;
            }
            if (!hasNext()) {
                throw new LexicalException("Comentário de bloco não fechado corretamente na linha " + linhaAtual);
            }
            posicao++; // Pula o '}'
        }
    }

    private Token extractNumber() {
        int start = posicao;
        while (hasNext() && (Character.isDigit(codigo.charAt(posicao)) || codigo.charAt(posicao) == '.')) {
            if (codigo.charAt(posicao) == '.') {
                // Verifica se é um número real
                if (posicao + 1 < codigo.length() && Character.isDigit(codigo.charAt(posicao + 1))) {
                    posicao++;
                    while (hasNext() && Character.isDigit(codigo.charAt(posicao))) {
                        posicao++;
                    }
                } else {
                    break;
                }
            } else {
                posicao++;
            }
        }
        return new Token(TokenType.NUMBER, codigo.substring(start, posicao), linhaAtual);
    }

    private Token extractIdentifierOrKeyword() {
        int start = posicao;
        while (hasNext() && (Character.isLetterOrDigit(codigo.charAt(posicao)) || codigo.charAt(posicao) == '_')) {
            posicao++;
        }
        String lexeme = codigo.substring(start, posicao);
        TokenType type = KEYWORDS.getOrDefault(lexeme, TokenType.IDENTIFIER);
        return new Token(type, lexeme, linhaAtual);
    }
    
    private Token extractStringLiteral() throws LexicalException {
        char delimiter = codigo.charAt(posicao);
        posicao++; // Pula o delimitador inicial (aspas simples ou duplas)
        int start = posicao;
        while (hasNext() && codigo.charAt(posicao) != delimiter) {
            if (codigo.charAt(posicao) == '\n') {
                throw new LexicalException("String literal não fechada corretamente na linha " + linhaAtual);
            }
            posicao++;
        }
        if (!hasNext()) {
            throw new LexicalException("String literal não fechada corretamente na linha " + linhaAtual);
        }
        posicao++; // Pula o delimitador final (aspas simples ou duplas)
        return new Token(TokenType.STRING_LITERAL, codigo.substring(start, posicao - 1), linhaAtual);
    }

   
}
