package token;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Token {
    long getId();
    int getDestinationIndex();

    boolean isSent();
    boolean isDelivered();

    long getSentTime();
    long getDeliveredTime();

    void markAsSent();
    void markAsDelivered();

    static List<Token> createTokensList(int destIndex, int tokensNum) {
        return IntStream.range(0, tokensNum)
                .boxed()
                .map(i -> new TokenImpl(destIndex))
                .collect(Collectors.toList());
    }
}
