interface IMMU {


    int getFrame(int page, boolean write);
    TlbEntry getTlbEntry(int page);
    void resetTlb();
}
