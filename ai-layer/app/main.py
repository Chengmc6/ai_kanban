from atexit import register
from fastapi import Body, FastAPI, Header

from app.core.config import settings
from app.core.exception_handler import register_exception_handler
from app.models.common.api_response import ApiResponse
from app.services.ai_service import AiKanbanService

app = FastAPI(title=settings.app_name, debug=settings.debug)

# 1. 注册全局异常处理器
register_exception_handler(app)

@app.post("/ai/generate",response_model=ApiResponse)
async def generate_kanban(
    # 直接获取原始的 authorization 字符串
    authorization: str = Header(..., description="用户登录凭证"),
    prompt: str = Body(..., embed=True),
):
    """
    AI 看板生成接口：
    接收前端 Token 和 Prompt，由 Python 编排 AI 生成并调用 Java 入库
    """  # noqa: RUF002
    # 2. 建议直接把原始 authorization 传给 service
    # 这样 JavaClient 初始化时就不需要再判断是否补上 "Bearer "
    service = AiKanbanService(authorization)
    return await service.generate_batch(prompt)
    