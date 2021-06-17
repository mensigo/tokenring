package ring;

import token.Token;
import transporter.Transporter;
import transporter.needblocking.LockBasedListModTransporter;
import transporter.needblocking.LockBasedListTransporter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RunTokenRing011LockBasedLinkedListMod extends RunTokenRing {

    static final String name = "LockBasedLinkedListModTransporter";

    public static void main(String[] args) throws InterruptedException {
        RunTokenRing runTokenRing = new RunTokenRing011LockBasedLinkedListMod();
        runTokenRing.run(name);
    }

    List<Transporter> createTransporters(int N, int P) {
        return IntStream.range(0, N)
                .boxed()
                .map(j -> {
                    int destIndex = j;
                    List<Token> tokenList = Token.createTokensList(destIndex, P / N);
                    LinkedList<Token> list = new LinkedList<>(tokenList);
                    return new LockBasedListModTransporter(list);
                })
                .collect(Collectors.toList());
    }
}
