from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field


class TaskDetailUpdateDto(BaseModel):
    model_config = ConfigDict(extra="forbid", frozen=False)  # 禁止前端传入未定义字段

    taskId: int = Field(..., description="任务ID不能为空")
    projectId: int = Field(..., description="项目ID不能为空")
    content: str | None = Field(None, description="任务内容", min_length=1)
    deadline: datetime | None = Field(None, description="截至时间")
    isAiGenerated: bool = Field(True, description="是否AI生成")
    aiPrompt: str | None = Field(None, description="AI提示词")
    aiModelInfo: str | None = Field(None, description="AI模型信息", max_length=50)
