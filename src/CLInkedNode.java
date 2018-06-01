//Circular Linked List
//Each node has reference to next node in list
//The last node references the first node in list
//thus creating a circle
class CLInkedNode<T> {
    T content;
    CLInkedNode<T> next;

    int page;

    //The initial node sets itself as the next element
    CLInkedNode(int page, T content) {
        this.content = content;
        next = this;
        this.page = page;
    }

    //inserts a new node into the list after the current node
    //new node has its next set to the current next
    //current has its next set to the new node
    CLInkedNode<T> insert(int page, T content) {
        CLInkedNode<T> node = new CLInkedNode<>(page, content);
        node.next = this.next;
        this.next = node;
        return node;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("CLinkedNode: ");
        builder.append(page).append(',');
        CLInkedNode<T> current = next;
        while(current.page != page) {
            builder.append(current.page).append(',');
            current = current.next;
        }
        return builder.toString();
    }
}
