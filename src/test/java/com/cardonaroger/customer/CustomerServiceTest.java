package com.cardonaroger.customer;

import com.cardonaroger.exception.DuplicateResourceException;
import com.cardonaroger.exception.RequestValidationException;
import com.cardonaroger.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class) // Initialize the mocks
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;


    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        Long id = (long) 10;
        Customer customer = new Customer(
                id, "Alex", "Alex@gmail.com", 19
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        //Given
        Long id = (long) 10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer con id [%d] no encontrado".formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "Alex@gmail.com";

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());

    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        //Given
        String email = "Alex@gmail.com";

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("El email ya esta tomado");

        //Then
        Mockito.verify(customerDao, Mockito.never()).insertCustomer(ArgumentMatchers.any());

    }

    @Test
    void deleteCustomerById() {
        //Given
        Long id = 10L;

        Mockito.when(customerDao.existsPersonWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        Mockito.verify(customerDao).deleteCustomer(id);
    }

    @Test void willThrowWhenDeleteCustomerByIdDoesntExist() {
        //Given
        Long id = 10L;

        Mockito.when(customerDao.existsPersonWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer con id [%d] no encontrado");

        //Then
        Mockito.verify(customerDao, Mockito.never()).deleteCustomer(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alexandro", newEmail, 23);

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alexandro", null, null);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 22);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest)).isInstanceOf(DuplicateResourceException.class).hasMessage("El email ya esta tomado");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No se encontraron cambios en los datos");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updateCustomer(any());
    }

}