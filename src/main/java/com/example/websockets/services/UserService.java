package com.example.websockets.services;

import com.example.websockets.models.*;
import com.example.websockets.models.interfaces.IResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService implements IUserService {
    private final static ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>();
    private static AtomicInteger id = new AtomicInteger();
    private ObjectMapper mapper;

    public UserService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public IResult addUser(TextMessage data) {
        try {
            UserDto userDto = mapper.readValue(new StringReader(data.getPayload()), UserDto.class);
            User user = new User();
            user.setLogin(userDto.getLogin());
            user.setId(id.incrementAndGet());
            user.setPassword(getDecodePassword(userDto.getPassword()));
            if (userNotUnique(user)) {
                return new ResultOperation(OperationType.add_user, false);
            }
            users.add(user);
            return new ResultOperation(OperationType.add_user, true);
        } catch (IOException e) {
            return new ResultOperation(OperationType.add_user, false);
        }
    }

    @Override
    public IResult getUserList() {
        List<User> userList = users.stream().toList();
        ResultUserList resultUserList = new ResultUserList();
        resultUserList.setData(userList);
        resultUserList.setOperation(OperationType.get_user_list.name());
        try {
            //fixme Маппер должен возвращать спиоск пользоватлей в следующем формате согласно тз ст. 2 get_user_list
            ///resultUserList.setJson(mapper.wri(userList));
            JsonNode jsonNode = mapper.valueToTree(resultUserList);
            resultUserList.setJson(mapper.writeValueAsString(jsonNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(resultUserList.getJson());
        return resultUserList;
    }

    @Override
    public IResult changeUserStatus(TextMessage data) {

        try {
            final UserIdDto userIdDto = mapper.readValue(new StringReader(data.getPayload()), UserIdDto.class);
            Optional<User> user = users.stream()
                    .filter(x -> x.getId().equals(userIdDto.getId()))
                    .findFirst();
            if (user.isPresent()) {
                user.get().inversStatus();
                return new ResultOperation(OperationType.change_user_status, true);
            } else {
                return new ResultOperation(OperationType.change_user_status, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultOperation(OperationType.change_user_status, false);
        }
    }

    public Boolean userNotUnique(User user) {
        return users.stream().anyMatch(x -> x.getLogin().equals(user.getLogin()));
    }

    @Override
    public IResult editUserPassword(TextMessage data) {
        return new ResultOperation(OperationType.edit_user_password, false);
    }

    public String getDecodePassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }
}
