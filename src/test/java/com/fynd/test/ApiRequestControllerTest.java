package com.fynd.test;

import com.fynd.test.model.Warehouse;
import com.fynd.test.service.WarehouseService;
import com.fynd.test.validation.Validator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
public class ApiRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private ApiRequestController apiRequestController;

    private Warehouse mockWarehouse;

    @Mock
    private Validator validator;

    @Before
    public void setUp() throws Exception
    {

        mockMvc = MockMvcBuilders.standaloneSetup(apiRequestController).build();
        mockWarehouse = new Warehouse( 1,111111111111L,  "green");
    }


    @Test
    public void getStatusTest() throws Exception {

        Mockito.when(warehouseService.findAll()).thenReturn(Collections.singletonList(mockWarehouse));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/status").accept(
                MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals("["+mockWarehouse+"]", result.getResponse().getContentAsString(), false);
    }

    @Test
    public void getStatusNegativeTest() throws Exception {

        Mockito.when(warehouseService.findAll()).thenReturn(Collections.emptyList());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/status").accept(
                MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals("Warehouse is Empty", result.getResponse().getContentAsString());
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }


    @Test
    public void createTest() throws Exception

    {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(warehouseService).save(mockWarehouse);

        String json = "{\n" + "\"count\": 6\n" + "}";
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .post("/create")
                .accept(MediaType.APPLICATION_JSON).content(json))
                .andReturn();
        String expected = "Created a warehouse with 6 slots";
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    }


    @Test
    public void createAlredyCreatedTest() throws Exception

    {
        Mockito.when(warehouseService.findAll()).thenReturn(Collections.singletonList(mockWarehouse));

        String json = "{\n" + "\"count\": 6\n" + "}";
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .post("/create")
                .accept(MediaType.APPLICATION_JSON).content(json))
                .andReturn();
        String expected = "Slots is already being created";
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    }

    @Test
    public void storeTest() throws Exception
    {
        Mockito.when(warehouseService.findByProductCode(Mockito.anyLong())).thenReturn(Collections.emptyList());
        Mockito.when(warehouseService.findSlot()).thenReturn(mockWarehouse);
        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(warehouseService).save(mockWarehouse);
        String json = "{\"productCode\" : \"111111111115\",\n" +
                "\t\"color\" : \"White\"\n" +"}";

        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .put("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        String expected = "Allocated Slot Number : "+mockWarehouse.getSlotNum();
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }


    @Test
    public void storeWarehouseFullTest() throws Exception
    {
        Mockito.when(warehouseService.findByProductCode(Mockito.anyLong())).thenReturn(Collections.emptyList());
        Mockito.when(warehouseService.findSlot()).thenReturn(null);
        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(true);
        String json = "{\"productCode\" : \"111111111115\",\n" +
                "\t\"color\" : \"White\"\n" +"}";

        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .put("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        String expected = "Warehouse is full";
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }


    @Test
    public void storeProductCodeTest() throws Exception
    {
        Mockito.when(warehouseService.findByProductCode(Mockito.anyLong())).thenReturn(Collections.emptyList());
        Mockito.when(warehouseService.findSlot()).thenReturn(mockWarehouse);
        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(false);
        String json = "{\"productCode\" : \"111111111115\",\n" +
                "\t\"color\" : \"White\"\n" +"}";

        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .put("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        String expected = "Enter a valid product code";

        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());
    }



    @Test
    public void storeInvalidProductCodeTest() throws Exception
    {
        Mockito.when(warehouseService.findByProductCode(Mockito.anyLong())).thenReturn(Collections.singletonList(mockWarehouse));
        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(true);
        String json = "{\"productCode\" : \"111111111115\",\n" +
                "\t\"color\" : \"White\"\n" +"}";

        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .put("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        String expected = "Please enter a unique product code we already have entry for this product code";
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());
    }



    @Test
    public void sellTest() throws Exception
    {
        String slot = "1";
        Mockito.when(warehouseService.delete(Mockito.anyInt())).thenReturn(1);
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/sell")
                .accept(MediaType.APPLICATION_JSON).param("slot", slot))
                .andReturn();
        String expected =" Slot Number " + slot +" is free";
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void sellSlotNotFoundTest() throws Exception
    {
        String slot = "1";
        Mockito.when(warehouseService.delete(Mockito.anyInt())).thenReturn(0);
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/sell")
                .accept(MediaType.APPLICATION_JSON).param("slot", slot))
                .andReturn();
        String expected ="Given slot not found in our record";
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void sellInvalidSlotTest() throws Exception
    {
        String slot = "0";
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/sell")
                .accept(MediaType.APPLICATION_JSON).param("slot", slot))
                .andReturn();
        String expected ="enter a valid slot number";
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(expected, mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void productCodeForColorTest() throws Exception
    {

        Mockito.when(warehouseService.findByColor(Mockito.anyString())).thenReturn(Collections.singletonList(mockWarehouse));
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/product_code_for_color")
                .accept(MediaType.APPLICATION_JSON).param("color", "green"))
                .andReturn();
        List<Long> slots = new ArrayList<Long>();
        slots.add(111111111111L);
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(slots.toString(), mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void productCodeForColorNotFoundTest() throws Exception
    {

        Mockito.when(warehouseService.findByColor(Mockito.anyString())).thenReturn(Collections.emptyList());
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/product_code_for_color")
                .accept(MediaType.APPLICATION_JSON).param("color", "green"))
                .andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals("Given color not found in our record", mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void productCodeForColorInvalidTest() throws Exception
    {

        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/product_code_for_color")
                .accept(MediaType.APPLICATION_JSON).param("color", ""))
                .andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals("please input valid color", mvcResult.getResponse().getContentAsString());

    }


    @Test
    public void slotNumForColorTest() throws Exception
    {

        Mockito.when(warehouseService.findByColor(Mockito.anyString())).thenReturn(Collections.singletonList(mockWarehouse));
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/slot_num_for_color")
                .accept(MediaType.APPLICATION_JSON).param("color", "green"))
                .andReturn();
        List<Integer> slots = new ArrayList<Integer>();
        slots.add(1);

        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(slots.toString(), mvcResult.getResponse().getContentAsString());

    }
    @Test
    public void slotNumForColorNotFoundTest() throws Exception
    {

        Mockito.when(warehouseService.findByColor(Mockito.anyString())).thenReturn(Collections.emptyList());
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/slot_num_for_color")
                .accept(MediaType.APPLICATION_JSON).param("color", "red"))
                .andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals("Given color not found in our record", mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void slotNumForColorInvalidTest() throws Exception
    {

        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/slot_num_for_color")
                .accept(MediaType.APPLICATION_JSON).param("color", ""))
                .andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals("please input valid color", mvcResult.getResponse().getContentAsString());

    }


    @Test
    public void slotNumForproductCodeTest() throws Exception
    {

        Mockito.when(warehouseService.findByProductCode(Mockito.anyLong())).thenReturn(Collections.singletonList(mockWarehouse));
        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(true);
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/slot_num_for_product_code")
                .accept(MediaType.APPLICATION_JSON).param("productCode", "111111111111"))
                .andReturn();
        List<Integer> slots = new ArrayList<Integer>();
        slots.add(1);

        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals(slots.toString(), mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void slotNumForproductCodeNotFoundTest() throws Exception
    {

        Mockito.when(warehouseService.findByProductCode(Mockito.anyLong())).thenReturn(Collections.emptyList());
        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(true);
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/slot_num_for_product_code")
                .accept(MediaType.APPLICATION_JSON).param("productCode", "111111111114"))
                .andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals("productCode is not found in our record", mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void slotNumForproductCodeInvalidTest() throws Exception
    {

        Mockito.when(validator.validateProductCode(Mockito.anyLong())).thenReturn(false);
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders
                .get("/slot_num_for_product_code")
                .accept(MediaType.APPLICATION_JSON).param("productCode", "111111111114"))
                .andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Assert.assertEquals("Please input a valid product code", mvcResult.getResponse().getContentAsString());

    }





}