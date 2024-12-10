package vn.ifine.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.ifine.jobhunter.domain.User;
import vn.ifine.jobhunter.domain.response.ResCreateUserDTO;
import vn.ifine.jobhunter.domain.response.ResUpdateUserDTO;
import vn.ifine.jobhunter.domain.response.ResUserDTO;
import vn.ifine.jobhunter.domain.response.ResultPaginationDTO;
import vn.ifine.jobhunter.service.UserService;
import vn.ifine.jobhunter.util.annotation.ApiMessage;
import vn.ifine.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User reqUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(reqUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + reqUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashPassword = this.passwordEncoder.encode(reqUser.getPassword());
        reqUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch a user")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        // return ResponseEntity.ok(this.userService.fetchAllUser(pageable));
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User reqUser) throws IdInvalidException {
        User currentUser = this.userService.handleUpdateUser(reqUser);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + reqUser.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(currentUser));
    }
}
