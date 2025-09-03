package Properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Prop {
    private Properties properties;

    public Prop(String filename) throws IOException {
        properties = new Properties();
        File configFile = new File(filename);
        
        try (FileReader reader = new FileReader(configFile)) {
            properties.load(reader);
        } catch (FileNotFoundException ex) {
            // file non esiste
            System.err.println(ex.getMessage());
            throw ex;
        } catch (IOException ex) {
            // I/O error
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

    public String get(String key) {
        // System.out.println(properties.getProperty(key));
        if (!properties.containsKey(key)) {
            throw new IllegalArgumentException("File di configurazione non configurato bene \n !! key: " + key);
        }
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        // System.out.println(properties.getProperty(key));
        if (!properties.containsKey(key)) {
            throw new IllegalArgumentException("File di configurazione non configurato bene \n !! key: " + key);
        }
        return Integer.parseInt(properties.getProperty(key));
    }
}
