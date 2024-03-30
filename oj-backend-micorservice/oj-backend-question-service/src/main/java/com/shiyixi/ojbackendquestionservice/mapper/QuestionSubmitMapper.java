package com.shiyixi.ojbackendquestionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.cache.annotation.Cacheable;

/**
* @author 33827
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2024-03-02 11:51:47
* @Entity generator.domain.QuestionSubmit
*/
@CacheNamespace
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {

}




