package zw.powertel.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.powertel.contracts.entities.SmsEntity;

import java.util.List;

public interface SmsRepository extends JpaRepository<SmsEntity, Long> {
    List<SmsEntity> findBySentFalse();
}
