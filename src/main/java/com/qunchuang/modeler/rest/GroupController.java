package com.qunchuang.modeler.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @GetMapping("/{groupId}")
    public List<User> findUsersByGroupId(@PathVariable String groupId) {
        List<User> users = new ArrayList<>();
        users.add(new User("张三"));
        users.add(new User("李四"));

        return users;
    }
}
