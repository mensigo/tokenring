package node;

import token.Token;
import transporter.Transporter;

import java.util.function.Consumer;

public class NodeImpl implements Node {
    private final Transporter next;
    private final Consumer<Token> tokenConsumer;
    private final int ringIndex;
    private static int createCounter;

    public NodeImpl(Transporter next, Consumer<Token> tokenConsumer) {
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

        int destIndex = token.getDestinationIndex();
        if (ringIndex == destIndex) {
            if (!token.isSent()) {
                // the first deliver - mark as sent & transfer next
                token.markAsSent();
                next.push(token);
            } else {
                // the second deliver - consume
                token.markAsDelivered();
                tokenConsumer.accept(token);
                // print
                // System.out.println("Node@ringIndex=" + ringIndex
                //        + " consumed token@destinationIndex=" + token.getDestinationIndex());
            }
        } else {
            // transfer next
            next.push(token);
            // print
            // System.out.println("Node@ringIndex=" + ringIndex
            //        + " passed token@destinationIndex=" + token.getDestinationIndex());
        }
    }
}
