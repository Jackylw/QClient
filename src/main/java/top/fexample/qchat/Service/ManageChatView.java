/**
 * @author Jacky Feng
 */
package top.fexample.qchat.Service;

import javafx.stage.Stage;
import top.fexample.qchat.controller.ChatController;

import java.util.HashMap;
import java.util.Map;

public class ManageChatView {
    public static final Map<String, Stage> openChatStages = new HashMap<>();
    public static final Map<String, ChatController> chatControllers = new HashMap<>();

    // 加入
    public static void addChatStage(String receiverId, Stage chatStage) {
        openChatStages.put(receiverId, chatStage);
    }

    public static void addChatController(String receiverId, ChatController chatController) {
        chatControllers.put(receiverId, chatController);
    }

    // 取出
    public static void removeChatStage(String receiverId) {
        Stage chatStage = openChatStages.remove(receiverId);
        if (chatStage != null) {
            chatStage.close();
        }
    }

    public static ChatController getChatController(String receiverId) {
        return chatControllers.get(receiverId);
    }

    public static Stage getChatStage(String receiverId) {
        return openChatStages.get(receiverId);
    }

}
