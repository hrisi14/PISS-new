package bg.sofia.uni.fmi.bookmarksmanager.exceptions;

public class NoSuchBookmarkException extends RuntimeException {
    public NoSuchBookmarkException(String message) {
        super(message);
    }
}
