package transporter.nonblocking;

import token.Token;
import transporter.Transporter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonblockingQueueTransporter implements Transporter {
    private final ConcurrentLinkedQueue<Token> queue;

    public NonblockingQueueTransporter(ConcurrentLinkedQueue<Token> queue) {
        this.queue = queue;
    }

    @Override
    public List<Token> getTokens() {
        return new ArrayList<>(queue);
    }

    @Override
    public void push(Token token) throws InterruptedException {
        queue.offer(token);
    }

    @Override
    public Token poll() throws InterruptedException {
        return queue.poll();
    }

}
