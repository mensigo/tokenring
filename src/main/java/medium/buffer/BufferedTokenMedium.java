package medium.buffer;

import token.Token;
import medium.Medium;

import java.util.List;

public interface BufferedTokenMedium extends Medium {
    List<Token> getTokens();
}
