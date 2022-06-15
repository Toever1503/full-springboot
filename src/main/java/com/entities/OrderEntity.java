package com.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "tbl_order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_code", unique = true)
    private String uuid;

    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "note")
    private String note;
    @Column(name = "total_prices")
    private Double totalPrices;
    @Column(name = "total_products")
    private Integer totalNumberProducts;
    @Column(name = "status")
    private String status;

    @Column(name = "transaction_no")
    private String transactionNo;

    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderDetailEntity> orderDetails;
}
