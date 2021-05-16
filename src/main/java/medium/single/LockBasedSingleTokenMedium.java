package medium.single;

import token.Token;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedSingleTokenMedium implements SingleTokenMedium {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Token token;

    public LockBasedSingleTokenMedium(Token token) {
        this.token = token;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public void push(Token token) throws InterruptedException {
        lock.lock();
        try {
            while (this.token != null) {
                condition.await();
            }
            this.token = token;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Token poll() throws InterruptedException {
        lock.lock();
        try {
            while (this.token == null) {
                condition.await();
            }
            Token taken = token;
            token = null;
            condition.signal();
            return taken;
        } finally {
            lock.unlock();
        }
    }
}
