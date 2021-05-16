package token;

import java.util.Map;

public interface Token {
    long getId();
    long getDestinationIndex();
    void setDestinationIndex(long index);

    boolean isDelivered();

    Map<Long, Integer> getMarks();
    void addMark(int ringIndex);
}
