package node;

import token.Token;
import medium.Medium;

import java.util.function.Consumer;

public class NodeImpl implements Node {
    private final Medium next;
    private final Consumer<Token> tokenConsumer;
    private int ringIndex;
    private static int createCounter;

    public NodeImpl(Medium next, Consumer<Token> tokenConsumer) {
        this.next = next;
        this.tokenConsumer = tokenConsumer;
        ringIndex = createCounter;
        createCounter += 1;
    }

    public static void zeroCreateCounter() {
        createCounter = 0;
    }

    @Override
    public int getRingIndex() {
        return ringIndex;
    }

    @Override
    public void receive(Token token) throws InterruptedException {

        if (ringIndex == token.getDestinationIndex()) {
            // consume
            token.addMark(ringIndex);
            tokenConsumer.accept(token);
            // print
            // System.out.println("Node@ringIndex=" + ringIndex
            //        + " received token@destinationIndex=" + token.getDestinationIndex());
        } else {
            // transfer next
            token.addMark(ringIndex);
            next.push(token);
            // print
            // System.out.println("Node@ringIndex=" + ringIndex
            //        + " passed token@destinationIndex=" + token.getDestinationIndex());
        }
    }
}
