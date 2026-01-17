"""
Java 后端 API 客户端模块
负责与 Java 服务进行 HTTP 通信，处理认证、异常和响应解析
"""  # noqa: RUF002

import httpx

from app.core.business_exception import BusinessException
from app.core.config import settings
from app.models.common.result_code import ResultCode


class JavaClient:
    """
    Java API 客户端

    用于与 Java 后端服务进行异步 HTTP 通信
    - 自动处理 Bearer Token 认证
    - 捕获各类 HTTP 和网络异常
    - 解析 Java 服务的业务响应码
    """

    def __init__(self, token: str):
        """
        初始化 Java 客户端

        Args:
            token: 认证令牌（可以是 "Bearer xxx" 或 "xxx"，会自动规范化）
        """  # noqa: RUF002
        # 规范化 token 格式：确保以 "Bearer " 前缀开头  # noqa: RUF003
        self.headers = {
            "Authorization": token if token.startswith("Bearer ") else f"Bearer {token}"
        }
        # 从配置读取 Java API 的基础地址
        self.base_url = settings.java_api_base
        # 从配置读取请求超时时间（秒）  # noqa: RUF003
        self.timeout = settings.java_timeout

    async def post(self, path: str, json_data: dict):
        """
        向 Java API 发送 POST 请求

        Args:
            path: 请求路径（相对于 base_url，如 "/project/create"）
            json_data: 请求体数据（字典格式，会自动序列化为 JSON）

        Returns:
            dict: Java 服务的响应数据

        Raises:
            BusinessException: 当认证过期、权限不足、后端异常或业务处理失败时抛出
        """  # noqa: RUF002
        async with httpx.AsyncClient() as client:
            try:
                # 发送 HTTP POST 请求到 Java 服务
                response = await client.post(
                    f"{self.base_url}{path}",
                    json=json_data,
                    headers=self.headers,
                    timeout=self.timeout,
                )

                # ===== HTTP 状态码检查 =====
                # 401 认证失败（通常是 token 过期）  # noqa: RUF003
                if response.status_code == 401:
                    raise BusinessException(
                        ResultCode.JAVA_AUTH_EXPIRED,
                        "登录已失效，请重新登录",  # noqa: RUF001
                    )

                # 403 权限不足（用户无权执行此操作）  # noqa: RUF003
                if response.status_code == 403:
                    raise BusinessException(
                        ResultCode.JAVA_PERMISSION_DENIED, "您没有权限执行此操作"
                    )

                # 5xx 服务器错误（Java 后端服务故障）  # noqa: RUF003
                if response.status_code >= 500:
                    raise BusinessException(
                        ResultCode.JAVA_API_ERROR, "Java 后端服务异常(500+)"
                    )

                # 状态码正常后才尝试解析 JSON 响应体
                res_json = response.json()

            except httpx.HTTPError as e:
                # 捕获网络相关异常（连接超时、DNS 失败等）  # noqa: RUF003
                raise BusinessException(
                    ResultCode.JAVA_API_ERROR, f"网络通信故障: {e!s}"
                ) from e
            except Exception as e:
                # 捕获其他异常（JSON 解析错误等）  # noqa: RUF003
                raise BusinessException(
                    ResultCode.UNKNOWN_ERROR, f"系统异常: {e!s}"
                ) from e

            # ===== 业务逻辑检查 =====
            # Java 服务返回的业务响应码（200 表示成功）  # noqa: RUF003
            if res_json.get("code") != 200:
                # 业务处理失败，抛出异常并包含错误消息  # noqa: RUF003
                raise BusinessException(
                    ResultCode.JAVA_API_ERROR,
                    message=f"业务处理失败: {res_json.get('message')}",
                )

            # 返回成功的响应数据给调用者
            return res_json
