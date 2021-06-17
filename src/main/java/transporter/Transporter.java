package transporter;

import token.Token;

import java.util.List;

public interface Transporter {
    void push(Token token) throws InterruptedException;
    Token poll() throws InterruptedException;
    List<Token> getTokens();
}
