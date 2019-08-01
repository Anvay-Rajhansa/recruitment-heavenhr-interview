package org.heavenhr.recruitment.event;

import lombok.Data;
import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.springframework.context.ApplicationEvent;

@Data
public class ApplicationStatusEvent extends ApplicationEvent {

    private final Application application;
    private final ApplicationStatus oldStatus;
    private final ApplicationStatus newStatus;

    public ApplicationStatusEvent(Object source, Application application, ApplicationStatus oldStatus,
                                  ApplicationStatus newStatus) {
        super(source);
        this.application = application;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
