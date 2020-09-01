package aaron.dev.mobileappws.service;

import aaron.dev.mobileappws.shared.dto.UserDto;
import aaron.dev.mobileappws.ui.model.response.OperationStatusModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(String id,UserDto user);
    void deleteUser(String id);
    List<UserDto> getUsers(int page,int limit);
}
