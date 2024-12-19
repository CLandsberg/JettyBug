import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.VirtualThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    public static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        var virtualThreadPool = new VirtualThreadPool();
        var server = new Server(virtualThreadPool);
        addConnector(server, 8080);
        addConnector(server, 8081);

        var handler = new ServletContextHandler("/");
        server.setHandler(handler);
        handler.addServlet(HelloServlet.class, "/hw");

        try {
            server.start();
            LOG.info("Server started on port 8080");

            var l = new AtomicLong(0);
            Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().name("platform").factory()).scheduleAtFixedRate(() -> {
                var i = l.getAndIncrement();
                LOG.info("Tick {}", i);
                Thread.ofVirtual().start(() -> LOG.info("Tack {}", i));
            }, 0, 1, TimeUnit.MINUTES);

            server.join();
        } catch (Exception e) {
            LOG.error("Error", e);
        }
    }

    private static void addConnector(Server server, int port) {
        ServerConnector http = new ServerConnector(server);
        http.setPort(port);
        server.addConnector(http);
    }

    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            LOG.info("Servlet called");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello World</h1>");
        }
    }
}