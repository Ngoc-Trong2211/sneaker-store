package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.User.*;
import com.example.sneaker_store.model.RoleEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.dto.response.user.CreateUserResponse;
import com.example.sneaker_store.dto.response.user.GetUserByIdResponse;
import com.example.sneaker_store.dto.response.user.GetUserResponse;
import com.example.sneaker_store.dto.response.user.UpdateUserResponse;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.RoleService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.specification.UserSpecification;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import com.example.sneaker_store.util.exception.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final RoleService roleService;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    @Override
    @PreAuthorize("hasAuthority('USER_CREATE') or hasAuthority('ADMIN')")
    public CreateUserResponse createUser(CreateUserRequest req) throws Exception {
        UserEntity user = new UserEntity();
        if(!validate(req.getEmail())){
            throw new EmailInvalidException("Định dạng email không hợp lệ");
        }
        if(this.userRepository.existsByEmail(req.getEmail())){
            throw new EmailExistsAlreadyException("Email đã tồn tại");
        }
        if (this.userRepository.existsByPhone(req.getPhone())){
            throw new PhoneExistsAlreadyException("Số điện thoại đã tồn tại");
        }
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        RoleEntity role = this.roleService.findById(req.getRoleId());
        if (role==null || !role.isActive()) throw new IdInvalidException("Vai trò không tồn tại");
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        this.userRepository.save(user);
        return this.modelMapper.map(user, CreateUserResponse.class);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN')")
    public GetUserResponse getUser(Pageable pageable, SpecificationUserRequest req) {
        Specification<UserEntity> spec = UserSpecification.specUser(req);
        Page<UserEntity> pageUser = this.userRepository.findAll(spec, pageable);

        GetUserResponse res = new GetUserResponse();
        GetUserResponse.DataPage resPage = this.modelMapper.map(pageUser, GetUserResponse.DataPage.class);
        res.setPage(resPage);
        List<GetUserResponse.User> users = pageUser.getContent().stream()
                .map(user -> {
                    GetUserResponse.User resUser = new GetUserResponse.User();
                    resUser.setId(user.getId());
                    resUser.setName(user.getName());
                    resUser.setEmail(user.getEmail());
                    resUser.setPhone(user.getPhone());
                    resUser.setUpdatedAt(user.getUpdatedAt());
                    resUser.setUpdatedBy(user.getUpdatedBy());
                    resUser.setCreatedAt(user.getCreatedAt());
                    resUser.setCreatedBy(user.getCreatedBy());
                    resUser.setStatus(user.getStatus().toString());
                    resUser.setRole(user.getRole().getName());
                    return resUser;
                }).toList();
        res.setUsers(users);

        return res;
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE') or hasAuthority('ADMIN')")
    public UpdateUserResponse updateUser(UpdateUserRequest req) {
        Optional<UserEntity> user = this.userRepository.findById(req.getId());
        if (this.userRepository.existsByPhoneAndIdNot(req.getPhone(), req.getId())){
            throw new PhoneExistsAlreadyException("Số điện thoại đã tồn tại");
        }
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            currentUser.setName(req.getName());
            currentUser.setPhone(req.getPhone());
            RoleEntity role = this.roleService.findById(req.getRoleId());
            if (role==null || !role.isActive()) throw new IdInvalidException("Vai trò không tồn tại");
            currentUser.setRole(role);
            this.userRepository.save(currentUser);
            return this.modelMapper.map(currentUser, UpdateUserResponse.class);
        }
        else{
            throw new IdInvalidException("ID người dùng không hợp lệ");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE_INFO')")
    public UpdateUserResponse updateUserInfo(UpdateInfoUserRequest req) {
        Optional<UserEntity> user = this.userRepository.findById(req.getId());
        if (this.userRepository.existsByPhoneAndIdNot(req.getPhone(), req.getId())){
            throw new PhoneExistsAlreadyException("Số điện thoại đã tồn tại");
        }
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            currentUser.setName(req.getName());
            currentUser.setPhone(req.getPhone());
            this.userRepository.save(currentUser);
            return this.modelMapper.map(currentUser, UpdateUserResponse.class);
        }
        else{
            throw new IdInvalidException("ID người dùng không hợp lệ");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE_STATUS') or hasAuthority('ADMIN')")
    public GetUserResponse.User updateStatus(String id, String status) {
        Optional<UserEntity> user = this.userRepository.findById(id);
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            currentUser.setStatus(UserStatus.valueOf(status));
            this.userRepository.save(currentUser);
            return this.modelMapper.map(currentUser, GetUserResponse.User.class);
        }
        else{
            throw new IdInvalidException("ID người dùng không hợp lệ");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('USER_DELETE') or hasAuthority('ADMIN')")
    public void disableUser(String id) {
        Optional<UserEntity> user = this.userRepository.findById(id);
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            currentUser.setStatus(UserStatus.DELETED);
            this.userRepository.save(currentUser);
        }
        else{
            throw new IdInvalidException("ID người dùng không hợp lệ");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void handleChangePassword(ChangePasswordRequest req) {
        String email = AuthServiceImpl.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng đang đăng nhập"));
        Optional<UserEntity> user = this.userRepository.findByEmail(email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            if (!encoder.matches(req.getCurrentPassword(), currentUser.getPassword()) ||
                    encoder.matches(req.getNewPassword(), currentUser.getPassword())){
                throw new ChangePasswordException(
                        "Mật khẩu hiện tại không đúng hoặc mật khẩu mới trùng với mật khẩu cũ");
            }
            else{
                if (req.getNewPassword().equals(req.getConfirmPassword())){
                    currentUser.setPassword(this.passwordEncoder.encode(req.getNewPassword()));
                    this.userRepository.save(currentUser);
                }
                else{
                    throw new PasswordMismatchException("Mật khẩu xác nhận không khớp");
                }
            }
        }
        else{
            throw new EmailInvalidException("Email không hợp lệ");
        }
    }

    @Override
    public void handleChangePasswordLogin(ChangePasswordRequest req, String email) {
        Optional<UserEntity> user = this.userRepository.findByEmail(email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user.isPresent()){
            UserEntity currentUser = user.get();
            if (!encoder.matches(req.getCurrentPassword(), currentUser.getPassword()) ||
                    encoder.matches(req.getNewPassword(), currentUser.getPassword())){
                throw new ChangePasswordException(
                        "Mật khẩu hiện tại không đúng hoặc mật khẩu mới trùng với mật khẩu cũ");
            }
            else{
                if (req.getNewPassword().equals(req.getConfirmPassword())){
                    currentUser.setPassword(this.passwordEncoder.encode(req.getNewPassword()));
                    this.userRepository.save(currentUser);
                }
                else{
                    throw new PasswordMismatchException("Mật khẩu xác nhận không khớp");
                }
            }
        }
        else{
            throw new EmailInvalidException("Email không hợp lệ");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ_DETAIL')")
    public GetUserByIdResponse getUserById(String id) {
        Optional<UserEntity> user = this.userRepository.findById(id);
        if (user.isPresent()){
            GetUserByIdResponse res = this.modelMapper.map(user.get(), GetUserByIdResponse.class);
            RoleEntity role = roleService.findById(user.get().getRole().getId());
            res.setRole(role.getName());
            return res;
        }
        else{
            throw new IdInvalidException("ID người dùng không hợp lệ");
        }
    }

    @Override
    public UserEntity findByEmail(String email) {
        Optional<UserEntity> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) return user.get();
        else throw new EmailInvalidException("Email không hợp lệ");
    }

    @Override
    public void updateRefreshToken(String refresh, UserEntity user) {
        user.setRefreshToken(refresh);
        this.userRepository.save(user);
    }

    @Override
    public UserEntity findByRefreshTokenAndEmail(String refresh, String email) {
        Optional<UserEntity> user = this.userRepository.findByRefreshTokenAndEmail(refresh, email);
        if (user.isPresent()) return user.get();
        else throw new EmailInvalidException("Email hoặc refresh token không hợp lệ");
    }
}
