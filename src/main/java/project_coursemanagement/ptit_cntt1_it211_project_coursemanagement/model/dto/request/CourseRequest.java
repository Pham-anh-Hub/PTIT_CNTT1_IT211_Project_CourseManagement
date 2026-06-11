package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseRequest {
    private String courseCode;
    private String courseName;
    private int credits;
    private String lecturerEmail;
    private String lecturerPhone;
}
