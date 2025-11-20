package zw.powertel.contracts.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsQueueItem {
    private String phone;
    private String message;
}
