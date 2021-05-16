package node;

import token.Token;

public interface Node {
    int getRingIndex();
    void receive(Token token) throws InterruptedException;
}
