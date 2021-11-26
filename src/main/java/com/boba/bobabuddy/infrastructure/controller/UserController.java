package com.boba.bobabuddy.infrastructure.controller;

import com.boba.bobabuddy.core.entity.User;
import com.boba.bobabuddy.core.usecase.auth.GetAuthorities;
import com.boba.bobabuddy.core.usecase.user.port.ICreateUser;
import com.boba.bobabuddy.core.usecase.user.port.IFindUser;
import com.boba.bobabuddy.core.usecase.user.port.IRemoveUser;
import com.boba.bobabuddy.core.usecase.user.port.IUpdateUser;
import com.boba.bobabuddy.infrastructure.assembler.UserResourceAssembler;
import com.boba.bobabuddy.infrastructure.dto.RoleDto;
import com.boba.bobabuddy.infrastructure.dto.UserDto;
import com.boba.bobabuddy.infrastructure.dto.converter.DtoConverter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/***
 * REST controller for User related api calls
 */
@RestController
public class UserController {

    // Interfaces of User usecases and user DTO converter
    private final ICreateUser createUser;
    private final IFindUser findUser;
    private final IRemoveUser removeUser;
    private final IUpdateUser updateUser;
    private final UserResourceAssembler assembler;
    private final DtoConverter<User, UserDto> dtoConverter;


    @Autowired
    public UserController(ICreateUser createUser, IFindUser findUser, IRemoveUser removeUser, IUpdateUser updateUser, ModelMapper mapper, UserResourceAssembler assembler) {
        this.createUser = createUser;
        this.findUser = findUser;
        this.removeUser = removeUser;
        this.updateUser = updateUser;
        this.assembler = assembler;
        this.dtoConverter = new DtoConverter<>(mapper, User.class, UserDto.class);
    }

    /**
     * POST HTTP requests for creating User resource with user role
     *
     * @param createUserRequest Request class that contains data necessary to construct a User entity.
     * @return User resource that was created
     */
    @PostMapping(path = "/users")
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody UserDto createUserRequest) {
        createUserRequest.setRoles(List.of(new RoleDto("ROLE_USER")));
        UserDto userToPresent = dtoConverter.convertToDto(createUser.create(dtoConverter.convertToEntity(createUserRequest)));
        return ResponseEntity.created(linkTo(methodOn(UserController.class).findByEmail(userToPresent.getEmail())).toUri()).body(assembler.toModel(userToPresent));
    }

    /**
     * POST HTTP requests for creating User resource with any role
     *
     * @param createUserRequest Request class that contains data necessary to construct an User entity.
     * @return User resource that was created
     */
    @PostMapping(path = "/admin/users")
    public ResponseEntity<EntityModel<UserDto>> createUserAdmin(@RequestBody UserDto createUserRequest) {
        UserDto userToPresent = dtoConverter.convertToDto(createUser.create(dtoConverter.convertToEntity(createUserRequest)));
        return ResponseEntity.created(linkTo(methodOn(UserController.class).findByEmail(userToPresent.getEmail())).toUri()).body(assembler.toModel(userToPresent));
    }

    /**
     * GET requests for the User resource belonging to an email.
     *
     * @param email the email of the User
     * @return the User resource with matching email
     */
    @GetMapping(path = "/user/users/{email}")
    public ResponseEntity<EntityModel<UserDto>> findByEmail(@PathVariable String email) {
        String currentUser = GetAuthorities.getCurrentUser();
        if (currentUser.equals(email) || GetAuthorities.isAdmin()) {
            return ResponseEntity.ok(assembler.toModel(dtoConverter.convertToDto(findUser.findByEmail(email))));
        } else { // TODO: do something else
            return ResponseEntity.ok(assembler.toModel(new UserDto()));
        }
    }

    /**
     * GET requests for the User resources belonging to a name.
     *
     * @param name the email of the User
     * @return the list of User resources with names matching the requested name
     */
    @GetMapping(path = "/admin/users", params = "name")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> findByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(assembler.toCollectionModel(dtoConverter.convertToDtoCollection(findUser.findByName(name))));
    }

    /**
     * GET requests for all User resources in the database
     *
     * @return list of all User resources in the database
     */
    @GetMapping(path = "/admin/users")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> findAll() {
        return ResponseEntity.ok(assembler.toCollectionModel(dtoConverter.convertToDtoCollection(findUser.findAll())));
    }

    /**
     * DELETE requests to remove User by its email from the database.
     *
     * @param email the email of the User to be removed from the database
     * @return NO_CONTENT http status
     */
    @DeleteMapping(path = "/user/users/{email}")
    public ResponseEntity<?> removeUserByEmail(@PathVariable String email) {
        String currentUser = GetAuthorities.getCurrentUser();
        if (currentUser.equals(email) || GetAuthorities.isAdmin()) {
            removeUser.removeByEmail(email);
            return ResponseEntity.noContent().build();
        } else { //TODO: do something else
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * PUT request to update an existing User
     * TODO: make sure the request is ok (does not change email to existing/invalid, does not change role, etc.)
     * @param email     email of the User we want to update.
     * @param userPatch the same User with updated fields.
     * @return the User resource after the modification
     */
    @PutMapping(path = "/user/users/{email}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(@PathVariable String email, @RequestBody UserDto userPatch) {
        String currentUser = GetAuthorities.getCurrentUser();
        if (currentUser.equals(email) || GetAuthorities.isAdmin()) {
            return ResponseEntity.ok(assembler.toModel(dtoConverter.convertToDto(updateUser.updateUser(findUser.findByEmail(email), userPatch))));
        } else { // TODO: do something else
            return ResponseEntity.ok(assembler.toModel(new UserDto()));
        }
    }

    @GetMapping(path = "/admin/user/token")
    public String loginUser(@RequestBody UserDto userDto) throws FirebaseAuthException {
        FirebaseAuth admin = FirebaseAuth.getInstance();
        UserRecord user = admin.getUserByEmail(userDto.getEmail());
        return admin.createCustomToken(user.getUid());
    }
}
