package com.leavems.leave_management_system.controller;

import com.leavems.leave_management_system.dto.CreateLeaveDTO;
import com.leavems.leave_management_system.dto.LeaveDTO;
import com.leavems.leave_management_system.model.LeaveRequest;
import com.leavems.leave_management_system.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    @PostMapping("/students/{studentId}/requests")
    public ResponseEntity<LeaveDTO> createRequest(@PathVariable Long studentId, @RequestBody CreateLeaveDTO dto) {
        LeaveRequest saved = leaveService.createLeaveRequest(studentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(LeaveDTO.fromEntity(saved));
    }

    @GetMapping("/students/{studentId}/requests")
    public ResponseEntity<?> listStudentRequests(@PathVariable Long studentId) {
        var list = leaveService.listByStudent(studentId).stream().map(LeaveDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<LeaveDTO> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(LeaveDTO.fromEntity(leaveService.getById(id)));
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<?> listPending() {
        var list = leaveService.listPending().stream().map(LeaveDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/requests/{id}/approve")
    public ResponseEntity<LeaveDTO> approve(@PathVariable Long id,
                                            @RequestParam Long approverId,
                                            @RequestParam String comment) {
        var updated = leaveService.approveLeave(id, approverId, comment);
        return ResponseEntity.ok(LeaveDTO.fromEntity(updated));
    }

    @PutMapping("/requests/{id}/reject")
    public ResponseEntity<LeaveDTO> reject(@PathVariable Long id,
                                           @RequestParam Long approverId,
                                           @RequestParam String comment) {
        var updated = leaveService.rejectLeave(id, approverId, comment);
        return ResponseEntity.ok(LeaveDTO.fromEntity(updated));
    }
}
