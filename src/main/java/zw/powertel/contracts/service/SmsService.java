package zw.powertel.contracts.service;

public interface SmsService {
    public boolean sendSms(String phone, String message);
}
