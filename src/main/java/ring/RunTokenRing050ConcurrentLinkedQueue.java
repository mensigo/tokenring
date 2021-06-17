package ring;

import token.Token;
import transporter.Transporter;
import transporter.nonblocking.NonblockingQueueTransporter;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Deprecated
public class RunTokenRing050ConcurrentLinkedQueue extends RunTokenRing {

    static final String name = "ConcurrentLinkedQueueTransporter";

    public static void main(String[] args) throws InterruptedException {
        RunTokenRing runTokenRing = new RunTokenRing010LockBasedLinkedList();
        runTokenRing.run(name);
    }

    List<Transporter> createTransporters(int N, int P) {
        return IntStream.range(0, N)
                .boxed()
                .map(j -> {
                    int destIndex = j;
                    List<Token> tokenList = Token.createTokensList(destIndex, P / N);
                    ConcurrentLinkedQueue<Token> queue = new ConcurrentLinkedQueue<>(tokenList);
                    return new NonblockingQueueTransporter(queue);
                })
                .collect(Collectors.toList());
    }
}
