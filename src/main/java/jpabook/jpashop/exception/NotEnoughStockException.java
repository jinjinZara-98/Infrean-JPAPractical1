package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException() {
    }

    //생성자, 메시지만 들어온 경우
    public NotEnoughStockException(String message) {
        super(message);
    }

    //생성자, 메시지와 이유 함께 들어온 경우
    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
