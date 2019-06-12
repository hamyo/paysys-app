package paysys;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import paysys.utils.PropertiesUtils;

import java.net.URI;

@Slf4j
public class Application {

    /**
     * Properties file name
     */
    private static final String APP_PROP_FILE_NAME = "application.properties";

    /**
     * Start server
     *
     * @param uri Uri for server
     * @return Started server
     */
    private static HttpServer startServer(String uri) {
        ResourceConfig rc = new ResourceConfig().packages("paysys.controller");
        rc.property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
        rc.register(new ApplicationBinder());
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc);
    }

    public static void main(String[] args) {
        try {
            String uri = PropertiesUtils.getServiceUri(APP_PROP_FILE_NAME);
            if (StringUtils.isEmpty(uri)) {
                log.error("Service not started. No url in the file {}", APP_PROP_FILE_NAME);
            } else {
                HttpServer server = startServer(uri);
                log.info("Jersey app started. It is available at {}", uri);
                System.out.println("Hit enter to stop it...");
                System.in.read();
                server.shutdownNow();
                log.info("Jersey app stopped");
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("Service not started", e);
        }
    }

}
