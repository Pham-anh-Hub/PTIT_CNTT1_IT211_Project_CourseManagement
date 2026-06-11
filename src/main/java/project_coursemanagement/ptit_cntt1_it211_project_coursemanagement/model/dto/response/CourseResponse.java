package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private int credits;
    private String lecturerFullName;
    private String lecturerEmail;
    private String lecturerPhone;
}
