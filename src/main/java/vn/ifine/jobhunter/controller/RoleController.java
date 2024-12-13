package vn.ifine.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import vn.ifine.jobhunter.domain.Role;
import vn.ifine.jobhunter.domain.response.ResultPaginationDTO;
import vn.ifine.jobhunter.service.RoleService;
import vn.ifine.jobhunter.util.annotation.ApiMessage;
import vn.ifine.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException {
        // check name
        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(role.getId()) == null) {
            throw new IdInvalidException("Role với id = " + role.getId() + " không tồn tại");
        }
        // // check name
        // if (this.roleService.existByName(role.getName())) {
        // throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn
        // tại");
        // }
        // update
        return ResponseEntity.ok(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.getRoles(spec, pageable));
    }
}
