package bg.sofia.uni.fmi.bookmarksmanager.exceptions;

public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
