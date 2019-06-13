package paysys;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import paysys.utils.PropertiesUtils;

import java.io.FileNotFoundException;
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
     * @return Started server
     * @throws FileNotFoundException if property file was not found
     */
    private static HttpServer startServer() throws FileNotFoundException {
        String uri = PropertiesUtils.getServiceUri(APP_PROP_FILE_NAME);
        if (StringUtils.isEmpty(uri)) {
            throw new IllegalStateException(String.format("Service not started. No %s in the file %s",
                    PropertiesUtils.getServiceUriParamName(), APP_PROP_FILE_NAME));
        }
        ResourceConfig rc = new ResourceConfig().packages("paysys.controller");
        rc.property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
        rc.register(new ApplicationBinder());
        rc.property(PropertiesUtils.getOperationTimeoutParamName(), PropertiesUtils.getOperationTimeout(APP_PROP_FILE_NAME));
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc);
    }

    public static void main(String[] args) {
        try {
            HttpServer server = startServer();
            log.info("Jersey app started.");
            System.out.println("Hit enter to stop it...");
            System.in.read();
            server.shutdownNow();
            log.info("Jersey app stopped.");
            System.exit(0);
        } catch (Exception e) {
            log.error("Service not started.", e);
        }
    }

}
