package knu.database.lms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequestDto {
    private String lectureCode;
    private String sectionCode;
    private int postId;
    private String content;
}
