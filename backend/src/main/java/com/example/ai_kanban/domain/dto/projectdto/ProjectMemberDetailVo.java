package com.example.ai_kanban.domain.dto.projectdto;

import com.example.ai_kanban.common.interfaces.UserAttachable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectMemberDetailVo implements UserAttachable {
    private Long userId;
    private Integer role;
    private LocalDateTime joinTime;

    private String nickname;
    private String avatar;
}
