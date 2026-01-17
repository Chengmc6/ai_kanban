from enum import Enum


class ResultCode(Enum):
    # --- 200: 成功 ---
    SUCCESS = (200, "Success")

    # --- 51xx: AI 技术基础设施错误 ---
    MODEL_ERROR = (5101, "AI 模型调用失败")
    PARSE_ERROR = (5102, "AI 返回内容解析失败")
    PROMPT_ERROR = (5103, "AI Prompt 构造错误")
    TIMEOUT = (5105, "AI 请求超时")

    # --- 52xx: AI 业务逻辑/内容质量错误 ---
    AI_CONTENT_OFFENSIVE = (5201, "AI 生成内容包含违规信息")
    AI_TASK_OVERFLOW = (5202, "AI 分解任务数量过多(单次上限50)")
    AI_INVALID_PROJECT_TYPE = (5203, "AI 无法识别该类型的项目需求")
    AI_DATE_LOGIC_ERROR = (5204, "AI 生成的时间逻辑有误(如截止日期早于创建日期)")

    # --- 53xx: 外部系统交互错误 ---
    JAVA_API_ERROR = (5104, "调用 Java 后端失败")
    JAVA_AUTH_EXPIRED = (5301, "Java 端登录凭证已失效")
    JAVA_PERMISSION_DENIED = (5302, "当前用户无权在 Java 端创建资源")

    # --- 59xx: 兜底 ---
    UNKNOWN_ERROR = (5199, "未知 AI 错误")

    def code(self):
        return self.value[0]

    def message(self):
        return self.value[1]
