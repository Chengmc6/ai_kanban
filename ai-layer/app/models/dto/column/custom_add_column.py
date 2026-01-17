from pydantic import BaseModel, ConfigDict, Field, field_validator


class CustomAddColumn(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    projectId: int = Field(
        ...,
        description="项目ID不能为空",
        ge=1,  # 项目ID >= 1
    )

    name: str = Field(
        ...,
        description="看板列名不能为空",
        min_length=1,
        max_length=50,
    )

    @field_validator("name")
    @classmethod
    def validate_name(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("看板列名不能为空")
        return cleaned

    @field_validator("projectId")
    @classmethod
    def validate_project_id(cls, v: int) -> int:
        if v < 1:
            raise ValueError("项目ID必须大于等于1")
        return v
