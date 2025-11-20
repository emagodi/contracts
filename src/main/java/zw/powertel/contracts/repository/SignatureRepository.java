package zw.powertel.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.powertel.contracts.entities.Signature;
import java.util.Optional;

public interface SignatureRepository extends JpaRepository<Signature, Long> {
    Optional<Signature> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
