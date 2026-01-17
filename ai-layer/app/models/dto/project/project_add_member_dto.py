from pydantic import BaseModel, Field, model_validator


class MemberItem(BaseModel):
    userId: int = Field(..., description="用户ID,不能为空")
    role: int | None = Field(None, ge=1, description="角色,可以为空")


class ProjectAddMemberDto(BaseModel):
    projectId: int = Field(..., description="项目ID,不能为空")
    members: list[MemberItem] = Field(..., description="成员列表不能为空")

    @model_validator(mode="after")
    def check_members_not_empty(self):
        if not self.members:
            raise ValueError("请选择用户")
        return self
