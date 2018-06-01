import java.io.File;

abstract class AbstractCPU {

    IMMU mmu;

    AbstractCPU(IMMU mmu) {
        this.mmu = mmu;
    }

    abstract void run(File file);
    abstract void trapOS(int page);
}

