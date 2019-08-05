package com.fynd.test;
import com.fynd.test.model.Warehouse;
import com.fynd.test.service.WarehouseService;
import com.fynd.test.validation.Validator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class ApiRequestController {

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private Validator validator;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<String> create(@RequestBody String payload) {
        JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();
        int count = jsonObject.get("count").getAsInt();
        List<Warehouse> warehouseList = warehouseService.findAll();
        if(warehouseList.isEmpty()) {
            for (int i = 1; i <= count; i++) {
                Warehouse warehouse = new Warehouse();
                warehouse.setSlotNum(i);
                warehouseService.save(warehouse);
            }
            return ResponseEntity.ok("Created a warehouse with " + count + " slots");
        }
        else
        {
            return ResponseEntity.ok("Slots is already being created");
        }
    }

    @RequestMapping(value = "status", method = RequestMethod.GET)
    public ResponseEntity getStatus()
    {
        List<Warehouse> warehouseList = warehouseService.findAll();
        if(warehouseList.isEmpty())
        {
            return ResponseEntity.ok("Warehouse is Empty");
        }
        return new ResponseEntity<List>(warehouseList, HttpStatus.OK);
    }

    @RequestMapping(value = "store", method = RequestMethod.PUT)
    public ResponseEntity<String> store (@Valid @RequestBody Warehouse request)
    {
        long productCode = request.getProductCode();
        String color = request.getColor().toLowerCase();

        ResponseEntity<String> responseEntity = null;
        boolean validation = validator.validateProductCode(productCode);
        if(!validation) {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter a valid product code");
        }
        else
        {
            List<Warehouse> dbWarehouse  = warehouseService.findByProductCode(productCode);
            if(dbWarehouse.isEmpty()) {
                Warehouse warehouse = warehouseService.findSlot();
                if (null != warehouse) {
                    warehouse.setColor(color);
                    warehouse.setProductCode(productCode);
                    warehouseService.save(warehouse);
                    responseEntity =  ResponseEntity.ok("Allocated Slot Number : " + warehouse.getSlotNum());
                } else {
                    responseEntity = ResponseEntity.ok("Warehouse is full");
                }
            }
            else
            {
                responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please enter a unique product code we already have entry for this product code");
            }
        }

        return responseEntity;
    }

    @RequestMapping(value = "sell", method = RequestMethod.GET)
    public ResponseEntity<String> sell(@RequestParam(value = "slot") int slot)
    {
        ResponseEntity<String> responseEntity;
        if(slot != 0)
        {
            int deleted = warehouseService.delete(slot);
            if(deleted != 0)
                responseEntity= ResponseEntity.ok(" Slot Number " + deleted +" is free");
            else
                responseEntity=  ResponseEntity.ok("Given slot not found in our record");

        }
        else {
            responseEntity  = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("enter a valid slot number");
        }
       return responseEntity;

    }

    @RequestMapping(value="product_code_for_color", method = RequestMethod.GET)
    public ResponseEntity<String> productCodeForColor(@RequestParam(value = "color") String color)
    {
        ResponseEntity<String> responseEntity;
        if(color != null && !color.isEmpty())
        {
            List<Warehouse> warehouse  = warehouseService.findByColor(color.toLowerCase());
            if (!warehouse.isEmpty()) {
                List<Long> productcodes = new ArrayList<Long>();
                for (Warehouse w : warehouse)
                    productcodes.add(w.getProductCode());
                responseEntity =  ResponseEntity.ok(productcodes.toString());
            }
            else
            {
                responseEntity = ResponseEntity.ok("Given color not found in our record");
            }
        }
        else {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please input valid color");
        }
        return responseEntity;
    }

    @RequestMapping(value="slot_num_for_color", method = RequestMethod.GET)
    public ResponseEntity<String> slotNumForColor(@RequestParam(value = "color") String color)
    {
        ResponseEntity<String> responseEntity;
        if(color != null && !color.isEmpty())
        {
            List<Warehouse> warehouse  = warehouseService.findByColor(color.toLowerCase());
            if(warehouse.size() != 0) {
                List<Integer> slots = new ArrayList<Integer>();
                for (Warehouse w : warehouse)
                    slots.add(w.getSlotNum());
                responseEntity =  ResponseEntity.ok(slots.toString());
            }
            else
            {
                responseEntity = ResponseEntity.status(HttpStatus.OK).body("Given color not found in our record");
            }
        }
        else
        {
            responseEntity=  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please input valid color");
        }
        return responseEntity;

    }

    @RequestMapping(value="slot_num_for_product_code", method = RequestMethod.GET)
    public ResponseEntity<String> slotNumForproductCode(@RequestParam(value = "productCode") long productCode)
    {
        ResponseEntity<String> responseEntity;
        boolean validation =  validator.validateProductCode(productCode);
        if(!validation)
        {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please input a valid product code");
        }

        else
        {
            List<Warehouse> warehouse  = warehouseService.findByProductCode(productCode);
            if(!warehouse.isEmpty()) {
                List<Integer> slots = new ArrayList<Integer>();
                for (Warehouse w : warehouse)
                    slots.add(w.getSlotNum());
                responseEntity =  ResponseEntity.ok(slots.toString());
            }
            else
            {
                responseEntity = ResponseEntity.ok("productCode is not found in our record");
            }
        }

        return responseEntity;

    }
}