package node;

import token.Token;
import transporter.Transporter;

public class NodeRoutine implements Runnable {
    private final Node node;
    private final Transporter pollTransporter;

    public NodeRoutine(Node node, Transporter pollTransporter) {
        this.node = node;
        this.pollTransporter = pollTransporter;
    }

    public Node getNode() {
        return node;
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
            Token token = pollTransporter.poll();
            node.receive(token);
        }
    }
}
