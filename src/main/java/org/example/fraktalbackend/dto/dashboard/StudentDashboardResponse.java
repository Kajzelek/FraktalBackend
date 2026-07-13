package org.example.fraktalbackend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.dto.user.UserProfileResponse;

import java.util.List;

@Data
@AllArgsConstructor
public class StudentDashboardResponse {
    private UserProfileResponse user;
    private List<DashboardCourseResponse> courses;
}
