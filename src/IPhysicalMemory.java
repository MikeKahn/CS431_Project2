interface IPhysicalMemory {

    int read(int frame, int offset);

    int[] getFrame(int frame);

    void write(int frame, int offset, int value);

    void writePage(int frame, int[] page);

    int getFrameCount();

    int getFrameSize();
}
