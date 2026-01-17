package com.example.ai_kanban.domain.dto.projectdto;

import com.example.ai_kanban.common.interfaces.UserAttachable;
import com.example.ai_kanban.domain.dto.columndto.BoardColumnDetailsVo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailsVo implements UserAttachable {
    private Long id;
    private String name;
    private Long ownerId;

    private String ownerName;
    private String ownerAvatar;
    private List<BoardColumnDetailsVo> columns;
    private List<ProjectMemberDetailVo> members;

    @Override
    public Long getUserId() {
        return ownerId;
    }

    @Override
    public void setNickname(String nickname) {
        ownerName=nickname;
    }

    @Override
    public void setAvatar(String avatar) {
        ownerAvatar=avatar;
    }
}
