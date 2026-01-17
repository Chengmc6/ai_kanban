from pydantic import BaseModel, ConfigDict, Field, field_validator


class ProjectCreateDto(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    projectName: str = Field(..., min_length=1, max_length=100, description="项目名")

    @field_validator("projectName")
    @classmethod
    def validate_name(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("项目名不能为空")
        return cleaned
