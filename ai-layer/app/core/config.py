from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """
    项目全局配置（从 .env 或环境变量加载）
    优先级：环境变量 > .env 文件 > 默认值
    """  # noqa: RUF002

    # --- 基础配置 ---
    app_name: str = Field(default="AI Kanban AI Services", description="应用名称")
    debug: bool = Field(default=False, alias="DEBUG", description="是否开启调试模式")

    # --- Java 后端配置 ---
    java_api_base: str = Field(
        default="http://localhost:8080",
        alias="JAVA_API_BASE",
        description="Java 后端服务地址（带协议和端口）",  # noqa: RUF001
    )
    java_timeout: float = Field(
        default=20.0,
        alias="JAVA_TIMEOUT",
        ge=5.0,  # 最小超时 5 秒，避免太短  # noqa: RUF003
        description="请求 Java 后端的超时时间（秒）",  # noqa: RUF001
    )

    # --- AI 模型配置 ---
    gemini_api_key: str = Field(
        ...,
        alias="GEMINI_API_KEY",
        description="Google Gemini API Key（必填）",  # noqa: RUF001
    )

    # 可扩展的通用 AI 配置（未来切换模型时保留）  # noqa: RUF003
    ai_provider: str = Field(
        default="gemini",
        alias="AI_PROVIDER",
        pattern="^(gemini|openai|deepseek|qwen)$",  # 限制可选值
        description="AI 提供商（gemini/openai/deepseek/qwen）",  # noqa: RUF001
    )
    ai_api_base: str | None = Field(
        default=None,
        alias="AI_API_BASE",
        description="自定义 AI API 基地址（留空使用官方）",  # noqa: RUF001
    )
    model_name: str = Field(
        default="gemini-1.5-flash",
        alias="AI_MODEL_NAME",
        description="使用的 AI 模型名称",
    )
    ai_system_prompt: str = Field(..., alias="AI_SYSTEM_PROMPT")

    # --- Pydantic v2 配置（推荐写法） ---  # noqa: RUF003
    model_config = SettingsConfigDict(
        env_file=".env",  # 自动加载 .env
        env_file_encoding="utf-8",
        env_ignore_empty=True,  # 忽略空值环境变量
        extra="ignore",  # 忽略多余字段
        case_sensitive=False,  # 环境变量不区分大小写
        validate_default=True,  # 校验默认值
    )


# 全局单例（推荐在项目启动时加载一次）  # noqa: RUF003
@lru_cache
def get_settings() -> Settings:
    """获取配置单例"""
    return Settings()  # type: ignore


settings = get_settings()
