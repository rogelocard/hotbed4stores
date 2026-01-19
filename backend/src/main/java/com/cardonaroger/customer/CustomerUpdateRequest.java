package com.cardonaroger.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
