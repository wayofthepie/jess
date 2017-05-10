package jess;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Listens to every build.
 */
@Extension
public class GlobalBuildListener extends RunListener<Run> {
    public GlobalBuildListener() {
        super(Run.class);
    }
}
