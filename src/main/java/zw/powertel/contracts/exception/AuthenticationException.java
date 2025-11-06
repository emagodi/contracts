package zw.powertel.contracts.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthenticationException extends RuntimeException{

    private final String msg;
}
