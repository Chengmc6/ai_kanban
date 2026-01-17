from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from pydantic import ValidationError

from app.core.business_exception import BusinessException
from app.models.common.api_response import ApiResponse
from app.models.common.result_code import ResultCode


def register_exception_handler(app: FastAPI):
    """
    注册全局异常处理器，确保所有接口返回统一的 ApiResponse 格式
    """  # noqa: RUF002

    # 1. 业务异常（最高优先级）  # noqa: RUF003
    @app.exception_handler(BusinessException)
    async def business_exception_handler(request: Request, exc: BusinessException):
        return JSONResponse(
            status_code=200,
            content=ApiResponse.fail(
                result_code=ResultCode(exc.code, exc.message),
                custom_message=exc.message,
            ).model_dump(),
        )

    # 2. Pydantic 参数校验失败
    @app.exception_handler(RequestValidationError)
    @app.exception_handler(ValidationError)
    async def validation_exception_handler(request: Request, exc: ValidationError):
        # 统一处理校验错误
        if isinstance(exc, (RequestValidationError, ValidationError)):
            errors = exc.errors()
            # 提取具体的字段和错误原因，例如: "projectName: 不能为空"
            msg = (
                f"{errors[0]['loc'][-1]}: {errors[0]['msg']}"
                if errors
                else "参数校验失败"
            )
        else:
            msg = "数据格式解析错误"

        return JSONResponse(
            status_code=200,
            content=ApiResponse.fail(
                result_code=ResultCode.PARSE_ERROR, custom_message=msg
            ).model_dump(),
        )

    # 3. 全局未知异常（兜底）  # noqa: RUF003
    @app.exception_handler(Exception)
    async def global_exception_handler(request: Request, exc: Exception):
        # 生产环境建议记录详细日志（不要直接返回给前端）  # noqa: RUF003

        return JSONResponse(
            status_code=200,
            content=ApiResponse.fail(
                result_code=ResultCode.UNKNOWN_ERROR,
                custom_message="服务器内部错误，请稍后重试",  # noqa: RUF001
            ).model_dump(),
        )
