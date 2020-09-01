package aaron.dev.mobileappws.service.impl;

import aaron.dev.mobileappws.exceptions.UserServiceException;
import aaron.dev.mobileappws.io.repository.UserRepository;
import aaron.dev.mobileappws.io.entity.UserEntity;
import aaron.dev.mobileappws.service.UserService;
import aaron.dev.mobileappws.shared.Utils;
import aaron.dev.mobileappws.shared.dto.UserDto;
import aaron.dev.mobileappws.ui.model.response.ErrorMessages;
import aaron.dev.mobileappws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Record Already Exists");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails,returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);


        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email){
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String id) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        BeanUtils.copyProperties(userEntity,returnValue);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String id, UserDto user) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        userRepository.save(userEntity);

        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> usersDto = new ArrayList<>();

        if(page>0) page = page - 1;

        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        List<UserEntity> users = usersPage.getContent();

        for(UserEntity user: users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user,userDto);
            usersDto.add(userDto);
        }

        return usersDto;
    }


}
