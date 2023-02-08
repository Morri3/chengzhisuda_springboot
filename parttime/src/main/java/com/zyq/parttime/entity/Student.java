package com.zyq.parttime.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "telephone", nullable = false)
    private String id;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "sno")
    private String sno;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "gender")
    private int gender;

    @Column(name = "emails")
    private String emails;

    @Column(name = "stu_name")
    private String stuName;

    @Column(name = "age")
    private int age;

    @Column(name = "reg_date")
    private Date regDate;

    @Column(name = "entrance_date")
    private Date entranceDate;

    @Column(name = "graduation_date")
    private Date graduationDate;

    @Column(name = "head")
    private long head;

    @Column(name = "grade")
    private String grade;

    @OneToMany(mappedBy = "stu")
    private Set<Signup> signups = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stu")
    private Set<Behavior> behaviors = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stu")
    private Set<Intention> intentions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stu")
    private Set<Resumes> resumes = new LinkedHashSet<>();

    public Set<Resumes> getResumes() {
        return resumes;
    }

    public void setResumes(Set<Resumes> resumes) {
        this.resumes = resumes;
    }

    public Set<Intention> getIntentions() {
        return intentions;
    }

    public void setIntentions(Set<Intention> intentions) {
        this.intentions = intentions;
    }

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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public long getHead() {
        return head;
    }

    public void setHead(long head) {
        this.head = head;
    }

    public Date getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(Date graduationDate) {
        this.graduationDate = graduationDate;
    }

    public Date getEntranceDate() {
        return entranceDate;
    }

    public void setEntranceDate(Date entranceDate) {
        this.entranceDate = entranceDate;
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

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
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

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}