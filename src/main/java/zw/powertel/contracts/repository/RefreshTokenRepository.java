package zw.powertel.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.powertel.contracts.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

}
