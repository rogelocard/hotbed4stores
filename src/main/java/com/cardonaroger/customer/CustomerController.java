package com.cardonaroger.customer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(path = "api/v1/customers", method = RequestMethod.GET)
    public List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }


}
