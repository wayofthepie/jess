package jess;

import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import jess.service.NatsService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
@Extension
public class GlobalBuildListener extends RunListener<Run> {

    private NatsService natsService;

    public GlobalBuildListener() throws IOException, TimeoutException {
        super(Run.class);

    }
//
//    @Override
//    public void onStarted(Run run, TaskListener listener) {
//        natsService.publish("test", run.getParent().getName().getBytes());
//        natsService.publish("test", run.getFullDisplayName().getBytes());
//        natsService.publish("test", run.getBuildStatusSummary().message.getBytes());
//    }
//
//    @Override
//    public void onCompleted(Run run, @Nonnull TaskListener listener) {
//        StringWriter writer = new StringWriter();
//        try {
//            IOUtils.copy(run.getLogInputStream(), writer);
//            String theString = writer.toString();
//            natsService.publish("complete", theString.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
