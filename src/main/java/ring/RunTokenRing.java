package ring;

import node.NodeImpl;
import token.Token;
import transporter.Transporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract class RunTokenRing {

    abstract List<Transporter> createTransporters(int N, int P);

    static int tokensDeliveredNum(Set<Token> tokens) {
        int cnt = 0;
        for (Token token: tokens) {
            if (token.isDelivered()) {
                cnt++;
            }
        }
        return cnt;
    }

    static String tokenToHistoryString(Token token, int run) {
        return token.getId() + "," +
                token.getDestinationIndex() + "," +
                token.getSentTime() + "," +
                token.getDeliveredTime() + "," +
                run;
    }

    static void writeToFile(String filename, List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringList.forEach(s -> stringBuilder.append(s).append("\n"));
        try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
            writer.println(stringBuilder.toString());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void run(String name) throws InterruptedException {
        int normalRuns = 40;
        int warmUpRuns = 10;

        String resultDir = "data/" + name;
        new File(resultDir).mkdirs();

        boolean verbose = false;

        // N - number of nodes
        // P - number of packages (tokens)

        int[] nRange = new int[] {2, 3, 4, 5, 6, 7, 8};
        // let P be divisible by N (to distribute tokens uniformly)
//        int[] pRange = new int[] {840, 1680, 2520};
        int[] pRange = new int[] {840};

        for (int N: nRange) {
            for (int P: pRange) {
                System.out.println("N=" + N + " P=" + P);

                for (int i = 0; i < warmUpRuns + normalRuns; i++) {
                    if (verbose) {
                        System.out.println("--- START RUN #" + (i + 1) + "---");
                    }

                    // create transporters & tokens
                    NodeImpl.zeroCreateCounter();
                    List<Transporter> transporters = this.createTransporters(N, P);
                    Set<Token> tokens = transporters.stream()
                            .flatMap(t -> t.getTokens().stream())
                            .collect(Collectors.toSet());

                    // create nodes, TokenRing, then start
                    TokenRing tokenRing = TokenRingImpl.factory(transporters, N, P, verbose);
                    tokenRing.start();

                    // wait until all tokens are delivered
                    int tmp;
                    do {
                        Thread.sleep(100);
                        tmp = RunTokenRing.tokensDeliveredNum(tokens);
                        if (verbose) {
                            System.out.println(tmp + " / " + tokens.size());
                        }
                    } while (tmp < tokens.size());
                    if (verbose) {
                        System.out.println("--- PROCESSED ALL TOKENS ---");
                    }

                    // shut down the ring
                    tokenRing.stop();
                    if (verbose) {
                        System.out.println("--- STOPPED ALL THREADS ---");
                    }

                    // collect results of the current run
                    int tmp_i = i;
                    if (tmp_i >= warmUpRuns) {
                        List<String> resultStringList = new ArrayList<>();
                        resultStringList.add("tokenId,tokenDestIndex,sentTsmp,delTsmp,run");
                        resultStringList.addAll(tokens.stream()
                                .map(t -> RunTokenRing.tokenToHistoryString(t, tmp_i))
                                .collect(Collectors.toList()));
                        String currentRunDir = resultDir + "/exp" +
                                "-N-" + N +
                                "-P-" + P +
                                "-nRuns-" + normalRuns +
                                "-wRuns-" + warmUpRuns;
                        new File(currentRunDir).mkdirs();
                        RunTokenRing.writeToFile(currentRunDir + "/run-" + tmp_i + ".csv", resultStringList);
                    }
                    if (verbose) {
                        System.out.println();
                    }
                }
            }
        }
    }
}
