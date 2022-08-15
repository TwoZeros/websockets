package com.example.websockets.services;

import com.example.websockets.dto.ChangePasswordDto;
import com.example.websockets.dto.UserDto;
import com.example.websockets.dto.UserIdDto;
import com.example.websockets.exceptions.UserNotFoundException;
import com.example.websockets.models.*;
import com.example.websockets.models.interfaces.IResult;
import com.example.websockets.services.interfaces.ISimpleNumberGenerator;
import com.example.websockets.services.interfaces.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService implements IUserService {
    private final static int COUNT_AVAILABLE_OPERATION = 100;
    private final static int MAX_NUMBER_OF_OPERATION = 10000;
    private final static ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>();
    private final static ConcurrentHashMap<WebSocketSession, UserData> authUser = new ConcurrentHashMap<>();
    private static AtomicInteger id = new AtomicInteger();
    private final ObjectMapper mapper;
    private final ISimpleNumberGenerator simpleNumberGenerator;

    public UserService(ObjectMapper mapper, ISimpleNumberGenerator simpleNumberGenerator) {
        this.mapper = mapper;
        this.simpleNumberGenerator = simpleNumberGenerator;
    }

    @Override
    public IResult addUser(TextMessage data) {
        try {
            UserDto userDto = mapper.readValue(new StringReader(data.getPayload()), UserDto.class);
            User user = new User();
            user.setLogin(userDto.getLogin());
            user.setId(id.incrementAndGet());
            user.setActive(true);
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
            JsonNode jsonNode = mapper.valueToTree(resultUserList);
            resultUserList.setJson(mapper.writeValueAsString(jsonNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultUserList;
    }

    @Override
    public IResult authUser(TextMessage data, WebSocketSession session) {
        try {
            final UserDto userDto = mapper.readValue(new StringReader(data.getPayload()), UserDto.class);
            User user = users.stream()
                    .filter(x -> x.getLogin().equals(userDto.getLogin()))
                    .filter(User::isActive)
                    .filter(x -> x.getPassword().equals(
                            (getDecodePassword(userDto.getPassword())))
                    )
                    .findFirst().orElseThrow(UserNotFoundException::new);
            user.setLastAuthDate(new Date());
            var listOperation = simpleNumberGenerator.generateNumbers(MAX_NUMBER_OF_OPERATION,
                    COUNT_AVAILABLE_OPERATION);
            authUser.put(session, new UserData(user,listOperation));

            return new ResultOperation(OperationType.auth, true);
        } catch (IOException | UserNotFoundException e) {
            return new ResultOperation(OperationType.auth, false);
        }
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
        try {
            final ChangePasswordDto changePasswordDto = mapper.readValue(
                    new StringReader(data.getPayload()), ChangePasswordDto.class);
            User user = users.stream()
                    .filter(x -> x.getId().equals(changePasswordDto.getUserId()))
                    .filter(x -> x.getPassword().equals(
                            getDecodePassword(changePasswordDto.getOldPassword())))
                    .findFirst()
                    .orElseThrow(UserNotFoundException::new);

            user.setPassword(getDecodePassword(changePasswordDto.getNewPassword()));

            return new ResultOperation(OperationType.edit_user_password, true);

        } catch (IOException | UserNotFoundException e) {
            return new ResultOperation(OperationType.edit_user_password, false);
        }
    }

    public String getDecodePassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }
}
