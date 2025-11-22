package com.leavems.leave_management_system.repository;

import com.leavems.leave_management_system.model.LeaveRequest;
import com.leavems.leave_management_system.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {
    List<LeaveRequest> findByStudentId(Long studentId);

    @Query("select lr from LeaveRequest lr where lr.student.id = :studentId and lr.status in :states and not (lr.endDate < :start or lr.startDate > :end)")
    List<LeaveRequest> findOverlappingForStudent(Long studentId, List<LeaveStatus> states, LocalDate start, LocalDate end);

    List<LeaveRequest> findByStatus(LeaveStatus status);
}
