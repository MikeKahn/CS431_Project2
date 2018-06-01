class PhysicalMemory implements IPhysicalMemory {

    private int frameSize;
    private int frameCount;

    private int[][] memory;

    PhysicalMemory(int pMemAddressWidth, int pageOffsetSize) {
        frameCount = (int)Math.pow(2,pMemAddressWidth - pageOffsetSize);
        frameSize = (int)Math.pow(2, pageOffsetSize);
        memory = new int[frameCount][frameSize];
    }

    public int read(int frame, int offset) {
        return memory[frame][offset];
    }

    public int[] getFrame(int frame) {
        return memory[frame];
    }

    public void write(int frame, int offset, int value) {
        memory[frame][offset] = value;
    }

    public void writePage(int frame, int[] data) {
        memory[frame] = data;
    }

    @Override
    public int getFrameCount() {
        return frameCount;
    }

    @Override
    public int getFrameSize() {
        return frameSize;
    }
}
