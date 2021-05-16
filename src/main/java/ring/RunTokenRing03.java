package ring;

import node.NodeImpl;
import token.Token;
import token.TokenImpl;
import medium.Medium;
import medium.buffer.BufferedTokenMedium;
import medium.buffer.LockBasedQueueBufferedTokenMedium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RunTokenRing03 {

    static final String name = "LockBasedQueueBufferedTokenMedium";

    public static void main(String[] args) throws InterruptedException {
        int normalRuns = 25;
        int warmUpRuns = 75;

        String resultDir = "data/" + name;
        new File(resultDir).mkdirs();

        int[] rsRange = new int[] {6, 36, 216};
        int[] ibtnRange = new int[] {25, 75, 225};

        for (int rs: rsRange) {
            for (int ibtn: ibtnRange) {
                System.out.println("rs=" + rs + " ibtn=" + ibtn);

                for (int i = 0; i < warmUpRuns + normalRuns; i++) {
                    // System.out.println("--- START RUN #" + (i + 1) + "---");
                    NodeImpl.zeroCreateCounter();
                    List<Medium> mediums = IntStream.range(0, rs)
                            .boxed()
                            .map(j -> {
                                int destinationIndex = j;
                                Queue<Token> queue = new ArrayDeque<>();
                                for (int k = 0; k < ibtn; k++) {
                                    Token token = new TokenImpl(destinationIndex, rs);
                                    queue.add(token);
                                }
                                return new LockBasedQueueBufferedTokenMedium(queue);
                            })
                            .collect(Collectors.toList());
                    List<Token> tokens = mediums.stream()
                            .flatMap(t -> ((BufferedTokenMedium) t).getTokens().stream())
                            .collect(Collectors.toList());

                    // create & start
                    TokenRingImpl tokenRing = TokenRingImpl.factory(mediums);
                    tokenRing.start();

                    // wait until all tokens are delivered
                    int tmp;
                    do {
                        Thread.sleep(50);
                        tmp = tokensDeliveredNum(tokens);
                        // System.out.println(tmp);
                    } while (tmp < tokens.size());
                    // System.out.println("--- PROCESSED ALL TOKENS ---");

                    // shut down the ring
                    tokenRing.stop();
                    // System.out.println("--- STOPPED ALL THREADS ---");

                    // collect results of the current run
                    int tmp_i = i;
                    if (tmp_i >= warmUpRuns) {
                        List<String> resultStringList = new ArrayList<>();
                        resultStringList.add("tokenId,tokenDestIndex,tmp,nodeId,run");
                        resultStringList.addAll(tokens.stream()
                                .flatMap(t -> tokenToHistory(t, tmp_i).stream())
                                .collect(Collectors.toList()));
                        String filename = resultDir + "/exp" +
                                "-rs-" + rs +
                                "-nr-" + normalRuns +
                                "-wr-" + warmUpRuns +
                                "-ibtn-" + ibtn;
                        writeToFile(filename + "-run" + tmp_i + ".csv", resultStringList);
                    }
                    // System.out.println();
                }
            }
        }
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
                .map(e -> token.getId() + ","
                        + token.getDestinationIndex() + ","
                        + e.getKey() + ","
                        + e.getValue() + ","
                        + run)
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
