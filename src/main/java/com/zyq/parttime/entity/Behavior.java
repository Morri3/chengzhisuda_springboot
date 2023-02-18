package com.zyq.parttime.entity;

import com.zyq.parttime.entity.Position;
import com.zyq.parttime.entity.Student;

import javax.persistence.*;

@Entity
@Table(name = "behavior")
public class Behavior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "b_id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stu_id", nullable = false)
    private Student stu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "p_id", nullable = false)
    private Position p;

    public Position getP() {
        return p;
    }

    public void setP(Position p) {
        this.p = p;
    }

    public Student getStu() {
        return stu;
    }

    public void setStu(Student stu) {
        this.stu = stu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}