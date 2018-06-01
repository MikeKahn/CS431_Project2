class MMU implements IMMU {

    private TLB tlb;
    private Computer comp;
    private int index;

    MMU(int tlbSize, Computer comp) {
        this.tlb = new TLB(tlbSize);
        this.comp = comp;
        index = 0;
    }

    //Check if page exists in TLB
    //if miss: check page table
    //if a miss again: trap os and setByIndex page in memory/page table
    public int getFrame(int page, boolean write) {
        TlbEntry result = getTlbEntry(page);
        PageTableEntry entry = comp.pageTable.getEntry(page);
        if(result != null) {
            Logger.setHit();
        } else {
            if(entry.valid) {
                Logger.setSoft();
            } else {
                Logger.setHard();
                comp.cpu.trapOS(page);
            }
            result = insert(page, entry.frame);
        }
        entry.referenced = true;
        result.referenced = true;
        if(write) {
            entry.dirty = true;
            result.dirty = true;
        }
        Logger.out("\tPage %x is in frame %d", result.page, result.frame);
        return result.frame;
    }

    private TlbEntry insert(int page, int frame) {
        if(index >= tlb.entries.length) {
            index = 0;
        }
        TlbEntry entry = tlb.entries[index];
        if(entry.dirty) {
            comp.os.writeToDisk(entry.page);
        }
        tlb.entries[index].set( true, false, false, page, frame);
        return tlb.entries[index++];
    }

    public TlbEntry getTlbEntry(int page) {
        for (TlbEntry entry : tlb.entries) {
            if(entry.page == page && entry.valid) {
                return entry;
            }
        }
        return null;
    }

    public void resetTlb() {
        for(TlbEntry entry: tlb.entries) {
            entry.referenced = false;
        }
    }
}