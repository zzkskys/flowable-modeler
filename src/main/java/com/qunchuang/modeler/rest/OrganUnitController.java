package com.qunchuang.modeler.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/organ-units")
public class OrganUnitController {

    @GetMapping
    public List<OrganUnit> organUnits() {
        ArrayList<OrganUnit> units = new ArrayList<>();
        units.add(new OrganUnit("0", "温州市局", LevelEnum.MUNICIPAL, ""));
        units.add(new OrganUnit("1", "鹿城分局", LevelEnum.DISTRICT, "0"));
        units.add(new OrganUnit("2", "瓯海分局", LevelEnum.MUNICIPAL, "0"));
        units.add(new OrganUnit("3", "特警桥派出所", LevelEnum.POLICE_STATION, "1"));
        units.add(new OrganUnit("4", "五马街派出所", LevelEnum.POLICE_STATION, "1"));
        return units;
    }

    @GetMapping("/tree")
    public OrganUnitTree tree() {
        OrganUnitTree top = new OrganUnitTree("0", "温州市局", LevelEnum.MUNICIPAL, "");
        OrganUnitTree luChengTree = new OrganUnitTree("1", "鹿城分局", LevelEnum.DISTRICT, "0");
        top.getLowers().add(luChengTree);
        top.getLowers().add(new OrganUnitTree(("2"), "瓯海分局", LevelEnum.DISTRICT, "0"));

        luChengTree.getLowers().add(new OrganUnitTree("3", "特警桥派出所", LevelEnum.POLICE_STATION, "1"));
        luChengTree.getLowers().add(new OrganUnitTree("4", "五马街派出所", LevelEnum.POLICE_STATION, "1"));

        return top;
    }


}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class OrganUnitTree {

    private String id;

    private String name;

    private LevelEnum level;

    private String upperId;

    private List<OrganUnitTree> lowers = new ArrayList<>();

    public OrganUnitTree(String id, String name, LevelEnum level, String upperId) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.upperId = upperId;
    }
}


@Getter
@Setter
@NoArgsConstructor
class OrganUnit {

    private String id;

    private String name;

    private LevelEnum level;

    private String upperId;

    public OrganUnit(String id, String name, LevelEnum level, String upperId) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.upperId = upperId;
    }
}

enum LevelEnum {
    //市级
    MUNICIPAL,

    // 区/县级
    DISTRICT,

    //派出所级
    POLICE_STATION
}