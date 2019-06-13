package paysys.utils;

import java.io.*;
import java.util.Properties;

/**
 * The {@code PropertiesUtils} is a helper for {@code Properties}
 */
public class PropertiesUtils {
    /**
     * Application's properties
     */
    private static Properties props = null;

    /**
     * Parameter name for service uri
     */
    private final static String SERVICE_URI_PARAM_NAME = "base.uri";
    /**
     * Parameter name for operation's timeout
     */
    private final static String OPERATION_TIMEOUT_PARAM_NAME = "operation.timeout";

    /**
     * Returns {@code Properties} from file
     *
     * @param fileName Name of property file
     * @return {@code Properties} from file
     * @throws FileNotFoundException if file was not found
     */
    private static synchronized Properties getProperties(String fileName) throws FileNotFoundException {
        if (props == null) {
            File file = new File(fileName);
            if (file.exists()) {
                props = loadProps(new FileInputStream(file));
            } else {
                props = loadProps(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));
            }
        }
        return props;
    }

    /**
     * Returns {@code Properties} from stream
     *
     * @param inputStream stream
     * @return {@code Properties} from stream
     */
    private static Properties loadProps(InputStream inputStream) {
        try (InputStream is = inputStream) {
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns uri for service from property file
     *
     * @param fileName Name of property file
     * @return uri for service
     * @throws FileNotFoundException if property file was not found
     */
    public static String getServiceUri(String fileName) throws FileNotFoundException {
        return getProperties(fileName).getProperty(SERVICE_URI_PARAM_NAME);
    }

    /**
     * Returns operation's timeout from property file
     *
     * @param fileName Name of property file
     * @return uri for service
     * @throws FileNotFoundException if file was not found
     */
    public static Long getOperationTimeout(String fileName) throws FileNotFoundException {
        String val = getProperties(fileName).getProperty(OPERATION_TIMEOUT_PARAM_NAME);
        return Long.getLong(val, 5);
    }

    /**
     * @return Parameter name for service uri
     */
    public static String getServiceUriParamName() {
        return SERVICE_URI_PARAM_NAME;
    }

    /**
     * @return Parameter name for operation's timeout
     */
    public static String getOperationTimeoutParamName() {
        return OPERATION_TIMEOUT_PARAM_NAME;
    }
}
