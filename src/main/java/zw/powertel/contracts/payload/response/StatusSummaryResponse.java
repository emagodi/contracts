package zw.powertel.contracts.payload.response;

import lombok.*;
import zw.powertel.contracts.enums.RequisitionStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusSummaryResponse {
    private RequisitionStatus status;
    private Long count;
}
