package ring;

import token.Token;
import transporter.Transporter;
import transporter.needblocking.LockBasedQueueTransporter;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RunTokenRing020LockBasedArrayDeque extends RunTokenRing {

    static final String name = "LockBasedArrayDequeTransporter";

    public static void main(String[] args) throws InterruptedException {
        RunTokenRing runTokenRing = new RunTokenRing020LockBasedArrayDeque();
        runTokenRing.run(name);
    }

    List<Transporter> createTransporters(int N, int P) {
        return IntStream.range(0, N)
                .boxed()
                .map(j -> {
                    int destIndex = j;
                    List<Token> tokenList = Token.createTokensList(destIndex, P / N);
                    Queue<Token> queue = new ArrayDeque<>(tokenList);
                    return new LockBasedQueueTransporter(queue);
                })
                .collect(Collectors.toList());
    }
}
