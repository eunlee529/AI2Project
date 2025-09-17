package com.example.AI2.dto;

import com.example.AI2.entity.IrisEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IrisDTO {
    long num;
    double loss;
    double accuracy;
    int epochs;

    public IrisEntity entity(){
        return new IrisEntity(null, loss, accuracy, epochs);
    }
}
