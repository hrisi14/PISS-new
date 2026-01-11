package bg.sofia.uni.fmi.bookmarksmanager.exceptions;

public class BookmarkAlreadyExistsException extends RuntimeException {
    public BookmarkAlreadyExistsException(String message) {
        super(message);
    }
}
