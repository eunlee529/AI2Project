package com.example.AI2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "hubdata")
public class HubEntity {
    @Id
    @Column
    String c_id;
    @Column
    int domain;
    @Column
    int source;
    @Column
    String source_spec;
    @Column
    String creation_year;
    @Lob
    @Column
    String content;
}
