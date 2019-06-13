package paysys.utils;

import java.io.*;
import java.util.Properties;

/**
 * The {@code PropertiesUtils} is a helper for {@code Properties}
 */
public class PropertiesUtils {

    /**
     * Returns {@code Properties} from file
     *
     * @param fileName Name of property file
     * @return {@code Properties} from file
     * @throws FileNotFoundException if file was not found
     */
    private static Properties getProperties(String fileName) throws FileNotFoundException {
        Properties props;
        File file = new File(fileName);
        if (file.exists()) {
            props = loadProps(new FileInputStream(file));
        } else {
            props = loadProps(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));
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
     * @throws FileNotFoundException if file was not found
     */
    public static String getServiceUri(String fileName) throws FileNotFoundException {
        return getProperties(fileName).getProperty("base.uri");
    }
}
