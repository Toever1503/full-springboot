package com.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_ward")
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "_name", nullable = false, length = 50)
    private String name;

    @Column(name = "_prefix", length = 20)
    private String prefix;

    @Column(name = "_province_id")
    private Integer province;

    @Column(name = "_district_id")
    private Integer district;

}