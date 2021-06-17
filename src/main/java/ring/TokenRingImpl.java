package ring;

import node.Node;
import node.NodeImpl;
import node.NodeRoutine;
import token.Token;
import token.TokenImpl;
import transporter.Transporter;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TokenRingImpl implements TokenRing {
    private final List<Thread> nodeThreads;

    public TokenRingImpl(List<Thread> nodeThreads) {
        this.nodeThreads = nodeThreads;
    }

    public static TokenRingImpl factory(List<Transporter> transporters, int N, int P, boolean verbose) {
        Consumer<Token> tokenConsumer = token -> {};
        List<Node> nodes = createNodes(transporters, tokenConsumer, N, P, verbose);
        if (verbose) {
            System.out.println("---");
        }

        List<Thread> nodeRoutineThreads = createNodeRoutineThreads(nodes, transporters, N, verbose);
        if (verbose) {
            System.out.println("---");
        }

        return new TokenRingImpl(nodeRoutineThreads);
    }

    @Override
    public void start() {
        nodeThreads.forEach(Thread::start);
    }

    @Override
    public void stop() {
        nodeThreads.stream()
                .peek(Thread::interrupt)
                .forEach(thread -> {
                    try {
                        thread.join();
                        // System.out.println("Joined " + thread.getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static int getNextRingIndex(int ringIndex, int ringSize) {
        return (ringIndex +1) % ringSize;
    }

    public static int getPrevRingIndex(int ringIndex, int ringSize) {
        return (ringIndex - 1 + ringSize) % ringSize;
    }

    private static List<Node> createNodes(List<Transporter> transporters,
                                          Consumer<Token> tokenConsumer,
                                          int N, int P, boolean verbose) {
        return IntStream.range(0, N)
                .boxed()
                .map(i -> {
                    int destIndex = i;
                    List<Token> initTokensList = IntStream.range(0, P / N)
                            .boxed()
                            .map(j -> new TokenImpl(destIndex))
                            .collect(Collectors.toList());
                    Transporter pushTransporter = transporters.get(destIndex);
                    return new NodeImpl(pushTransporter, tokenConsumer, N);
                })
                .peek(n -> { if (verbose) System.out.println("Node@idx=" + n.getRingIndex() + " has been created."); })
                .collect(Collectors.toList());
    }

    private static List<Thread> createNodeRoutineThreads(List<Node> nodes,
                                                         List<Transporter> transporters,
                                                         int N, boolean verbose) {
        return nodes.stream()
                .map(n -> {
                    int prevId = getPrevRingIndex(n.getRingIndex(), N);
                    Transporter pollTransporter = transporters.get(prevId);
                    return new NodeRoutine(n, pollTransporter);
                })
                .peek(nr -> {
                    if (verbose) System.out.println("NodeRoutine@idx=" + nr.getNode().getRingIndex() + " has been created.");
                })
                .map(Thread::new)
                .collect(Collectors.toList());
    }
}
