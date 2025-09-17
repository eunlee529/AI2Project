package com.example.AI2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "iris0820")
@SequenceGenerator(name = "iris",
                    sequenceName = "iris0820_seq",
                    initialValue = 1000,
                    allocationSize = 1
)
public class IrisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "iris")
    Long num;
    @Column
    double loss;
    @Column
    double accuracy;
    @Column
    int epochs;
}
