package ring;

import token.Token;
import transporter.Transporter;
import transporter.blocking.BlockingQueueTransporter;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RunTokenRing030ArrayBlockingQuque extends RunTokenRing {

    static final String name = "ArrayBlockingQueueTransporter";

    public static void main(String[] args) throws InterruptedException {
        RunTokenRing runTokenRing = new RunTokenRing030ArrayBlockingQuque();
        runTokenRing.run(name);
    }

    List<Transporter> createTransporters(int N, int P) {
        return IntStream.range(0, N)
                .boxed()
                .map(j -> {
                    int destIndex = j;
                    List<Token> tokenList = Token.createTokensList(destIndex, P / N);
                    BlockingQueue<Token> queue = new ArrayBlockingQueue<>(P);
                    queue.addAll(tokenList);
                    return new BlockingQueueTransporter(queue);
                })
                .collect(Collectors.toList());
    }
}
