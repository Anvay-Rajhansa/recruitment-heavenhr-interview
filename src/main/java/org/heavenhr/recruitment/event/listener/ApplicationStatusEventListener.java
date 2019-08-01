package org.heavenhr.recruitment.event.listener;

import org.heavenhr.recruitment.event.ApplicationStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStatusEventListener implements ApplicationListener<ApplicationStatusEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStatusEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationStatusEvent applicationStatusEvent) {
        LOG.info("Status change event for application with id - {} : oldStatus - {} newStatus - {}",
                applicationStatusEvent.getApplication().getId(),
                applicationStatusEvent.getOldStatus(),
                applicationStatusEvent.getNewStatus());
    }
}
