package com.leavems.leave_management_system.service;

import com.leavems.leave_management_system.dto.CreateLeaveDTO;
import com.leavems.leave_management_system.model.LeaveRequest;
import com.leavems.leave_management_system.model.LeaveStatus;
import com.leavems.leave_management_system.model.Role;
import com.leavems.leave_management_system.model.User;
import com.leavems.leave_management_system.repository.LeaveRequestRepository;
import com.leavems.leave_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRequestRepository lrRepo;
    private final UserRepository userRepo;

    @Value("${app.max-leave-days:10}")
    private int maxLeaveDays;

    @Transactional
    public LeaveRequest createLeaveRequest(Long studentId, CreateLeaveDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ResponseStatusException(BAD_REQUEST, "End date must be after or equal to start date");
        }
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (days > maxLeaveDays) {
            throw new ResponseStatusException(BAD_REQUEST, "Max leave length is " + maxLeaveDays + " days");
        }
        User student = userRepo.findById(studentId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));
        // check overlapping pending/approved leaves
        List<LeaveRequest> overlaps = lrRepo.findOverlappingForStudent(studentId, List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED), dto.getStartDate(), dto.getEndDate());
        if (!overlaps.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Overlapping leave exists");
        }
        LeaveRequest lr = LeaveRequest.builder()
                .student(student)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        return lrRepo.save(lr);
    }

    @Transactional
    public LeaveRequest approveLeave(Long requestId, Long approverId, String comment) {
        var approver = userRepo.findById(approverId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Approver not found"));
        if (!(approver.getRole() == Role.INSTRUCTOR || approver.getRole() == Role.ADMIN)) {
            throw new ResponseStatusException(FORBIDDEN, "Not authorized to approve");
        }
        if (comment == null || comment.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Instructor comment required when approving/rejecting");
        }
        LeaveRequest lr = lrRepo.findById(requestId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Leave request not found"));
        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(BAD_REQUEST, "Only pending requests can be approved");
        }
        // optional: check overlaps again
        lr.setStatus(LeaveStatus.APPROVED);
        lr.setInstructorComment(comment);
        return lrRepo.save(lr);
    }

    @Transactional
    public LeaveRequest rejectLeave(Long requestId, Long approverId, String comment) {
        var approver = userRepo.findById(approverId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Approver not found"));
        if (!(approver.getRole() == Role.INSTRUCTOR || approver.getRole() == Role.ADMIN)) {
            throw new ResponseStatusException(FORBIDDEN, "Not authorized to reject");
        }
        if (comment == null || comment.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Instructor comment required when approving/rejecting");
        }
        LeaveRequest lr = lrRepo.findById(requestId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Leave request not found"));
        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(BAD_REQUEST, "Only pending requests can be rejected");
        }
        lr.setStatus(LeaveStatus.REJECTED);
        lr.setInstructorComment(comment);
        return lrRepo.save(lr);
    }

    public List<LeaveRequest> listByStudent(Long studentId) {
        return lrRepo.findByStudentId(studentId);
    }

    public List<LeaveRequest> listPending() {
        return lrRepo.findByStatus(LeaveStatus.PENDING);
    }

    public LeaveRequest getById(Long id) {
        return lrRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Leave request not found"));
    }
}
