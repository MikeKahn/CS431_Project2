import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class Logger {

    private ArrayList<String> log;
    private String fileName;
    private boolean debug;
    private boolean soft;
    private boolean hard;
    private boolean hit;
    private boolean dirty;
    private int pageNum;

    private static Logger instance;

    static void init(String fileName, boolean debug) {
        instance = new Logger(fileName);
        instance.debug = debug;
        System.out.println("Logger instance created - output_filename: " + fileName + ", debug: " + debug);
    }

    private Logger(String fileName) {
        log = new ArrayList<>();
        this.fileName = fileName;
        soft = false;
        hard = false;
        hit = false;
        pageNum = -1;
        dirty = false;
    }

    private void reset() {
        soft = false;
        hard = false;
        hit = false;
        pageNum = -1;
        dirty = false;
    }

    static void setSoft() {
        instance.soft = true;
    }

    static void setHard() {
        instance.hard = true;
    }

    static void setHit() {
        instance.hit = true;
    }

    static void setEvicted(int page, boolean dirty) {
        instance.pageNum = page;
        instance.dirty = dirty;
    }

    static void log(int address, boolean read, int value) {
        String entry = String.format("%04X", address) + "," +
                (read ? 0 : 1) + "," +
                value + "," +
                (instance.soft ? 1 : 0) + "," +
                (instance.hard ? 1 : 0) + "," +
                (instance.hit ? 1 : 0) + "," +
                instance.pageNum + "," +
                (instance.dirty ? 1 : 0);
        instance.log.add(entry);
        instance.reset();
    }

    static void out(String message, Object ... args) {
        if(instance.debug) {
            System.out.println(String.format(message, args));
        }
    }

    static void export() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(instance.fileName + ".csv"))) {
            writer.write("Address,r/w,value,soft,hard,hit,evicted_pg#,dirty_evicted_page\n");
            for (String entry: instance.log) {
                writer.write(entry + "\n");
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
