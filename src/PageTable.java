class PageTable implements IPageTable {

    final PageTableEntry[] entries;

    PageTable(int cpuAddressWidth, int pageOffsetSize) {
        entries = new PageTableEntry[(int)Math.pow(2,cpuAddressWidth - pageOffsetSize)];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new PageTableEntry();
        }
    }

    public PageTableEntry getEntry(int page) {
        return entries[page];
    }

    public int getSize() {
        return entries.length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("_PageTable_\n");
        for (int i = 0; i < entries.length; i++) {
            if(entries[i].valid) {
                builder.append(i).append('|').append(entries[i].frame).append('|').append(entries[i].referenced).append('\n');
            }
        }
        return builder.toString();
    }

}
