package medium;

import token.Token;

public interface Medium {
    void push(Token token) throws InterruptedException;
    Token poll() throws InterruptedException;
}
