package com.fynd.test.validation;


import org.springframework.stereotype.Component;

@Component
public class Validator {

   public boolean validateProductCode(long productCode)
    {
        int length = String.valueOf(productCode).length();
        if(length != 12 )
        {
            return false;

        }
        return true;
    }
}

