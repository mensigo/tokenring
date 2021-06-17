package transporter.needblocking;

import token.Token;
import transporter.Transporter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedListTransporter implements Transporter {
    private final LinkedList<Token> list;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public LockBasedListTransporter(LinkedList<Token> list) {
        this.list = list;
    }

    @Override
    public List<Token> getTokens() {
        return new ArrayList<>(list);
    }

    @Override
    public void push(Token token) throws InterruptedException {
        try {
            lock.lock();
            boolean added;
            do {
                added = list.add(token);
            } while (!added);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Token poll() throws InterruptedException {
        lock.lock();
        try {
            while (list.isEmpty()) {
                notEmpty.await();
            }
            return list.poll();
        } finally {
            lock.unlock();
        }
    }
}
