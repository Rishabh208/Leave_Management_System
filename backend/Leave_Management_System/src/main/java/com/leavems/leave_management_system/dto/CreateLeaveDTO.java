package com.leavems.leave_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLeaveDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}
