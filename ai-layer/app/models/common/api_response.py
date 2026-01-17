from typing import Generic, Self, TypeVar

from pydantic import BaseModel, ConfigDict

from app.models.common.result_code import ResultCode

# 定义泛型T
T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    # Pydantic v2 推荐写法：使用 ConfigDict  # noqa: RUF003
    model_config = ConfigDict(
        extra="forbid", frozen=False
    )  # frozen=False 允许运行时修改

    code: int = 200
    message: str = "Success"
    data: T | None = None

    @classmethod
    def success(cls, message: str = "Success", data: T | None = None) -> Self:
        return cls(code=ResultCode.SUCCESS.code(), message=message, data=data)

    @classmethod
    def fail(cls, result_code: ResultCode, custom_message: str | None = None) -> Self:
        msg = custom_message if custom_message else result_code.message()
        return cls(code=result_code.code(), message=msg, data=None)
