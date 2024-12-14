package com.example.blog.inheritinverse;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerMapperTest {

    @Test
    void toDto() {
        Customer customer = new Customer(1L, "소비자 이름");

        CustomerDto customerDto = CustomerMapper.INSTANCE.toDto(customer);

        assertThat(customerDto.id).isEqualTo(customer.getId());
        assertThat(customerDto.customerName).isEqualTo(customer.getName());
    }

    @Test
    void toCustomer() {
        CustomerDto customerDto = new CustomerDto(1L, "소비자 이름");

        Customer customer = CustomerMapper.INSTANCE.toCustomer(customerDto);

        assertThat(customerDto.id).isEqualTo(customer.getId());
        assertThat(customerDto.customerName).isEqualTo(customer.getName());
    }
}
