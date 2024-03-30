package com.shiyixi.ojbackendquestionservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiyixi.ojbackendmodel.entity.Question;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.cache.annotation.Cacheable;

/**
* @author 33827
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-03-02 11:51:41
* @Entity generator.domain.Question
*/
@CacheNamespace
public interface QuestionMapper extends BaseMapper<Question> {

}




