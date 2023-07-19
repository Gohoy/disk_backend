package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.Type;
import org.example.vo.FormatType;
import org.example.vo.TypeVO;

import java.util.List;


public interface TypeService extends IService<Type> {
    public Type getTypeById(int id);
    public List<Type> getChildren(int id);
    public List<FormatType> getTypes();
    public int insertType(Type type);
    public int deleteType(int id);
    public int updateType(TypeVO type);
}
