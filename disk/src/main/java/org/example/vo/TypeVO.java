package org.example.vo;

import lombok.Data;
import org.example.entity.Type;

import java.util.List;

@Data
public class TypeVO extends Type {
    private List<Type> children;
}
