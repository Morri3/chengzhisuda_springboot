package com.zyq.parttime.entity;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "employer")
@Proxy(lazy = false)//禁止懒加载，解决定时任务运行失败的bug
public class Employer {
    @Id
    @Column(name = "telephone", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "u_id", nullable = false)
    private Unit u;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "jno")
    private String jno;

    @Column(name = "gender")
    private int gender;

    @Column(name = "emails")
    private String emails;

    @Column(name = "emp_name")
    private String empName;

    @Column(name = "age")
    private int age;

    @Column(name = "reg_date")
    private Date regDate;

    @Column(name = "head")
    private String head;

    @Column(name = "emp_grade")
    private int empGrade;

    @OneToMany(mappedBy = "op")
    private Set<Parttimes> parttimes = new LinkedHashSet<>();

    public Set<Parttimes> getPositions() {
        return parttimes;
    }

    public void setPositions(Set<Parttimes> parttimes) {
        this.parttimes = parttimes;
    }

    public int getEmpGrade() {
        return empGrade;
    }

    public void setEmpGrade(int empGrade) {
        this.empGrade = empGrade;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getJno() {
        return jno;
    }

    public void setJno(String jno) {
        this.jno = jno;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Unit getU() {
        return u;
    }

    public void setU(Unit u) {
        this.u = u;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}