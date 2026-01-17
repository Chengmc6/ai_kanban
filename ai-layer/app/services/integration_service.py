from datetime import datetime
from typing import Any

from app.core.config import settings
from app.models.dto.column.custom_add_column import CustomAddColumn
from app.models.dto.project.ai_batch_cretae_project_dto import AiBatchCreateProjectDto
from app.models.dto.project.project_create_dto import ProjectCreateDto
from app.models.dto.task.task_create_dto import TaskCreateDto
from app.models.dto.task.task_detail_update_dto import TaskDetailUpdateDto
from app.services.java_client import JavaClient


class IntegrationService:
    def __init__(self, token: str) -> None:
        self.client = JavaClient(token)

    async def create_project(self, name: str) -> Any:
        dto = ProjectCreateDto(projectName=name)
        res = await self.client.post("/project", dto.model_dump())
        return res

    async def add_column(self, projectId: int, name: str) -> Any:
        dto = CustomAddColumn(projectId=projectId, name=name)
        res = await self.client.post("/column", dto.model_dump())
        return res

    async def create_task(
        self, projectId: int, columnId: int, title: str, priority: int | None
    ) -> Any:
        dto = TaskCreateDto(
            projectId=projectId, columnId=columnId, title=title, priority=priority
        )
        res = await self.client.post("/task", dto.model_dump())
        return res

    async def update_task(
        self,
        taskId: int,
        projectId: int,
        content: str,
        deadline: datetime,
        prompt: str | None,
    ) -> Any:
        dto = TaskDetailUpdateDto(
            taskId=taskId,
            projectId=projectId,
            content=content,
            deadline=deadline,
            isAiGenerated=True,
            aiPrompt=prompt,
            aiModelInfo=settings.model_name,
        )
        res = await self.client.post("/task/detail/ai", dto.model_dump())
        return res

    async def batch_create_project(self, dto: AiBatchCreateProjectDto) -> Any:
        res = await self.client.post("/project/ai/batch", dto.model_dump())
        return res
