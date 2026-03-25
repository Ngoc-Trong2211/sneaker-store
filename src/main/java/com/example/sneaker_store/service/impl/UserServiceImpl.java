package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.CreateUserRequest;
import com.example.sneaker_store.model.request.SpecificationUserRequest;
import com.example.sneaker_store.model.request.UpdateUserRequest;
import com.example.sneaker_store.model.response.user.CreateUserResponse;
import com.example.sneaker_store.model.response.user.GetUserResponse;
import com.example.sneaker_store.model.response.user.UpdateUserResponse;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.service.specification.UserSpecification;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import com.example.sneaker_store.util.exception.User.EmailExistsAlreadyException;
import com.example.sneaker_store.util.exception.User.EmailInvalidException;
import com.example.sneaker_store.util.exception.User.IdInvalidException;
import com.example.sneaker_store.util.exception.User.PhoneExistsAlreadyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest req) throws Exception {
        UserEntity user = new UserEntity();
        if(!validate(req.getEmail())){
            throw new EmailInvalidException("Invalid email format!");
        }
        if(this.userRepository.existsByEmail(req.getEmail())){
            throw new EmailExistsAlreadyException("Email is exists");
        }
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        this.userRepository.save(user);
        return this.modelMapper.map(user, CreateUserResponse.class);
    }

    @Override
    public GetUserResponse getUser(Pageable pageable, SpecificationUserRequest req) {
        Specification<UserEntity> spec = UserSpecification.specUser(req);
        Page<UserEntity> pageUser = this.userRepository.findAll(spec, pageable);

        GetUserResponse res = new GetUserResponse();
        GetUserResponse.DataPage resPage = this.modelMapper.map(pageUser, GetUserResponse.DataPage.class);
        res.setPage(resPage);
        List<GetUserResponse.User> users = pageUser.getContent().stream()
                .map(user -> this.modelMapper.map(user, GetUserResponse.User.class)).toList();
        res.setUsers(users);

        return res;
    }

    @Override
    public UpdateUserResponse updateUser(UpdateUserRequest req) {
        Optional<UserEntity> user = this.userRepository.findById(req.getId());
        if (this.userRepository.existsByPhone(req.getPhone())){
            throw new PhoneExistsAlreadyException("Phone is already!");
        }
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            currentUser.setName(req.getName());
            currentUser.setPhone(req.getPhone());
            this.userRepository.save(currentUser);
            return this.modelMapper.map(currentUser, UpdateUserResponse.class);
        }
        else{
            throw new IdInvalidException("Id is invalid!");
        }
    }

    @Override
    public GetUserResponse.User updateStatus(Long id, UserStatus status) {
        Optional<UserEntity> user = this.userRepository.findById(id);
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            currentUser.setStatus(status);
            this.userRepository.save(currentUser);
            return this.modelMapper.map(currentUser, GetUserResponse.User.class);
        }
        else{
            throw new IdInvalidException("Id is invalid!");
        }
    }
}
