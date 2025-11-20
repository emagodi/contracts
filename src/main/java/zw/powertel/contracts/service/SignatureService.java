package zw.powertel.contracts.service;

import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.entities.Signature;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SignatureService {

    Signature uploadSignature(String email, MultipartFile file) throws IOException;

    Signature updateSignature(String email, MultipartFile file) throws IOException;

    Optional<Signature> getSignatureByEmail(String email);

    Signature getSignatureById(Long id);

    List<Signature> getAllSignatures();

    void deleteSignature(Long id) throws IOException;

    void deleteSignatureByEmail(String email) throws IOException;
}
