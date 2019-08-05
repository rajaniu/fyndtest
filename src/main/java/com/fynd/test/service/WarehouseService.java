package com.fynd.test.service;
import com.fynd.test.model.Warehouse;

import java.util.List;

public interface WarehouseService {

    void save(Warehouse warehouse);
    List<Warehouse> findAll();
    Warehouse findSlot();
    int delete(int slotNum);
    List<Warehouse> findByColor(String color);
    List<Warehouse> findByProductCode(long productCode);
}
