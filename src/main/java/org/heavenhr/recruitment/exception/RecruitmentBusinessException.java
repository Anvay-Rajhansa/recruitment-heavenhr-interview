package org.heavenhr.recruitment.exception;

public class RecruitmentBusinessException extends RuntimeException {

    public RecruitmentBusinessException(String message) {
        super(message);
    }

    public RecruitmentBusinessException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RecruitmentBusinessException(Throwable throwable) {
        super((throwable));
    }
}
