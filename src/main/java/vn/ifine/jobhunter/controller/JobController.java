package vn.ifine.jobhunter.controller;

import java.util.Optional;

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
import vn.ifine.jobhunter.domain.Job;
import vn.ifine.jobhunter.domain.response.ResultPaginationDTO;
import vn.ifine.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.ifine.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.ifine.jobhunter.service.JobService;
import vn.ifine.jobhunter.util.annotation.ApiMessage;
import vn.ifine.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a new job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        // check job có tồn tại
        Optional<Job> jobOptional = this.jobService.fetchJobById(job.getId());
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check job có tồn tại
        Optional<Job> jobOptional = this.jobService.fetchJobById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs")
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(this.jobService.fetchAll(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Fetch a job")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        // check job có tồn tại
        Optional<Job> jobOptional = this.jobService.fetchJobById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok(jobOptional.get());
    }

}
