/*
 * Created by Michael on 2/19/2018.
 */
class TlbEntry {

    boolean valid;
    boolean referenced;
    boolean dirty;
    int page;
    int frame;

    TlbEntry() {
        valid = false;
        referenced = false;
        dirty = false;
        page = -1;
        frame = -1;
    }

    void reset() {
        valid = false;
        referenced = false;
        dirty = false;
        page = -1;
        frame = -1;
    }

    void set(boolean valid, boolean referenced, boolean dirty, int page, int frame) {
        this.valid = valid;
        this.referenced = referenced;
        this.dirty = dirty;
        this.page = page;
        this.frame = frame;
    }
}
