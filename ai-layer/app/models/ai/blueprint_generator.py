from typing import Optional

from pydantic_ai import Agent

from app.core.config import settings
from app.models.ai.ai_blueprint import AiProjectBlueprint


class BlueprintGenerator:
    _instance: Optional["BlueprintGenerator"] = None

    def __init__(self):
        self.agent = Agent(
            f"google-gla:{settings.model_name}",
            output_type=AiProjectBlueprint,
            system_prompt=settings.ai_system_prompt,
        )

    @classmethod
    def get_instance(cls) -> "BlueprintGenerator":
        if cls._instance is None:
            cls._instance = cls()
        return cls._instance

    async def generate_blueprint(self, user_prompt: str) -> AiProjectBlueprint:
        result = await self.agent.run(user_prompt=user_prompt)
        return result.output
