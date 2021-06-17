package ring;

import token.Token;
import transporter.Transporter;
import transporter.needblocking.LockBasedQueueTransporter;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Deprecated
public class RunTokenRing060LockBasedPriorityQueue extends RunTokenRing {

    static final String name = "LockBasedPriorityQueueTransporter";

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
                    Queue<Token> queue = new PriorityQueue<>(tokenList);
                    return new LockBasedQueueTransporter(queue);
                })
                .collect(Collectors.toList());
    }
}
