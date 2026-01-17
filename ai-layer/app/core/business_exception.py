from app.models.common.result_code import ResultCode


class BusinessException(Exception):
    def __init__(self, result: ResultCode | int, message: str | None = None):
        if isinstance(result, ResultCode):
            # 如果传的是枚举，提取 code 和其默认 message  # noqa: RUF003
            self.code = result.code()
            self.message = message if message else result.message()
        else:
            # 如果传的是 int，直接使用  # noqa: RUF003
            self.code = result
            self.message = message if message else "Unknown Business Error"
        super().__init__(self.message)
