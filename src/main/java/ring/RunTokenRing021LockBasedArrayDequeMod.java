package ring;

import token.Token;
import transporter.Transporter;
import transporter.needblocking.LockBasedQueueModTransporter;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RunTokenRing021LockBasedArrayDequeMod extends RunTokenRing {

    static final String name = "LockBasedArrayDequeModTransporter";

    public static void main(String[] args) throws InterruptedException {
        RunTokenRing runTokenRing = new RunTokenRing021LockBasedArrayDequeMod();
        runTokenRing.run(name);
    }

    List<Transporter> createTransporters(int N, int P) {
        return IntStream.range(0, N)
                .boxed()
                .map(j -> {
                    int destIndex = j;
                    List<Token> tokenList = Token.createTokensList(destIndex, P / N);
                    Queue<Token> queue = new ArrayDeque<>(tokenList);
                    return new LockBasedQueueModTransporter(queue);
                })
                .collect(Collectors.toList());
    }
}
