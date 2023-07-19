package org.example.controller.type;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.controller.file.ResultType;
import org.example.dao.TypeDao;
import org.example.entity.Type;
import org.example.service.TypeService;
import org.example.vo.FormatType;
import org.example.vo.TypeVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/type")
@Api(value = "typeController")
public class TypeController {
    public static RedisTemplate redisTemplate;
    @Resource
    private TypeDao typeDao;
    @Resource
    private TypeService typeService;


//    获取所有的type
    @ApiOperation("获取所有type")
    @Cacheable(value = {"type"})
    @GetMapping ("/getTypes")
    public ResultType getTypes(){
        List<FormatType> types = typeService.getTypes();
        return new ResultType<>().data(types).code(200).message("获取成功");
    }

@ApiOperation("根据id删除type")
    @CacheEvict(value = {"type"},allEntries=true)
    @PostMapping("/deleteType/{id}")
    public ResultType deleteType(@PathVariable("id") int id){
//        System.out.println(id);
        return new ResultType().data(typeService.deleteType(id)).code(200).message("删除成功");
    }
    @ApiOperation("增加type类型")
    @CacheEvict(value = {"type"},allEntries=true)
    @PostMapping("/insertType")
    public ResultType insertType(@RequestBody Type type){
        return new ResultType().data(typeService.insertType(type)).code(200).message("插入成功");
    }
    @ApiOperation("更新type")
    @CacheEvict(value = {"type"},allEntries=true)
    @PostMapping("/updateType")
    public ResultType updateType(@RequestBody TypeVO type){

        return new ResultType().data(typeService.updateType(type)).code(200).message("更新成功");
    }
}
