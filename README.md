#  Warehouse
Warehouse is an application that helps user to manage all the products record as per color and slot.

## Features
User can perform following operations -
  - create slots in warehouse
  - check the status of waresouse
  - Get product code as per slot
  - Get slot as per color
  - Get slot as per product code

## Requirements
For building and running the application you need:
    - JDK 1.8
    - Maven 3
    - Spring Boot
    - H2 Database
    - Spring Data JPA

## Running the application locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the main method in the com.fynd.test.WebApplication class from your IDE.
    Alternatively you can use the Spring Boot Maven plugin like so:

```bash
mvn spring-boot:run
```

### End Points
end points for local environment.
#### create
    http://localhost:8080/create

    {
        "count": {count}
    }

#### Status
    http://localhost:8080/status
#### Store
    http://localhost:8080/store

    {
    	"productCode" : {productCode},
    	"color" : {color}

    }

#### Sell
    http://localhost:8080/sell?slot={slot_Num}
#### Product Code For Color
    http://localhost:8080/product_code_for_color?color={color}
#### Slot Number For Color
    http://localhost:8080/slot_num_for_color?color={color}
#### Slot Number For Product Code
    http://localhost:8080/slot_num_for_product_code?productCode={productCode}

#### Todos

#### License
----
**Free Software!**


