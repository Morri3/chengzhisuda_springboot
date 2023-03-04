package com.zyq.parttime.entity;

import com.zyq.parttime.entity.Resumedetail;
import com.zyq.parttime.entity.Student;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "resumes")
public class Resumes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stu_id", nullable = false)
    private Student stu;

    @Column(name = "current_area")
    private String currentArea;

    @Column(name = "upload_time")
    private Date uploadTime;

    @Column(name = "exp")
    private String exp;

    @Column(name = "pic_url")
    private String picUrl;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "r_status")
    private String rStatus;

    @OneToMany(mappedBy = "r")
    private Set<Resumedetail> resumedetails = new LinkedHashSet<>();

    public Set<Resumedetail> getResumedetails() {
        return resumedetails;
    }

    public void setResumedetails(Set<Resumedetail> resumedetails) {
        this.resumedetails = resumedetails;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(String currentArea) {
        this.currentArea = currentArea;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public Student getStu() {
        return stu;
    }

    public void setStu(Student stu) {
        this.stu = stu;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getrStatus() {
        return rStatus;
    }

    public void setrStatus(String rStatus) {
        this.rStatus = rStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}