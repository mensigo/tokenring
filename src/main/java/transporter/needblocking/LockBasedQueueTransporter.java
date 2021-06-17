package transporter.needblocking;

import token.Token;
import transporter.Transporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedQueueTransporter implements Transporter {
    private final Queue<Token> queue;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public LockBasedQueueTransporter(Queue<Token> queue) {
        this.queue = queue;
    }

    @Override
    public List<Token> getTokens() {
        return new ArrayList<>(queue);
    }

    @Override
    public void push(Token token) throws InterruptedException {
        try {
            lock.lock();
            boolean added;
            do {
                added = queue.add(token);
            } while (!added);
            notEmpty.signal();
            // idea: signal only if queue was empty
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Token poll() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }
}
