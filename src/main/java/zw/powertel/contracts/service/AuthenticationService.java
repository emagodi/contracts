package zw.powertel.contracts.service;

import zw.powertel.contracts.entities.User;
import zw.powertel.contracts.payload.request.AuthenticationRequest;
import zw.powertel.contracts.payload.request.RegisterRequest;
import zw.powertel.contracts.payload.request.UserUpdateRequest;
import zw.powertel.contracts.payload.response.AuthenticationResponse;


public interface AuthenticationService {

    public AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);

    public User getUserById(Long id);

    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    public void changePassword(String email, String currentPassword, String newPassword);

    public String generateOtp();
}
