import java.io.File;

class Computer {

    AbstractCPU cpu;
    IOS os;
    IPhysicalMemory pMemory;
    IPageTable pageTable;

    void init(IOS os, AbstractCPU cpu, IPhysicalMemory pMemory, IPageTable pageTable) {
        this.pMemory = pMemory;
        this.pageTable = pageTable;
        this.os = os;
        this.cpu = cpu;
    }

    void run(File file) {
        cpu.run(file);
    }
}
