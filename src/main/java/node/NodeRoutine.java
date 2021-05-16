package node;

import token.Token;
import medium.Medium;

public class NodeRoutine implements Runnable {
    private final Node node;
    private final Medium medium;

    public NodeRoutine(Node node, Medium medium) {
        this.node = node;
        this.medium = medium;
    }

    @Override
    public void run() {
        try {
            job();
        } catch (InterruptedException e) {
            // print
            // System.err.format("Node %d has been interrupted!\n", node.getId());
        }
    }

    private void job() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            Token token = medium.poll();
            node.receive(token);
        }
    }
}
