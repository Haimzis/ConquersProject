package Tasks;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class XMLLoader extends Task<Boolean> {
    private String fileName;
    private final long SLEEP_TIME = 5;

    XMLLoader(String fileName){
        this.fileName = fileName;
    }
    @Override
    protected Boolean call() throws Exception {
        return true;
    }
}
