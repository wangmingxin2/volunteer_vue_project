package com.hu.volunteer.service.impl;

import com.hu.volunteer.pojo.Message;
import com.hu.volunteer.mapper.MessageMapper;
import com.hu.volunteer.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通知消息表 服务实现类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
