package tr.com.common.exceptions;

import java.util.Set;

public class ServiceNotAvailableException extends BaseException{
    private static final int code = 1010;
    private static final long serialVersionUID = 2281443542174873165L;

    public ServiceNotAvailableException(String message) {
        super(message, code);
    }

    public ServiceNotAvailableException(String message, Set<String> errList) {
        super(message, errList, code);
    }
}
