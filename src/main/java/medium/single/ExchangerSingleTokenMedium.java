package medium.single;

import token.Token;

import java.util.concurrent.Exchanger;

public class ExchangerSingleTokenMedium implements SingleTokenMedium {
    private final Exchanger<Token> exchanger;
    private Token token;

    public ExchangerSingleTokenMedium(Exchanger<Token> exchanger, Token token) {
        this.exchanger = exchanger;
        this.token = token;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public void push(Token token) throws InterruptedException {
        System.out.println("push..");
        exchanger.exchange(token);
    }

    @Override
    public Token poll() throws InterruptedException {
        System.out.println("pop..");
        token = null;
        return exchanger.exchange(null);
    }
}
