package com.cardonaroger.customer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getLong("id")).thenReturn(1L);
        Mockito.when(resultSet.getInt("age")).thenReturn(19);
        Mockito.when(resultSet.getString("name")).thenReturn("Jamila");
        Mockito.when(resultSet.getString("email")).thenReturn("jamila@gmail.com");

        //When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        //Then
        Customer expected = new Customer(
                1L,
                "Jamila",
                "jamila@gmail.com",
                19
        );
        assertThat(actual).isEqualTo(expected);
    }
}