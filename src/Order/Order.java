package Order;

public class Order{
    private OType type;
    private int size;
    private int price;

    public Order(OType type2, int size, int price) {
        this.type = type2;
        this.size = size;
        this.price = price;
    }

    public OType getType() {
        return type;
    }

    public void setType(OType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    
}
