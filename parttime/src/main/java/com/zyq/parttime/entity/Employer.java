package com.zyq.parttime.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "employer")
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
    private long head;

    @Column(name = "emp_grade")
    private boolean empGrade;

    @OneToMany(mappedBy = "op")
    private Set<Position> positions = new LinkedHashSet<>();

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public boolean getEmpGrade() {
        return empGrade;
    }

    public void setEmpGrade(boolean empGrade) {
        this.empGrade = empGrade;
    }

    public long getHead() {
        return head;
    }

    public void setHead(long head) {
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