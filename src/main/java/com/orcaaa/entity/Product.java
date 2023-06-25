package com.orcaaa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "product_id")
    private String productId = UUID.randomUUID().toString();

    @NotBlank(message = "Harga produk tidak boleh kosong")
    @Column(name = "product_price")
    private String productPrice;

    @NotBlank(message = "Deskripsi produk tidak boleh kosong")
    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "stock")
    private int stock;

    @OneToMany(mappedBy = "product")
    private List<Order> orders;

}
