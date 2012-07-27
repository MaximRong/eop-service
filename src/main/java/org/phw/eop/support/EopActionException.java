package org.phw.eop.support;

public class EopActionException extends Exception {
    private static final long serialVersionUID = -3091136184005585116L;
    private String messageCode;

    /**
     * default ctor.
     */
    public EopActionException() {
        super();
    }

    /**
     * ctor.
     * @param message 异常消息
     */
    public EopActionException(String message) {
        super(message);
    }

    /**
     * ctor.
     * @param message 异常消息
     * @param cause 
     */
    public EopActionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * ctor.
     * @param cause 
     */
    public EopActionException(Throwable cause) {
        super(cause);
    }

    /**
     * ctor.
     * @param messageCode 异常代码
     * @param message 异常消息
     */
    public EopActionException(String messageCode, String message) {
        super(message);
        setMessageCode(messageCode);

    }

    /**
     * ctor.
     * @param messageCode 异常代码
     * @param message 异常消息
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public EopActionException(String messageCode, String message, Throwable cause) {
        super(message, cause);
        setMessageCode(messageCode);
    }

    /**
     * 取得异常编码。
     * @return 异常编码
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * 设置异常编码。
     * @param messageCode 异常编码
     */
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

}
