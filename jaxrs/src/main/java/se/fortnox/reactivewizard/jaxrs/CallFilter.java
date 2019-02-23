package se.fortnox.reactivewizard.jaxrs;

import rx.Observable;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;

public interface CallFilter extends BiFunction<JaxRsRequest, Annotation[], Observable<Boolean>> {
}
