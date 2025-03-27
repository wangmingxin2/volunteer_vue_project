package com.hu.volunteer.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.hu.volunteer.pojo.Message;
import com.hu.volunteer.service.IMessageService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 通知消息表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */

@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    /**
     * 创建消息
     *
     * @param message 消息信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createMessage(@RequestBody Message message) {
        if (message == null) {
            return Result.validateFailed("消息数据不能为空");
        }
        boolean isSaved = messageService.save(message);
        if (isSaved) {
            return Result.success("消息创建成功");
        } else {
            return Result.error("消息创建失败");
        }
    }

    /**
     * 根据ID获取消息
     *
     * @param id 消息ID
     * @return 消息信息
     */
    @GetMapping("/{id}")
    public Result<Message> getMessageById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("消息ID无效");
        }

        Message message = messageService.getById(id);
        if (message != null) {
            return Result.success(message);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的消息");
        }
    }

    /**
     * 获取所有消息
     *
     * @return 消息列表
     */
    @GetMapping
    public Result<List<Message>> getAllMessages() {
        List<Message> messages = messageService.list();
        return Result.success(messages);
    }

    /**
     * 更新消息信息
     *
     * @param message 更新的消息信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Message> updateMessage(@RequestBody Message message) {
        if (message == null) {
            return Result.validateFailed("消息数据不能为空");
        }
        boolean isUpdated = messageService.updateById(message);
        if (isUpdated) {
            // 返回更新后的消息信息
            return Result.success(messageService.getById(message.getNotificationId()));
        } else {
            return Result.error("更新消息信息失败");
        }
    }

    /**
     * 删除消息
     *
     * @param id 消息ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteMessage(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("消息ID无效");
        }

        // 检查消息是否存在
        Message existingMessage = messageService.getById(id);
        if (existingMessage == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的消息");
        }

        boolean isRemoved = messageService.removeById(id);
        if (isRemoved) {
            return Result.success("消息删除成功");
        } else {
            return Result.error("消息删除失败");
        }
    }
}