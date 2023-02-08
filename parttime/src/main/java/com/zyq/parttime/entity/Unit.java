package com.zyq.parttime.entity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "unit")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id", nullable = false)
    private int id;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "descriptions")
    private String descriptions;

    @Column(name = "loc")
    private String loc;

    @Column(name = "job_nums")
    private int jobNums;

    @OneToMany(mappedBy = "u")
    private Set<Employer> employers = new LinkedHashSet<>();

    public Set<Employer> getEmployers() {
        return employers;
    }

    public void setEmployers(Set<Employer> employers) {
        this.employers = employers;
    }

    public int getJobNums() {
        return jobNums;
    }

    public void setJobNums(int jobNums) {
        this.jobNums = jobNums;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}