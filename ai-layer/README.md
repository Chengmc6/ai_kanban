# AI Kanban - AI レイヤー

## 概要

このプロジェクトは、AI 駆動型かんばんシステムのバックエンドサービスです。ユーザーの自然言語プロンプトを受け取り、AI が自動的にプロジェクト計画を生成し、データベースに保存します。Python/FastAPI で実装されており、Java バックエンドとの統合も行います。

## 主な機能

- **AI ブループリント生成**: ユーザーのプロンプトから AI がプロジェクト構造を自動生成
- **バッチプロジェクト作成**: 生成されたプランを一括でプロジェクトとして作成
- **Java 統合**: 大量データ処理は Java バックエンドに委譲
- **例外処理**: グローバル例外ハンドリングとビジネス例外管理
- **API レスポンス統一**: 標準的な API レスポンス形式を提供

## プロジェクト構成

```
ai-layer/
├── app/                          # アプリケーションコード
│   ├── main.py                   # FastAPI メインエントリーポイント
│   ├── api/                      # API ルートハンドラ
│   │   └── v1/                   # API v1 エンドポイント
│   ├── core/                     # コア機能
│   │   ├── config.py             # 設定管理
│   │   ├── business_exception.py # ビジネス例外定義
│   │   └── exception_handler.py  # 例外ハンドラー登録
│   ├── models/                   # データモデル
│   │   ├── ai/                   # AI 関連モデル
│   │   │   ├── ai_blueprint.py   # ブループリント定義
│   │   │   ├── blueprint_generator.py  # ブループリント生成ロジック
│   │   │   └── blueprint_executor.py   # ブループリント実行ロジック
│   │   ├── common/               # 共通モデル
│   │   │   ├── api_response.py   # API レスポンステンプレート
│   │   │   └── result_code.py    # 結果コード定義
│   │   └── dto/                  # データ転送オブジェクト
│   │       ├── project/          # プロジェクト関連DTO
│   │       ├── task/             # タスク関連DTO
│   │       └── column/           # カラム関連DTO
│   ├── services/                 # ビジネスロジック層
│   │   ├── ai_service.py         # AI サービス
│   │   ├── integration_service.py# 統合サービス
│   │   └── java_client.py        # Java バックエンド通信
│   └── utils/                    # ユーティリティ関数
├── tests/                        # テストコード
├── pyproject.toml                # プロジェクト設定
└── requirements.txt              # 依存パッケージ
```

## API エンドポイント

### AI 看板生成

**リクエスト:**
```
POST /ai/generate
```

**ヘッダー:**
- `Authorization`: ユーザー認証トークン (必須)

**ボディ:**
```json
{
  "prompt": "プロンプトテキスト"
}
```

**レスポンス:**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    // 生成されたプロジェクトデータ
  }
}
```

## セットアップ

### 前提条件

- Python 3.8 以上
- pip または poetry

### インストール

1. リポジトリをクローン

```bash
git clone <repository-url>
cd ai-layer
```

2. 依存パッケージをインストール

```bash
pip install -r requirements.txt
```

または Poetry を使用:

```bash
poetry install
```

### 環境設定

`.env` ファイルを作成し、以下の環境変数を設定してください：

```env
APP_NAME=AI Kanban
DEBUG=False
# その他の設定...
```

## 実行

### 開発環境で実行

```bash
uvicorn app.main:app --reload
```

API は `http://localhost:8000` でアクセスできます。

### API ドキュメント

Swagger UI: `http://localhost:8000/docs`
ReDoc: `http://localhost:8000/redoc`

## アーキテクチャ

### 処理フロー

1. **リクエスト受け取り**: フロントエンドから認証情報とプロンプトを受け取り
2. **ブループリント生成**: AI がプロンプトを解析してプロジェクト構造を生成
3. **プロジェクト作成**: ブループリントをもとに Java バックエンドでプロジェクトを作成
4. **レスポンス返却**: 作成結果をフロントエンドに返却

### 主要クラス

- **AiKanbanService**: AI かんばんサービスのメインクラス
- **BlueprintGenerator**: プロンプトからブループリント生成
- **BlueprintExecutor**: ブループリント実行とデータベース操作
- **JavaClient**: Java バックエンドとの通信

## 開発ガイド

### 例外処理

ビジネスロジックで例外が発生した場合、`BusinessException` を使用してください：

```python
from app.core.business_exception import BusinessException

raise BusinessException(code=400, message="エラーメッセージ")
```

### API レスポンス

レスポンスは `ApiResponse` を使用して統一します：

```python
from app.models.common.api_response import ApiResponse

return ApiResponse(
    code=200,
    message="成功",
    data=result_data
)
```

## テスト

テストを実行するには：

```bash
pytest tests/
```

## トラブルシューティング

### 問題: 認証エラー
- 正しい `Authorization` ヘッダーが送信されているか確認
- トークンが有効期限内か確認

### 問題: Java バックエンド接続エラー
- Java サーバーが起動しているか確認
- `java_client.py` の接続設定を確認

## ライセンス

[ライセンス情報を追加してください]

## 貢献

プルリクエストを歓迎します。大きな変更の場合は、まず Issue を開いて変更内容を議論してください。

## サポート

問題が発生した場合は、Issue を作成してください。
