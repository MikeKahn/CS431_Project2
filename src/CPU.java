import java.io.*;

class CPU extends AbstractCPU {

    private Computer comp;

    CPU(int tlbSize, Computer comp) {
        super(new MMU(tlbSize, comp));
        this.comp = comp;
    }

    private void execute(boolean read, int address, int value) {
        int page = address >> 8;
        int offset = address & 0xFF;
        int frame = mmu.getFrame(page, !read);
        if(read) {
            value = comp.pMemory.read(frame, offset);
        } else {
            comp.pMemory.write(frame, offset, value);
        }
        Logger.out("\tValue %s: %d", read ? "read" : "written", value);
        Logger.log(address, read, value);
    }

    //tells the os to load the missing page into physical memory
    void trapOS(int page) {
        comp.os.loadFromDisk(page);
    }

    //read instruction from file
    //execute instruction
    //call os reset
    //once all instructions are called, call os to write all dirty pages to disk
    void run(File file) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while((line = reader.readLine()) != null) {
                boolean read = Integer.parseInt(line) == 0;
                int address = Integer.parseInt(reader.readLine(),16);
                int value = -1;
                if(!read) {
                    value = Integer.parseInt(reader.readLine());
                }
                Logger.out("Instruction: %s at %x", read ? "read" : "write", address);
                execute(read, address, value);
                comp.os.reset();
            }
        } catch(IOException | NumberFormatException| NullPointerException e) {
            e.printStackTrace();
            return;
        }
        comp.os.writeAllDirty();
    }
}
