package zw.powertel.contracts.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDraftResponse {
    private Long id;
    private String title;
    private String author;
    private String version;
    private String status;
    private String fileUrl;
    private String summary;
    private Long requisitionId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
