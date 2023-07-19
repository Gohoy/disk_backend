package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dao.TypeDao;
import org.example.entity.Type;
import org.example.service.TypeService;
import org.example.vo.FormatType;
import org.example.vo.TypeVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeDao, Type> implements TypeService {

    @Resource
    private TypeDao typeDao;

    @Override
    public Type getTypeById(int id) {
        return typeDao.selectById(id);
    }


    public List<FormatType> getTypes() {
        List<Type> typeList = typeDao.selectList(new QueryWrapper<Type>().eq("class_id", 3));
        List<FormatType> result = new ArrayList<>();
        for (Type type : typeList) {
            if (type.getValue() == null) {
                continue;
            }
            FormatType formatType = new FormatType();
            formatType.setId(type.getId());
            formatType.setLabel(type.getLabel());
            formatType.setChildren(getChildren(type.getChildrenIds()));
            formatType.setValue(type.getValue());
            result.add(formatType);
        }
        System.out.println(result);
        return result;
    }

    private List<FormatType> getChildren(String childrenIds) {
        if (childrenIds == null || childrenIds.isEmpty() || childrenIds.equals("-1")) {
            return null;
        }
        String[] ids = childrenIds.split(",");
        List<FormatType> children = new ArrayList<>();
        for (String id : ids) {
            Type type = typeDao.selectById(Integer.parseInt(id.trim()));
            if (type == null || type.getValue() == null) {
                continue;
            }
            FormatType formatType = new FormatType();
            formatType.setId(type.getId());
            formatType.setLabel(type.getLabel());
            formatType.setChildren(getChildren(type.getChildrenIds()));
            formatType.setValue(type.getValue());
            children.add(formatType);
        }
        return children.isEmpty() ? null : children;
    }


    @Override
    public List<Type> getChildren(int id) {
        Type type = typeDao.selectById(id);
        String children = type.getChildrenIds();
        String[] array = children.split(",");
        List<Type> result = new ArrayList<>();
        if (!array[0].equals("-1") && !array[0].isEmpty()) {
            for (String childrenId : array) {
//                System.out.println(childrenId);
                int cid = Integer.parseInt(childrenId.trim());
//                System.out.println(cid);
                if (getTypeById((cid)) != null) {
                    result.add(getTypeById(cid));
                }
            }

        }
        return result;
    }
//
//    @Override
//    public List<FormatType> getTypes() {
//        List<Type> typeList = typeDao.selectList(new QueryWrapper<Type>().eq("class_id", 3));
//        System.out.println(typeList);
//        System.out.println("getTypes");
//        List<FormatType> result = new ArrayList<>();
//        for (Type type : typeList) {
//            List<FormatType> formatTypes = formatTypes(type);
//            System.out.println("getTypes1");
//            if (formatTypes != null) {
//                result.addAll(formatTypes);
//            }
//        }
//        return result;
//    }
//    public List<FormatType> formatTypes(Type type) {
//
//
//        List<FormatType> formatTypes = new ArrayList<>(); // return data
//        List<FormatType> formatTypes1 = new ArrayList<>(); // children list
//
//        try {
//            FormatType formatType = new FormatType();
//            List<Type> children = getChildren(type.getId());// get children
//            if (type.getId() % 100 == 0) {
//                formatType.setId(type.getId());
//                formatType.setValue(type.getValue());
//                formatType.setLabel(type.getLabel());// first level new formatType
//                formatType.setLevel(type.getClassId());
//            }
//            if (children.size() > 0) { // has children
//
//
//                for (Type child : children) {
//                    FormatType formatType2 = new FormatType();
//                    formatType2.setId(child.getId());
//                    formatType2.setValue(child.getValue());
//                    formatType2.setLabel(child.getLabel());//child new formatType
//                    formatType2.setLevel(child.getClassId());
//                    if (getChildren(child.getId()) != null) {
//                        List<FormatType> formatTypes2 = formatTypes(child);
//                        formatType2.setChildren(formatTypes2);
//                    }
//                    if(formatType2.getChildren().size() ==0){
//                        formatType2.setChildren(null);
//                    }
//                    formatTypes1.add(formatType2);//add to children list
//                }
//                if (formatType.getValue() != null) {
//                    formatType.setChildren(formatTypes1);
//                }
//
//            }
//            if (formatType.getValue() != null) {
//                formatTypes.add(formatType);
//            } else {
//                formatTypes = formatTypes1;
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//
//        return formatTypes;
//    }


    @Override
    public int insertType(Type type) {
        int id = typeDao.insert(type);
        return id;
    }

    @Override
    public int deleteType(int id) {
        int delete = typeDao.deleteById(id);
        return delete;
    }

    @Override
    public int updateType(TypeVO type) {
        List<Type> children = type.getChildren();
        ArrayList<Integer> childrenIds = new ArrayList<>();
        for (Type child : children) {
            childrenIds.add(child.getId());
        }
        String ChildrenIds = childrenIds.toString().substring(1, childrenIds.toString().length() - 1);
        if (childrenIds.isEmpty()) {
            ChildrenIds = "-1";
        }
        type.setChildrenIds(ChildrenIds);
        int i = typeDao.updateById(type);
        return i;
    }
}
