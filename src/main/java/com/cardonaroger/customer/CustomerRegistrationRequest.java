package com.cardonaroger.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
