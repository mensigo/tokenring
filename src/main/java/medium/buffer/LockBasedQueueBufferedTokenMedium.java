package medium.buffer;

import token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedQueueBufferedTokenMedium implements BufferedTokenMedium {
    private final Queue<Token> queue;

    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public LockBasedQueueBufferedTokenMedium(Queue<Token> queue) {
        this.queue = queue;
    }

    @Override
    public List<Token> getTokens() {
        return new ArrayList<>(queue);
    }

    @Override
    public void push(Token token) throws InterruptedException {
        lock.lock();
        try {
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
