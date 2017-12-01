package se.fortnox.reactivewizard.jaxrs;

import se.fortnox.reactivewizard.ExceptionHandler;
import se.fortnox.reactivewizard.jaxrs.params.ParamResolverFactories;
import se.fortnox.reactivewizard.jaxrs.response.JaxRsResultFactoryFactory;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.junit.Test;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import se.fortnox.reactivewizard.utils.JaxRsTestUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.Executors;

import static se.fortnox.reactivewizard.utils.JaxRsTestUtil.testServer;
import static org.fest.assertions.Assertions.assertThat;

public class SynchronousResourcesTest {

    @Path("threadname")
    public class TestRes {
        @GET
        public String getThreadName() {
            return Thread.currentThread().getName();
        }
    }

    @Test
    public void shouldNotRunOnEventLoopThread() {
        assertThat(testServer(new TestRes()).get("/threadname"))
                .isNotEmpty()
                .doesNotContain("rxnetty-nio-eventloop");
    }

    @Test
    public void shouldRunOnConfiguredScheduler() {
        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor(runnable->new Thread(runnable,"customthread")));
        final RequestHandler handler = new JaxRsRequestHandler(
                new Object[] { new TestRes() },
                new JaxRsResourceFactory(new ParamResolverFactories(), new JaxRsResultFactoryFactory(), new BlockingResourceScheduler(scheduler)),
                new ExceptionHandler(),
                false);
        JaxRsTestUtil.TestServer testServer = new JaxRsTestUtil.TestServer(handler);
        assertThat(testServer.get("/threadname"))
                .isEqualTo("\"customthread\"");
    }
}