package se.fortnox.reactivewizard.helloworld;

import com.fasterxml.jackson.databind.JsonNode;
import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.TimeUnit;


@Path("/helloworld")
public class HelloWorldResource {

    private final GoogleResource googleResource;
    private final Tracer tracer;

    @Inject
    public HelloWorldResource(Tracer tracer, GoogleResource googleResource) {
        this.tracer = tracer;
        this.googleResource = googleResource;
    }

    @GET
    public Observable<String> greeting() {
        return Observable.using(
            () -> {
                return tracer.buildSpan("large-operation-" + Thread.currentThread().getName()).startActive(false);
            },
            (scope) -> {
                Span span = scope.span().setOperationName("mixing");
                return Observable.zip(
                    googleResource.getIndex().subscribeOn(Schedulers.newThread()).doOnSubscribe(() -> {
                        tracer.scopeManager().activate(span, false);
                        try (Scope inner = tracer.buildSpan("google-op").startActive(true)) {
                            inner.span().log("got google, thread:" + Thread.currentThread().getName());
                        }
                    }),
                    Observable.just("value").timer(1, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).doOnTerminate(() -> {
                       tracer.scopeManager().activate(span, false);
                       try (Scope inner = tracer.buildSpan("delayed-op").startActive(true)) {
                           inner.span().log("subscribe delayed, thread:" + Thread.currentThread().getName());
                       }
                    }),
                    (jsonNode, s) -> jsonNode.toString()
                ).doOnTerminate(span::finish);
            },
            (scope -> tracer.scopeManager().activate(scope.span(), true).close())
        );
    }

    @Path("/again")
    @GET
    public Observable<String> greetAgain() {
        return Observable.using(
            () -> tracer.buildSpan("large-operation-again").start(),
            span -> {
                Scope scope = tracer.scopeManager().activate(span, true);
                Span mixingSpan = tracer.buildSpan("mixer").asChildOf(span.context()).start();
                return Observable.zip(
                    googleResource.getIndex().subscribeOn(Schedulers.newThread()).doOnSubscribe(() -> {
                        tracer.buildSpan("google-op").asChildOf(mixingSpan.context()).start().log("got google, thread:" + Thread.currentThread().getName()).finish();
                    }),
                    Observable.timer(1, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).doOnTerminate(() -> {
                        tracer.buildSpan("delayed-op").asChildOf(mixingSpan.context()).start().log("subscribe delayed, thread:" + Thread.currentThread().getName()).finish();
                    }),
                    (jsonNode, s) -> jsonNode.toString()
                ).doOnTerminate(() -> scope.close());
            },
            Span::finish
        );
    }
}
