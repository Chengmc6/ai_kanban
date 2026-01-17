from pydantic import BaseModel, Field


class AiTaskItem(BaseModel):
    title: str = Field(..., description="任务标题，动宾短语")  # noqa: RUF001
    content: str = Field(
        ...,
        description="详细的任务内容，包含具体的执行步骤、技术要点和验收准则",  # noqa: RUF001
    )
    priority: int = Field(
        default=1,
        ge=1,
        le=3,
        description="紧急程度：1(普通), 2(重要), 3(紧急)",  # noqa: RUF001
    )
    days_to_complete: int = Field(
        default=3,
        description="从当前开始，预计多少天内完成该任务",  # noqa: RUF001
    )


class AiColumnItem(BaseModel):
    name: str = Field(..., description="看板列名（如：待处理、进行中、已完成、测试中）")  # noqa: RUF001
    tasks: list[AiTaskItem] = Field(default_factory=list)


class AiProjectBlueprint(BaseModel):
    name: str = Field(..., description="富有吸引力的项目名称")
    columns: list[AiColumnItem] = Field(default_factory=list)
