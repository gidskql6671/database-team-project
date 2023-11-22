package knu.database.lms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostRequestDto {
    private String lectureCode;
    private String sectionCode;
    private int postId;
}
