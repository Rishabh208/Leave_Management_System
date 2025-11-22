package com.leavems.leave_management_system.dto;

import com.leavems.leave_management_system.model.LeaveRequest;
import com.leavems.leave_management_system.model.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private String instructorComment;
    private LocalDateTime requestedAt;

    public static LeaveDTO fromEntity(LeaveRequest e) {
        return LeaveDTO.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .studentName(e.getStudent().getUsername())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .reason(e.getReason())
                .status(e.getStatus())
                .instructorComment(e.getInstructorComment())
                .requestedAt(e.getRequestedAt())
                .build();
    }
}
