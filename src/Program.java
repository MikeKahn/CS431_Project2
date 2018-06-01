import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Michael Kahn.
 * CS 431 Project 2: Virtual Memory Simulator
 */
public class Program {

    private static boolean DEBUG = false;
    private static final String DEFAULT_PAGE_PATH = "page_files";
    private static final String WORKING_PATH = "working_";
    private static final int CPU_ADDRESS_WIDTH = 16;
    private static final int P_MEM_ADDRESS_WIDTH = 12;
    private static final int PAGE_OFFSET_SIZE = 8;
    private static final int TLB_SIZE = 8;
    private static final int RESET = 5;

    public static void main(String[] args) {
        long start = System.nanoTime();
        if(args.length == 0) {
            System.err.println("Invalid arguments: missing file path argument.");
            return;
        } else if(args.length > 1 && args[1].equalsIgnoreCase("debug")) {
            DEBUG = true;
        }

        File file = new File(args[0]);
        if(!file.exists()) {
            System.err.println("File given as argument does not exist.");
            return;
        }

        int index = file.getName().lastIndexOf('.');
        String fileName = index > 0 ? file.getName().substring(0,index) : file.getName();
        String working_path = WORKING_PATH + fileName;

        Logger.init(fileName, DEBUG);
        System.out.println("Copying page files to working directory.");
        try {
            copyPages(working_path);
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Finished creating copies.");
        Logger.out("System:\n\tcpu address width: %d\n\tPhysical memory address width: %d\n\tPage offset: %d" +
                        "\n\tTLB size: %d\n\treset: %d\n\tworking path: %s",
                        CPU_ADDRESS_WIDTH, P_MEM_ADDRESS_WIDTH, PAGE_OFFSET_SIZE, TLB_SIZE,RESET, WORKING_PATH);

        Computer comp = new Computer();
        AbstractCPU cpu = new CPU(TLB_SIZE, comp);
        IPhysicalMemory pMemory = new PhysicalMemory(P_MEM_ADDRESS_WIDTH, PAGE_OFFSET_SIZE);
        IPageTable pageTable = new PageTable(CPU_ADDRESS_WIDTH, PAGE_OFFSET_SIZE);
        IOS os = new OS(working_path, RESET, pMemory.getFrameCount(), comp);
        comp.init(os,cpu,pMemory,pageTable);

        System.out.println("Executing Instructions.");
        comp.run(file);
        Logger.out("Execution time: %fms", (System.nanoTime() - start)/1000000.0);
        System.out.println("Finished, exporting data.");
        Logger.export();
    }

    //creates a working copy of the starting page files
    private static void copyPages(String working_path) throws IOException {
        File directory = new File(DEFAULT_PAGE_PATH);
        File[] files = directory.listFiles();
        Path out = FileSystems.getDefault().getPath(working_path);
        Files.createDirectories(out);
        if(files != null) {
            for(File file: files) {
                Path source = file.toPath();
                Files.copy(source, out.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

}
