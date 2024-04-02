/**
 * @author Jacky Feng
 * @date 2024/4/2 20:50
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.common.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBuffer {
    public static ConcurrentHashMap<String, List<Message>> messageBuffer = new ConcurrentHashMap<>();

    public static void addBufferedMessage(String userId, Message message) {
        // 先从map中获取或初始化对应userId的message list
        List<Message> messages = messageBuffer.computeIfAbsent(userId, k -> new ArrayList<>());
        // 将新消息添加到该用户的message list中
        messages.add(message);
    }

    public static List<Message> getAndClearBufferedMessages(String userId) {
        // 获取该用户的所有消息
        List<Message> messages = messageBuffer.get(userId);
        // 移除该用户的消息列表（可选，取决于是否需要彻底清理缓存）
        messageBuffer.remove(userId);
        // 返回消息列表
        return messages == null ? new ArrayList<>() : new ArrayList<>(messages);
    }
}
