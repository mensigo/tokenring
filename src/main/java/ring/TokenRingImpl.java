package ring;

import node.Node;
import node.NodeRoutine;
import node.NodeImpl;
import token.Token;
import medium.Medium;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TokenRingImpl implements TokenRing {
    private final int size;
    private final List<Thread> nodeThreads;

    public TokenRingImpl(int size, List<Thread> nodeThreads) {
        this.size = size;
        this.nodeThreads = nodeThreads;
    }

    public static TokenRingImpl factory(List<Medium> mediums) {
        int ringSize = mediums.size();
        Consumer<Token> tokenConsumer = token -> {};
        List<Node> nodes = mediums.stream()
                .map(m -> new NodeImpl(m, tokenConsumer))
                // .peek(n -> System.out.println("Node@idx=" + n.getRingIndex() + " has been created."))
                .collect(Collectors.toList());
        // System.out.println("---");
        List<Thread> nodeRoutineThreads = nodes.stream()
                .map(n -> {
                    int prevId = getPrevRingIndex(n.getRingIndex(), ringSize);
                    return new NodeRoutine(n, mediums.get(prevId));
                })
                // .peek(nr -> System.out.println(
                //        "Node@idx=" + nr.getNode().getRingIndex()
                //        + " is listening to node@idx=" + getPrevRingIndex(nr.getNode().getRingIndex(), ringSize) + "."))
                .map(Thread::new)
                .collect(Collectors.toList());
        // System.out.println("---");
        return new TokenRingImpl(ringSize, nodeRoutineThreads);
    }

    @Override
    public int size() {
        return size;
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

    private static int getPrevRingIndex(int i, int count) {
        return (i - 1 + count) % count;
    }
}
