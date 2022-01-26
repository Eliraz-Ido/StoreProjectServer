package com.dev.utils;

import com.dev.objects.SaleObject;
import com.dev.objects.UserObject;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessagesHandler extends TextWebSocketHandler {

    private static List<WebSocketSession> sessionList = new CopyOnWriteArrayList<>();
    private static Map<String, WebSocketSession> sessionMap = new HashMap<>();

    @PostConstruct
    public void init () {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        Map<String, String> map = Utils.splitQuery(session.getUri().getQuery());
        sessionMap.put(map.get("token"), session);
        sessionList.add(session);
        System.out.println("afterConnectionEstablished");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        System.out.println("handleTextMessage");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session);
        sessionMap.values().remove(session);
        System.out.println("afterConnectionClosed");
    }

    public void sendStartSaleNotifications(Set<UserObject> users, SaleObject sale) throws IOException {
        for (UserObject user : users) {
            WebSocketSession session = sessionMap.get(user.getToken());
            if (session != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("notification",
                            "A New Sale Has Started In " + sale.getShop().getShopName() + "! \n " +
                                    sale.getDescription());
                    session.sendMessage(new TextMessage(jsonObject.toString()));

            }
        }
    }

    public void sendEndSaleNotifications(Set<UserObject> users, SaleObject sale) throws IOException {
        for (UserObject user : users) {
            WebSocketSession session = sessionMap.get(user.getToken());
            if (session != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("notification",
                        "Hurry Up! The Sale In " + sale.getShop().getShopName() + " Is About To End! \n " +
                                sale.getDescription());
                session.sendMessage(new TextMessage(jsonObject.toString()));

            }
        }
    }


}