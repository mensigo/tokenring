package node;

import ring.TokenRingImpl;
import token.Token;
import transporter.Transporter;

import java.util.function.Consumer;

@Deprecated
public class NodeImplBackup implements Node {
    private final Transporter next;
    private final Consumer<Token> tokenConsumer;
    private final int ringIndex;
    private final int ringSize;
    private static int createCounter;

    public NodeImplBackup(Transporter next, Consumer<Token> tokenConsumer, int ringSize) {
        this.next = next;
        this.tokenConsumer = tokenConsumer;
        ringIndex = createCounter;
        createCounter += 1;
        this.ringSize = ringSize;
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
            // consume
            token.markAsDelivered();
            tokenConsumer.accept(token);
            // print
            // System.out.println("Node@ringIndex=" + ringIndex
            //        + " received token@destinationIndex=" + token.getDestinationIndex());
        } else if (ringIndex == TokenRingImpl.getNextRingIndex(destIndex, ringSize) &&
                !token.isSent()) {
            // set sent time & transfer next
            token.markAsSent();
            next.push(token);
        } else {
            // transfer next
            next.push(token);
            // print
            // System.out.println("Node@ringIndex=" + ringIndex
            //        + " passed token@destinationIndex=" + token.getDestinationIndex());
        }
    }
}
