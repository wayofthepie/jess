package jess.configuration;

import com.google.inject.AbstractModule;
import jess.service.NatsService;
import jess.service.NatsServiceImpl;

/**
 *
 */
public class JessConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(NatsService.class).to(NatsServiceImpl.class).in(com.google.inject.Singleton.class);
    }
}
