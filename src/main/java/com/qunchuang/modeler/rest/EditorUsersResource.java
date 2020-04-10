///* Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.qunchuang.modeler.rest;
//
//import org.flowable.idm.api.Group;
//import org.flowable.idm.api.IdmIdentityService;
//import org.flowable.idm.api.User;
//import org.flowable.ui.common.model.GroupRepresentation;
//import org.flowable.ui.common.model.ResultListDataRepresentation;
//import org.flowable.ui.common.model.UserRepresentation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/app")
//public class EditorUsersResource {
//
//    private final IdmIdentityService idmIdentityService;
//
//    public EditorUsersResource(IdmIdentityService idmIdentityService) {
//        this.idmIdentityService = idmIdentityService;
//    }
//
//
//    /**
//     * 查询指定的用户
//     */
//    @RequestMapping(value = "/rest/editor-users", method = RequestMethod.GET)
//    public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter) {
//        if (!StringUtils.isEmpty(filter)) {
//            filter = filter.trim();
//            String sql = "select * from ACT_ID_USER where ID_ like #{id} or LAST_ like #{name} limit 10";
//            filter = "%" + filter + "%";
//            List<User> matchingUsers = idmIdentityService.createNativeUserQuery().sql(sql).parameter("id", filter).parameter("name", filter).list();
//            List<UserRepresentation> userRepresentations = new ArrayList<>(matchingUsers.size());
//            for (User user : matchingUsers) {
//                userRepresentations.add(new UserRepresentation(user));
//            }
//            return new ResultListDataRepresentation(userRepresentations);
//        }
//        return null;
//    }
//
//
//    /**
//     * 查询特定的分组
//     */
//    @RequestMapping(value = "/rest/editor-groups", method = RequestMethod.GET)
//    public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter) {
//        if (!StringUtils.isEmpty(filter)) {
//            filter = filter.trim();
//            String sql = "select * from ACT_ID_GROUP where NAME_ like #{name} limit 10";
//            filter = "%" + filter + "%";
//            List<Group> groups = idmIdentityService.createNativeGroupQuery().sql(sql).parameter("name", filter).list();
//            List<GroupRepresentation> result = new ArrayList<>();
//            for (Group group : groups) {
//                result.add(new GroupRepresentation(group));
//            }
//            return new ResultListDataRepresentation(result);
//        }
//        return null;
//    }
//}
