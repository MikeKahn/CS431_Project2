import java.io.*;

class OS implements IOS {

    private Computer comp;
    private String pagePath;
    private int reset;
    private int counter = 0;
    private int free;
    private CLInkedNode<PageTableEntry> pointer;

    OS(String pagePath, int reset, int frameCount, Computer comp) {
        this.pagePath = pagePath;
        this.reset = reset;
        this.comp = comp;
        free = frameCount;
        pointer = null;
    }

    public void loadFromDisk(int page) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pagePath + "/" + String.format("%02X", page) + ".pg")))){
            int[] data = new int[comp.pMemory.getFrameSize()];
            int index = 0;
            String line;
            while((line = reader.readLine()) != null) {
                data[index++] = Integer.parseInt(line);
            }
            insertToPageTable(page);
            comp.pMemory.writePage(comp.pageTable.getEntry(page).frame, data);
        } catch(IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void writeToDisk(int page) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pagePath + "/" + String.format("%02X", page) + ".pg")))){
            int[] data = comp.pMemory.getFrame(comp.pageTable.getEntry(page).frame);
            for (int line : data) {
                writer.write(line + "\n");
            }
        } catch(IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void insertToPageTable(int page) {
        if(pointer == null) {
            //No current nodes in list, create initial node
            pointer = new CLInkedNode<>(page, comp.pageTable.getEntry(page));
            pointer.content.set(true, false, false, 0);
            free--;
            return;
        } else if(free > 0) {
            //There are still free frames available, so add on to list
            pointer = pointer.insert(page, comp.pageTable.getEntry(page));
            pointer.content.set(true, false, false, comp.pMemory.getFrameCount() - free);
            free--;
            //If this is the last free node, move pointer to the root node
            if(free <= 0) {
                pointer = pointer.next;
            }
            return;
        }

        while(true) {
            //check if current node has been referenced
            //dereference if it has, and move onto to next node
            if(pointer.content.referenced) {
                pointer.content.referenced = false;
                pointer = pointer.next;
            } else {
                int frame = pointer.content.frame;
                //check if page to be removed is dirty
                //write page to disk if its dirty
                if(pointer.content.dirty) {
                    comp.os.writeToDisk((pointer.page));
                }
                Logger.setEvicted(pointer.page, pointer.content.dirty);
                Logger.out("\tEvicted from page table: %d %s, frame: %d", pointer.page, pointer.content.dirty ? "| dirty" : "", frame);
                //Reset entries of the page in the page table and TLB(if its in the tlb)
                pointer.content.reset();
                TlbEntry entry = comp.cpu.mmu.getTlbEntry(pointer.page);
                if(entry != null) {
                    entry.reset();
                }
                //set frame to new page
                pointer.content = comp.pageTable.getEntry(page);
                pointer.content.set(true,false,false, frame);
                pointer.page = page;
                //move pointer to next node as this node was just set
                pointer = pointer.next;
                return;
            }
        }
    }

    //Writes all current dirty pages to disk
    public void writeAllDirty() {
        int initPage = pointer.page;
        CLInkedNode<PageTableEntry> current = pointer;
        do {
            if(current.content.valid && current.content.dirty) {
                writeToDisk(current.page);
            }
            current.content.reset();
            current = current.next;
        } while(current.page != initPage);
    }

    //Should be called after every execution of an instruction
    //Increments instruction counter
    //When the reset value is reached, dereference all entries in page table/tlb
    public void reset() {
        counter++;
        if(counter >= reset) {
            counter = 0;
            resetTables();
        }
    }

    private void resetTables() {
        int initPage = pointer.page;
        CLInkedNode<PageTableEntry> current = pointer;
        do {
            current.content.referenced = false;
            current = current.next;
        } while(current.page != initPage);
        comp.cpu.mmu.resetTlb();
    }
}
