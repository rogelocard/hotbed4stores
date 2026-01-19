package com.cardonaroger.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this); // Initialize the mocks
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //When
        underTest.selectAllCustomers();
        //Then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        Long id = (long) 1;

        //When
        underTest.selectCustomerById(id);

        //Then
//        verify(customerRepository).findById(id);
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer(
                "Yessia",
                "Yessi@gmail.com",
                25
        );
        //When
        underTest.insertCustomer(customer);

        //Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = "foo@gmail.com";
        //When
        underTest.existsPersonWithEmail(email);
        //Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsPersonWithId() {
        //Given
        Long id = (long) 1;

        //When
        underTest.existsPersonWithId(id);

        //Then
        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomer() {
        //Given
        Long id = (long) 1;

        //When
        underTest.deleteCustomer(id);

        //Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = new Customer(
                "Ali", "ali@gmail.com", 2
        );

        //When
        underTest.updateCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }
}