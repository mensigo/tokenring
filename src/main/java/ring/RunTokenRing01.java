package ring;

import node.NodeImpl;
import token.Token;
import token.TokenImpl;
import medium.Medium;
import medium.single.LockBasedSingleTokenMedium;
import medium.single.SingleTokenMedium;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// not using this option
@Deprecated
public class RunTokenRing01 {

    // LockBasedSingleTokenMedium

    public static void main(String[] args) throws InterruptedException {
        int ringSize = 10;
        int normalRuns = 10;
        int warmUpRuns = 10;
        String filename = "RunTokenRing01Results" +
                "-rs-" + ringSize +
                "-nr-" + normalRuns +
                "-wr-" + warmUpRuns + ".csv";
        List<Medium> mediums;
        List<Token> tokens;
        List<String> resultStringList = new ArrayList<>();
        resultStringList.add("TokenDestId,tmp,nodeId,run");

        for (int i = 0; i < warmUpRuns + normalRuns; i++) {
            System.out.println("--- START RUN #" + (i + 1) + "---");
            NodeImpl.zeroCreateCounter();
            mediums = IntStream.range(0, ringSize)
                    .boxed()
                    .map(j -> {
                        int destinationIndex = j;
                        Token token = new TokenImpl(destinationIndex, ringSize);
                        return new LockBasedSingleTokenMedium(token);
                    })
                    .collect(Collectors.toList());
            tokens = mediums.stream()
                    .map(t -> ((SingleTokenMedium) t).getToken())
                    .collect(Collectors.toList());
            // create & start
            TokenRingImpl tokenRing = TokenRingImpl.factory(mediums);
            tokenRing.start();
            // wait until all tokens are delivered
            int tmp;
            do {
                tmp = tokensDeliveredNum(tokens);
                System.out.println(tmp);
                Thread.sleep(4000);
            } while (tmp < tokens.size());
            System.out.println("--- PROCESSED ALL TOKENS ---");
            // shut down the ring
            tokenRing.stop();
            System.out.println("--- STOPPED ALL THREADS ---");
            // collect results of the current run
            if (i >= warmUpRuns) {
                int tmp_i = i;
                resultStringList.addAll(tokens.stream()
                        .flatMap(t -> tokenToHistory(t, tmp_i).stream())
                        .collect(Collectors.toList()));
            }
            System.out.println();
        }
        writeToFile(filename, resultStringList);
    }

    private static int tokensDeliveredNum(List<Token> tokens) {
        int cnt = 0;
        for (Token token: tokens) {
            if (token.isDelivered()) {
                cnt++;
            }
        }
        return cnt;
    }

    private static List<String> tokenToHistory(Token token, int run) {
        Map<Long, Integer> marks = token.getMarks();
        return marks.entrySet().stream()
                .map(e -> token.getDestinationIndex() + "," + e.getKey()
                        + "," + e.getValue() + "," + run)
                .collect(Collectors.toList());
    }

    private static void writeToFile(String filename, List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringList.forEach(s -> stringBuilder.append(s).append("\n"));
        try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
            writer.println(stringBuilder.toString());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
