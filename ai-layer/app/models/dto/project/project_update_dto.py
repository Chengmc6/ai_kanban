from pydantic import BaseModel, Field


class ProjectUpdateDto(BaseModel):
    projectId: int = Field(..., description="项目ID")
    name: str | None = Field(None, min_length=1, description="新项目名")
    newUserId: int | None = Field(None, ge=1, description="被转让者ID")
