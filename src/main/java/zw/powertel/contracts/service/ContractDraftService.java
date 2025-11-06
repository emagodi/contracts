package zw.powertel.contracts.service;

import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.payload.response.ContractDraftResponse;

import java.util.List;

public interface ContractDraftService {
    ContractDraftResponse uploadDraftFile(Long requisitionId, MultipartFile file,
                                          String title, String author, String version, String summary);
    ContractDraftResponse getDraftByRequisition(Long requisitionId);
    List<ContractDraftResponse> getAllDrafts();
}
