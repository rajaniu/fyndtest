 package com.fynd.test.model;

import javax.persistence.*;
import java.util.Objects;

 @Entity
@Table(name = "Warehouse")
public class Warehouse {

    @Id
    @Column(name ="Slot_Num")
    private int slotNum;

    @Column(name = "Product_Code")
    private long productCode;

    @Column(name ="Color")
    private String color;

    public Warehouse()
    {

    }
    public Warehouse(int slotNum, long productCode, String color) {
        this.slotNum = slotNum;
        this.productCode = productCode;
        this.color = color;
    }

    public int getSlotNum() {
        return slotNum;
    }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Warehouse)) return false;
         Warehouse warehouse = (Warehouse) o;
         return getSlotNum() == warehouse.getSlotNum() &&
                 getProductCode() == warehouse.getProductCode() &&
                 Objects.equals(getColor(), warehouse.getColor());
     }

     @Override
     public int hashCode() {
         return Objects.hash(getSlotNum(), getProductCode(), getColor());
     }

     @Override
     public String toString() {
         return "{" +
                 "slotNum:" + slotNum +
                 ", productCode:" + productCode +
                 ", color:'" + color + '\'' +
                 '}';
     }

     public void setSlotNum(int slotNum) {
        this.slotNum = slotNum;
    }

    public long getProductCode() {
        return productCode;
    }

    public void setProductCode(long productCode) {
        this.productCode = productCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
