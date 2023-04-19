package com.zyq.parttime.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "parttimes")
public class Parttimes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "op_id", nullable = false)
    private Employer op;

    @Column(name = "position_name")
    private String positionName;

    @Column(name = "category")
    private String category;

    @Column(name = "num")
    private int num;

    @Column(name = "salary")
    private String salary;

    @Column(name = "area")
    private String area;

    @Column(name = "exp")
    private String exp;

    @Column(name = "content")
    private String content;

    @Column(name = "requirement")
    private String requirement;

    @Column(name = "signup_ddl")
    private Date signupDdl;

    @Column(name = "slogan")
    private String slogan;

    @Column(name = "work_time")
    private String workTime;

    @Column(name = "settlement")
    private String settlement;

    @Column(name = "position_status")
    private String positionStatus;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @OneToMany(mappedBy = "p")
    private Set<Signup> signups = new LinkedHashSet<>();

    @OneToMany(mappedBy = "p")
    private Set<Behavior> behaviors = new LinkedHashSet<>();

    public Set<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(Set<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public Set<Signup> getSignups() {
        return signups;
    }

    public void setSignups(Set<Signup> signups) {
        this.signups = signups;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public Date getSignupDdl() {
        return signupDdl;
    }

    public void setSignupDdl(Date signupDdl) {
        this.signupDdl = signupDdl;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Employer getOp() {
        return op;
    }

    public void setOp(Employer op) {
        this.op = op;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}