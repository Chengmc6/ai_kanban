from pydantic import BaseModel, Field


class ProjectQueryDto(BaseModel):
    pageNum: int = Field(1, ge=1, description="页码,默认第一页")
    pageSize: int = Field(10, ge=1, description="每页数量,默认每页10条数据")
    keywords: str | None = Field(None, description="搜索关键字,可为空")
