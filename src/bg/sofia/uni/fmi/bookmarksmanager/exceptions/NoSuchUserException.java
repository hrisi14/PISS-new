package bg.sofia.uni.fmi.bookmarksmanager.exceptions;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String message) {
        super(message);
    }
}
