from datetime import datetime, timedelta

from app.models.ai.ai_blueprint import AiProjectBlueprint
from app.models.dto.project.ai_batch_cretae_project_dto import (
    AiBatchCreateProjectDto,
    ColumnItemDto,
    TaskItemDto,
)
from app.services.integration_service import IntegrationService


class BlueprintExecutor:
    def __init__(self, token: str):
        self.integration = IntegrationService(token=token)

    async def execute_create_project(
        self, blueprint: AiProjectBlueprint, user_prompt: str
    ):
        project_res = await self.integration.create_project(blueprint.name)
        project_id = project_res["data"]["id"]

        for column in blueprint.columns:
            col_res = await self.integration.add_column(project_id, column.name)
            col_id = col_res["data"]["id"]

            for task in column.tasks:
                task_res = await self.integration.create_task(
                    project_id, col_id, task.title, task.priority
                )
                task_id = task_res["data"]["id"]
                deadline = datetime.now() + timedelta(task.days_to_complete)

                await self.integration.update_task(
                    task_id, project_id, task.content, deadline, user_prompt
                )

        return project_res

    async def execute_batch_create_project(
        self, blueprint: AiProjectBlueprint, user_prompt: str
    ):
        base_time = datetime.now()
        dto = AiBatchCreateProjectDto(
            projectName=blueprint.name,
            isAiGenerated=True,
            aiPrompt=user_prompt,
            columns=[
                ColumnItemDto(
                    columnName=col.name,
                    tasks=[
                        TaskItemDto(
                            title=task.title,
                            content=task.content,
                            priority=task.priority,
                            deadline=base_time + timedelta(task.days_to_complete),
                        )
                        for task in col.tasks
                    ],
                )
                for col in blueprint.columns
            ],
        )
        project_res = await self.integration.batch_create_project(dto)
        return project_res
