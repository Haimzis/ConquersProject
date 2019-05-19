package Tasks;

import javafx.concurrent.Task;

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
