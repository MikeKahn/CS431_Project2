/*
 * Created by Michael on 2/19/2018.
 */
class PageTableEntry {

    boolean valid;
    boolean referenced;
    boolean dirty;
    int frame;

    PageTableEntry() {
        valid = false;
        referenced = false;
        dirty = false;
        frame = 0;
    }

    void reset() {
        valid = false;
        referenced = false;
        dirty = false;
        frame = -1;
    }

    void set(boolean valid, boolean referenced, boolean dirty, int frame) {
        this.valid = valid;
        this.referenced = referenced;
        this.dirty = dirty;
        this.frame = frame;
    }
}
