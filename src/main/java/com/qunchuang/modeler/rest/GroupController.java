package com.qunchuang.modeler.rest;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.model.GroupRepresentation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final IdmIdentityService idmIdentityService;

    public GroupController(IdmIdentityService idmIdentityService) {
        this.idmIdentityService = idmIdentityService;
    }

    /**
     * 查询某组下的用户
     */
    @GetMapping("/{groupId}/users")
    public List<User> findUsersByGroupId(@PathVariable String groupId) {
        List<User> users = new ArrayList<>();
        users.add(new User("张三"));
        users.add(new User("李四"));

        return users;
    }

    /**
     * 查询特定的分组
     */
    @GetMapping
    public List<GroupRepresentation> getGroups(String search,
                                               @RequestParam(defaultValue = "10") int size) {
        List<GroupRepresentation> result = new ArrayList<>();
        if (search == null || search.isEmpty()) {
            List<Group> groups = idmIdentityService
                    .createGroupQuery()
                    .listPage(0, size);

            for (Group group : groups) {
                result.add(new GroupRepresentation((group)));
            }
        } else {
            search = search.trim();
            String sql = "select * from ACT_ID_GROUP where NAME_ like #{name} limit 10";
            search = "%" + search + "%";
            List<Group> groups = idmIdentityService
                    .createNativeGroupQuery()
                    .sql(sql)
                    .parameter("name", search)
                    .list();
            for (Group group : groups) {
                result.add(new GroupRepresentation(group));
            }
        }
        return result;
    }
}
