class TLB {

    final TlbEntry[] entries;

    TLB(int size) {
        entries = new TlbEntry[size];
        for (int i = 0; i < size; i++) {
            entries[i] = new TlbEntry();
        }
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("_TLB_\n");
        for (TlbEntry entry : entries) {
            if(entry.valid) {
                builder.append(entry.page).append('|').append(entry.frame).append('|').append(entry.dirty).append('\n');
            }
        }
        return builder.toString();
    }
}
