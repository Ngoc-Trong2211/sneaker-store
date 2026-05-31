package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.User.*;
import com.example.sneaker_store.dto.response.user.CreateUserResponse;
import com.example.sneaker_store.dto.response.user.GetUserByIdResponse;
import com.example.sneaker_store.dto.response.user.GetUserResponse;
import com.example.sneaker_store.dto.response.user.UpdateUserResponse;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.ApiMessage;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@RequestMapping("/user/v1")
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    @ApiMessage(message = "Create new user success")
    @Operation(summary = "Create new user", description = "Create new user in system")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest req) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(req));
    }

    @GetMapping("/users")
    @Operation(summary = "Get user", description = "Lấy danh sách người dùng trong hệ thống")
    @ApiMessage(message = "Get user")
    public ResponseEntity<GetUserResponse> getUser(SpecificationUserRequest req,  @ParameterObject Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUser(pageable, req));
    }

    @GetMapping("/users/{id}")
    @ApiMessage(message = "Lay nguoi dung theo id")
    @Operation(summary = "Get user by id", description = "Lấy danh sách người dùng theo id trong hệ thống")
    public ResponseEntity<GetUserByIdResponse> getUserById(@PathVariable("id") String id) throws IdInvalidException {
        log.info("Tim nguoi dung co id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUserById(id));
    }

    @PutMapping("/users")
    @Operation(summary = "Update user", description = "Cập nhật người dùng đã chọn trong hệ thống")
    @ApiMessage(message = "Update user")
    public ResponseEntity<UpdateUserResponse> updateUser(@Valid @RequestBody UpdateUserRequest req){
        log.info("Update user");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.userService.updateUser(req));
    }

    @PutMapping("/users/info")
    @Operation(summary = "Update user", description = "Cập nhật người dùng đã chọn trong hệ thống")
    @ApiMessage(message = "Update user")
    public ResponseEntity<UpdateUserResponse> updateUser(@Valid @RequestBody UpdateInfoUserRequest req){
        log.info("Update user");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.userService.updateUserInfo(req));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Update status", description = "Cập nhật trạng thái người dùng trong hệ thống")
    @ApiMessage(message = "Update status")
    public ResponseEntity<GetUserResponse.User> disableUser(@PathVariable("id") String id,
                                                            @RequestParam String status) {
        log.info("Update status user");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.userService.updateStatus(id, status));
    }

    @PatchMapping("/users/{id}")
    @Operation(summary = "Disable user", description = "Vô hiệu hóa người dùng trong hệ thống")
    @ApiMessage(message = "Disable user")
    public ResponseEntity<String> disableUser(@PathVariable("id") String id) {
        log.info("Disable user");
        this.userService.disableUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete success");
    }

    @PostMapping("/users/change-password")
    @Operation(summary = "Change password", description = "Đổi mật khẩu người dùng trong hệ thống")
    @ApiMessage(message = "Change password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest req) {
        log.info("Change password");
        this.userService.handleChangePassword(req);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đã cập nhật mật khẩu mới");
    }

    @PostMapping("/users/change-password/login")
    @Operation(summary = "Change password", description = "Đổi mật khẩu người dùng trong hệ thống")
    @ApiMessage(message = "Change password")
    public ResponseEntity<String> changePasswordLogin(@RequestBody @Valid ChangePasswordRequest req, @RequestParam String email) {
        log.info("Change password");
        this.userService.handleChangePasswordLogin(req, email);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Đã cập nhật mật khẩu mới");
    }
}
