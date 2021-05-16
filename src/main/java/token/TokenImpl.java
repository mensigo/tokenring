package token;

import java.util.LinkedHashMap;
import java.util.Map;

public class TokenImpl implements Token {
    private long destinationIndex;
    private final LinkedHashMap<Long, Integer> marksMap;
    private final int ringSize;

    private final long tokenId;
    private static long tokenCounter;

    public TokenImpl(long destinationIndex, int ringSize) {
        this.destinationIndex = destinationIndex;
        marksMap = new LinkedHashMap<>();
        this.ringSize = ringSize;
        tokenId = tokenCounter;
        tokenCounter++;
    }

    @Override
    public boolean isDelivered() {
        // print
        // System.out.println(marksMap.size() + " " +  ringSize + " ");
        return marksMap.size() == ringSize;
    }

    @Override
    public long getId() {
        return tokenId;
    }

    @Override
    public long getDestinationIndex() {
        return destinationIndex;
    }

    @Override
    public void setDestinationIndex(long index) {
        destinationIndex = index;
    }

    @Override
    public Map<Long, Integer> getMarks() {
        return marksMap;
    }

    @Override
    public void addMark(int ringIndex) {
        long mark = System.nanoTime();
        marksMap.put(mark, ringIndex);
    }
}
