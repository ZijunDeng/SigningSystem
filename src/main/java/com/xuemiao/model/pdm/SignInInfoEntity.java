package com.xuemiao.model.pdm;

import com.xuemiao.model.pdm.primaryKey.StudentIdAndOperDateKey;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by root on 16-10-25.
 */
@Entity
@Table(name = "sign_in_info")
@IdClass(StudentIdAndOperDateKey.class)
public class SignInInfoEntity implements Serializable{
    @Id
    private Long studentId;
    @Id
    private Date operDate;
    @Column(name = "start_morning")
    private Timestamp startMorning;
    @Column(name = "end_morning")
    private Timestamp endMorning;
    @Column(name = "start_afternoon")
    private Timestamp startAfternoon;
    @Column(name = "end_afternoon")
    private Timestamp endAfternoon;
    @Column(name = "start_night")
    private Timestamp startNight;
    @Column(name = "end_night")
    private Timestamp endNight;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public Timestamp getStartMorning() {
        return startMorning;
    }

    public void setStartMorning(Timestamp startMorning) {
        this.startMorning = startMorning;
    }

    public Timestamp getEndMorning() {
        return endMorning;
    }

    public void setEndMorning(Timestamp endMorning) {
        this.endMorning = endMorning;
    }

    public Timestamp getStartAfternoon() {
        return startAfternoon;
    }

    public void setStartAfternoon(Timestamp startAfternoon) {
        this.startAfternoon = startAfternoon;
    }

    public Timestamp getEndAfternoon() {
        return endAfternoon;
    }

    public void setEndAfternoon(Timestamp endAfternoon) {
        this.endAfternoon = endAfternoon;
    }

    public Timestamp getStartNight() {
        return startNight;
    }

    public void setStartNight(Timestamp startNight) {
        this.startNight = startNight;
    }

    public Timestamp getEndNight() {
        return endNight;
    }

    public void setEndNight(Timestamp endNight) {
        this.endNight = endNight;
    }
}