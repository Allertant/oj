package com.shiyixi.ojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiyixi.ojbackendcommon.common.ErrorCode;
import com.shiyixi.ojbackendcommon.constant.CommonConstant;
import com.shiyixi.ojbackendcommon.exception.BusinessException;
import com.shiyixi.ojbackendcommon.utils.SqlUtils;
import com.shiyixi.ojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shiyixi.ojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shiyixi.ojbackendmodel.entity.Question;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import com.shiyixi.ojbackendmodel.entity.User;
import com.shiyixi.ojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.shiyixi.ojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.shiyixi.ojbackendmodel.vo.QuestionSubmitVO;
import com.shiyixi.ojbackendmodel.vo.UserVO;
import com.shiyixi.ojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.shiyixi.ojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.shiyixi.ojbackendquestionservice.service.QuestionService;
import com.shiyixi.ojbackendquestionservice.service.QuestionSubmitService;
import com.shiyixi.ojbackendserviceclient.service.JudgeFeignClient;
import com.shiyixi.ojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author shiyixi
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;



    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断编程语言是否正确
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不存在");
        }
        // 判断实体是否存在，根据类别获取实体
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 插入到数据库
        long userId = loginUser.getId();

        String code = questionSubmitAddRequest.getCode();

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }

        Long questionSubmitId = questionSubmit.getId();

        /*
         * 修改题目中的提交数 + 1
         * 涉及到多线程问题，因此需要加锁
         * 前面的内容不需要加锁，允许一定的容错，比如一份代码提交了多次，那么这多次提交也给他处理算了
         */
        questionService.addSubmitNum(questionId);

        // 发送消息
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));

        return questionSubmit.getId();

    }



    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        queryWrapper.eq(StringUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    /**
     * 对于单个提交记录转化为 VO 对象
     * @param questionSubmit 问题提交
     * @param loginUser 登录用户
     * @return 脱敏后的问题提交
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long userId = loginUser.getId();

        if (userId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)) {
            /*
             * 用户信息脱敏：设置代码为空
             */
            questionSubmitVO.setCode(null);
        }

        // 设置上用户信息
        Long submitVOUserId = questionSubmitVO.getUserId();
        User submitUser = userFeignClient.getById(submitVOUserId);
        UserVO submitUserVO = new UserVO();
        BeanUtils.copyProperties(submitUser, submitUserVO);
        questionSubmitVO.setUserVO(submitUserVO);

        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        // 数据
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(),
                questionSubmitPage.getSize(),
                questionSubmitPage.getTotal());

        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        // 对每条提交记录进行转换为 VO 对象
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}


