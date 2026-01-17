from pydantic import BaseModel, ConfigDict, Field, field_validator


class TaskCreateDto(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    projectId: int = Field(..., description="项目ID不能为空")
    columnId: int = Field(..., description="看板列ID不能为空")
    title: str = Field(
        ..., description="任务标题不能为空", max_length=200, min_length=1
    )
    priority: int | None = Field(default=1, ge=1, description="任务优先级")

    @field_validator("title")
    @classmethod
    def validate_title(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("任务标题不能为空")
        return cleaned
