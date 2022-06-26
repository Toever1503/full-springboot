package com.entities;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/*
 * we need cart detail to group product sku
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "tbl_cart")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedDate desc")
    private List<CartDetailEntity> cartDetails;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedDate") // use updatedDate to sort the latest product sku which added to cart
    private Date updatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
