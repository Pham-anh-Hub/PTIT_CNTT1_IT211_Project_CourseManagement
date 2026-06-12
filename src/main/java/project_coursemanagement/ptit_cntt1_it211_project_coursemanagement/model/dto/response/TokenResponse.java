package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
}