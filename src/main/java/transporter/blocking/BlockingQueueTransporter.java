package transporter.blocking;

import token.Token;
import transporter.Transporter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTransporter implements Transporter {
    private final BlockingQueue<Token> blockingQueue;

    public BlockingQueueTransporter(BlockingQueue<Token> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public List<Token> getTokens() {
        return new ArrayList<>(blockingQueue);
    }

    @Override
    public void push(Token token) throws InterruptedException {
        blockingQueue.put(token);
    }

    @Override
    public Token poll() throws InterruptedException {
        return blockingQueue.take();
    }
}
