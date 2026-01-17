from datetime import datetime, timedelta

from pydantic import BaseModel, ConfigDict, Field, field_validator

from app.core.config import settings


class TaskItemDto(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    title: str = Field(..., ge=1, description="任务标题不能为空")
    content: str | None = Field(None, ge=1, description="任务描述")
    priority: int | None = Field(default=1, ge=1, le=3, description="优先级")
    deadline: datetime = Field(
        default_factory=lambda: datetime.now() + timedelta(days=3),
        description="截至时间",
    )

    @field_validator("title")
    @classmethod
    def validate_title(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("任务标题不能为空")
        return cleaned


class ColumnItemDto(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    columnName: str = Field(..., ge=1, description="看板列名不能为空")
    tasks: list[TaskItemDto] = Field(default=[], description="看板列下的任务")

    @field_validator("columnName")
    @classmethod
    def validate_column_name(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("看板列名不能为空")
        return cleaned


class AiBatchCreateProjectDto(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    projectName: str = Field(..., ge=1, description="项目名不能为空")
    isAiGenerated: bool = Field(default=True, description="是否由AI生成")
    aiPrompt: str | None = Field(ge=1, description="用户原始提示词")
    aiModelInfo: str = Field(default=settings.model_name, description="AI模型")

    columns: list[ColumnItemDto] = Field(default=[], description="看板列")

    @field_validator("projectName")
    @classmethod
    def validate_project_name(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("项目名不能为空")
        return cleaned

    @field_validator("aiPrompt")
    @classmethod
    def validate_ai_prompt(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("提示词不能为空")
        return cleaned
