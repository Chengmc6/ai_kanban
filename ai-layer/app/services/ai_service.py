from app.models.ai.blueprint_executor import BlueprintExecutor
from app.models.ai.blueprint_generator import BlueprintGenerator


class AiKanbanService:
    def __init__(self, token: str):
        self.generator = BlueprintGenerator.get_instance()
        self.executor = BlueprintExecutor(token=token)

    async def generate_single(self, user_prompt: str):
        blueprint = await self.generator.generate_blueprint(user_prompt)
        data = await self.executor.execute_create_project(blueprint, user_prompt)

        return data

    async def generate_batch(self, user_prompt: str):
        blueprint = await self.generator.generate_blueprint(user_prompt)
        data = await self.executor.execute_batch_create_project(blueprint, user_prompt)
        return data
