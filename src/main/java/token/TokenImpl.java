package token;

public class TokenImpl implements Token {
    private int destinationIndex;
    private final long tokenId;

    private long sentTimestamp;
    private long deliveredTimestamp;

    private boolean isSent;
    private boolean isDelivered;

    private static long tokenCounter;

    public TokenImpl(int destinationIndex) {
        this.destinationIndex = destinationIndex;
        tokenId = tokenCounter;
        tokenCounter++;
    }

    @Override
    public long getId() {
        return tokenId;
    }

    @Override
    public int getDestinationIndex() {
        return destinationIndex;
    }

    @Override
    public boolean isSent() {
        return isSent;
    }

    @Override
    public boolean isDelivered() {
        return isDelivered;
    }

    @Override
    public long getSentTime() {
        return sentTimestamp;
    }

    @Override
    public long getDeliveredTime() {
        return deliveredTimestamp;
    }

    @Override
    public void markAsSent() {
        sentTimestamp = System.nanoTime();
        isSent = true;
    }

    @Override
    public void markAsDelivered() {
        deliveredTimestamp = System.nanoTime();
        isDelivered = true;
    }
}
