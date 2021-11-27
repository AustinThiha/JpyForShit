package chem.kth.spyforlove.model;

import java.util.Date;

import chem.kth.spyforlove.CallType;


public class PhoneCall {
    private int id;
    private String phone;
    private Date start_date;
    private Date end_date;
    private CallType callType;

    public PhoneCall(int id, String phone, Date start_date, Date end_date, CallType callType) {
        this.id = id;
        this.phone = phone;
        this.start_date = start_date;
        this.end_date = end_date;
        this.callType = callType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    @Override
    public String toString() {
        return "PhoneCall{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", callType=" + callType +
                '}';
    }
}
