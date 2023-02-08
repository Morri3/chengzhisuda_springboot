package com.zyq.parttime.entity;

import com.zyq.parttime.entity.Signup;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "mark")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "m_id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "s_id", nullable = false)
    private Signup s;

    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "pf")
    private int pf;

    @Column(name = "pl")
    private int pl;

    @Column(name = "we")
    private int we;

    @Column(name = "lt")
    private int lt;

    @Column(name = "pt")
    private int pt;

    @Column(name = "ods")
    private int ods;

    @Column(name = "dsps")
    private int dsps;

    @Column(name = "create_time")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getDsps() {
        return dsps;
    }

    public void setDsps(int dsps) {
        this.dsps = dsps;
    }

    public int getOds() {
        return ods;
    }

    public void setOds(int ods) {
        this.ods = ods;
    }

    public int getPt() {
        return pt;
    }

    public void setPt(int pt) {
        this.pt = pt;
    }

    public int getLt() {
        return lt;
    }

    public void setLt(int lt) {
        this.lt = lt;
    }

    public int getWe() {
        return we;
    }

    public void setWe(int we) {
        this.we = we;
    }

    public int getPl() {
        return pl;
    }

    public void setPl(int pl) {
        this.pl = pl;
    }

    public int getPf() {
        return pf;
    }

    public void setPf(int pf) {
        this.pf = pf;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public Signup getS() {
        return s;
    }

    public void setS(Signup s) {
        this.s = s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}