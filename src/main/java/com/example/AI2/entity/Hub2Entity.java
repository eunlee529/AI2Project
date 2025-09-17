package com.example.AI2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "hubdata2")
public class Hub2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NUM") // <-- 여기를 꼭 지정!
    private Long num;
    @Column
    int qa_id;
    @Column
    int domain;
    @Column
    int q_type;
    @Lob
    @Column
    String question;
    @Lob
    @Column
    String answer;
}
