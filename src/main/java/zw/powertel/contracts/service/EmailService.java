package zw.powertel.contracts.service;

import zw.powertel.contracts.payload.request.MailBody;

public interface EmailService {
    public void sendSimpleMessage(MailBody mailBody);
}
