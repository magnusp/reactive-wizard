package se.fortnox.reactivewizard.helloworld;

import com.google.inject.Binder;
import com.google.inject.Inject;
import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;
import se.fortnox.reactivewizard.binding.AutoBindModule;

public class MyModule implements AutoBindModule {
    @Override
    public void configure(Binder binder) {
        System.setProperty(Configuration.JAEGER_SERVICE_NAME, "MyService");
        System.setProperty(Configuration.JAEGER_REPORTER_MAX_QUEUE_SIZE, "1");
        System.setProperty(Configuration.JAEGER_SAMPLER_TYPE, "const");
        System.setProperty(Configuration.JAEGER_SAMPLER_PARAM, "1");
        Tracer tracer = TracerResolver.resolveTracer();
        binder.bind(Tracer.class).toInstance(tracer);
    }
}
