package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.common.interfaces.UserAttachable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskBaseVo implements UserAttachable {
    private Long id;
    private Long columnId;
    private String title;
    private Byte priority;
    private Long assigneeId;
    private Integer orderNum;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long projectId;
    private Long version;

    private String assigneeName;
    private String assigneeAvatar;

    @Override
    public Long getUserId() {
        return assigneeId;
    }

    @Override
    public void setNickname(String nickname) {
        assigneeName=nickname;
    }

    @Override
    public void setAvatar(String avatar) {
        assigneeAvatar=avatar;
    }
}
