package Order;

import exceptions.InvalidatePriceSizeException;

public class Order {
    private OType type;
    private int size;
    private int price;

    public Order(OType type2, int size, int price) {
        this.type = type2;
        this.size = size;
        this.price = price;
    }

    public Order(Order other) {
        this.type = other.type;
        this.size = other.size;
        this.price = other.price;
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

    public static void validateOrderInput(int size, int price)throws InvalidatePriceSizeException{

        final int MAX = 2147483647;
        final int MIN = 1;

        if(size < MIN || price < MIN || size > MAX || price > MAX){
            throw new InvalidatePriceSizeException("Valore non valido");
        }
    }

}
