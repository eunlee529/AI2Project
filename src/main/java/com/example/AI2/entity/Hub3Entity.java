package com.example.AI2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "hubdata4")
public class Hub3Entity {

    @Id
    @Column(name = "doc_id", length = 50)  // PK
    private String docId;

    @Column(name = "doc_class")
    private Long docClass;  // NUMBER(6,0)

    @Column(name = "casenames", length = 50)
    private String casenames;

    @Column(name = "normalized_court", length = 50)
    private String normalizedCourt;

    @Column(name = "casetype", length = 50)
    private String casetype;

    @Lob  // sentences 는 여전히 CLOB
    @Column(name = "sentences")
    private String sentences;

    @Column(name = "announce_date", length = 50)
    private String announceDate;
}
