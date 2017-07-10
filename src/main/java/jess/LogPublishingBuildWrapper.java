package jess;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.console.ConsoleLogFilter;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.YesNoMaybe;
import jenkins.tasks.SimpleBuildWrapper;
import jess.service.NatsService;
import jess.service.NatsServiceImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class LogPublishingBuildWrapper extends SimpleBuildWrapper {
    private static NatsService natsService;

    static {
        try {
            natsService = new NatsServiceImpl();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @DataBoundConstructor
    public LogPublishingBuildWrapper() {
        natsService.publish("Test", "Service started");
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher,
                      TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {

    }

    @Override
    public ConsoleLogFilter createLoggerDecorator(@Nonnull Run<?, ?> build) {
        return new ConsoleLogFilterImpl(build);
    }

    private static class ConsoleLogFilterImpl extends ConsoleLogFilter implements Serializable {
        private static final long serialVersionUID = 1;

        ConsoleLogFilterImpl(Run<?, ?> build) {

        }

        @Override
        public OutputStream decorateLogger(Run build, OutputStream logger) throws IOException, InterruptedException {

            return new NatsOutputStream(natsService, logger);
        }

    }

    @Extension(dynamicLoadable = YesNoMaybe.YES)
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "NatsBuildWrapper";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }
    }
}

