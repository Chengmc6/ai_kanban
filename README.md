# AI Kanban

## プロジェクト概要

AI Kanbanは、AIを活用したタスク管理・プロジェクト管理システムです。ユーザーがより効率的にプロジェクトを管理し、タスクを追跡できるように設計されています。

勉強のために開発しています。

## 主な機能

- **プロジェクト管理**: プロジェクトの作成、更新、削除、メンバー管理
- **タスク管理**: タスクの作成・管理・ステータス追跡
- **AI統合**: AIを活用した自動タスク生成・最適化機能
- **ユーザー認証**: ユーザー登録・ログイン・権限管理
- **Kanbanボード**: 視覚的なカンバンボード表示

## 技術スタック

### バックエンド
- **フレームワーク**: Spring Boot 3.5.9
- **言語**: Java 17
- **データベース**: MySQL
- **ORM**: MyBatis Plus
- **ビルドツール**: Maven

### AI層
- **言語**: Python
- **役割**: AI機能の実装・処理

### フロントエンド
- **状態**: 開発中（Coming Soon）

## プロジェクト構成

```
ai_kanban/
├── backend/          # Spring Boot バックエンド
├── ai-layer/         # Python AI処理層
├── frontend/         # フロントエンド（開発中）
└── README.md         # このファイル
```

## インストール・セットアップ

### 要件

- Java 17以上
- MySQL 8.0以上
- Python 3.8以上
- Maven 3.6以上

### バックエンド設定

1. MySQLデータベースを作成
```sql
CREATE DATABASE ai_kanban;
```

2. `backend/src/main/resources/application.properties`でデータベース接続情報を設定

3. バックエンドを起動
```bash
cd backend
mvn spring-boot:run
```

### AI層設定

1. Python環境をセットアップ
```bash
cd ai-layer
python -m venv venv
source venv/bin/activate  # Linux/Mac
# または
venv\Scripts\activate  # Windows
```

2. 依存パッケージをインストール
```bash
pip install -r requirements.txt
```

3. AI層を起動
```bash
python app/main.py
```

## フロントエンド

フロントエンドは現在開発中です。完成予定は随時更新予定です。

## API仕様

バックエンドはRESTful APIを提供しています。詳細は、バックエンドのドキュメントをご参照ください。

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## 貢献

このプロジェクトへの貢献を歓迎します。

## サポート

問題が発生した場合は、GitHubのIssueを作成してください。

