 package com.fynd.test.service.impl;

import com.fynd.test.dao.WarehouseDao;
import com.fynd.test.model.Warehouse;
import com.fynd.test.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseDao warehouseDao;

    @Override
    public void save(Warehouse warehouse) {
       warehouseDao.save(warehouse);
    }

    @Override
    public List<Warehouse> findAll() {
       return warehouseDao.findByColorNotNullAndProductCodeGreaterThan(0);
    }

    @Override
    public Warehouse findSlot()
    {
       return warehouseDao.findTopByProductCodeOrderBySlotNum(0);

    }

    @Override
    public int delete(int slotNum)
    {
        Warehouse warehouse = warehouseDao.findBySlotNum(slotNum);
        if(warehouse != null)
        {
            warehouse.setColor(null);
            warehouse.setProductCode(0);
            Warehouse updatedWare = warehouseDao.save(warehouse);
            return updatedWare.getSlotNum();
        }
        return 0;
    }

    @Override
    public List<Warehouse> findByColor(String color) {
        return warehouseDao.findByColor(color);
    }

    @Override
    public List<Warehouse> findByProductCode(long productCode) {
        return warehouseDao.findByProductCode(productCode);
    }

}
