package com.qunchuang.modeler.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{userId}")
    public User getOne(@PathVariable String userId) {
        return new User("张三");
    }

    /**
     * 查询人员并分页
     */
    @GetMapping("/page")
    public PageImpl<User> users(@RequestParam(value = "organUnitIds", required = false) List<String> organUnitIds,
                                Role role,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "0") int size,
                                String search) {

        System.out.println(organUnitIds);

        List<User> users = new ArrayList<>();
        users.add(new User("张三"));
        users.add(new User("李四"));
        users.add(new User("王五"));

        return new PageImpl<>(users);
    }


    /**
     * 根据机构和角色查询人员
     */
    @GetMapping("/by-organ-unit")
    public List<User> findByOrganUnit(String organUnitId, Role role) {
        List<User> users = new ArrayList<>();
        users.add(new User("张三"));
        users.add(new User("李四"));
        users.add(new User("王五"));

        return users;
    }

    /**
     * 查询人员并设置大小
     */
    @GetMapping
    public List<User> users(String search, int size) {
        List<User> users = new ArrayList<>();
        users.add(new User("张三"));
        users.add(new User("李四"));
        users.add(new User("王五"));

        return users;
    }
}


@Getter
@Setter
class User {
    private String id = UUID.randomUUID().toString();

    private String name = "";

    private OrganUnit organUnit = new OrganUnit("0", "温州市局", LevelEnum.MUNICIPAL, "");

    private Role role = Role.LEADER;

    public User(String name) {
        this.name = name;
    }
}


enum Role {
    //警察
    POLICE,

    //管理员
    ADMIN,

    //领导
    LEADER
}