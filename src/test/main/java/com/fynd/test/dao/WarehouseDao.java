package com.fynd.test.dao;

import com.fynd.test.model.Warehouse;
import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface WarehouseDao extends CrudRepository<Warehouse, Long> {

    Warehouse save(Warehouse warehouse);
    List<Warehouse> findAll();
    Warehouse  findTopByProductCodeOrderBySlotNum(long productCode);
    Warehouse findBySlotNum(int slotNum);
    List<Warehouse> findByColor(String color);
    List<Warehouse> findByProductCode(long productCode);
    List<Warehouse> findByColorNotNullAndProductCodeGreaterThan(long productCode);
}
