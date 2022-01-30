package utils.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.net.URLEncoder;
import java.net.URLDecoder;

public class Config {

    public static String CONFIG_LOCATION=Config.class.getClassLoader()
            .getResource("config.properties").getFile();

    public static Properties getProperties() {
        Properties properties=new Properties();
        try {
            properties.load(new FileReader(URLDecoder.decode(CONFIG_LOCATION, "UTF-8")));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config properties");
        }
    }
}
