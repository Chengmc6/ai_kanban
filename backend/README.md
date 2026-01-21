# AI Kanban - バックエンド

## プロジェクト概要

AI Kanban は、プロジェクト管理と タスク追跡を効率化するための包括的な Kanban ボード管理システムです。Spring Boot フレームワークを使用して構築され、企業レベルのアプリケーション要件に対応しています。

## 主な機能

- **ユーザー管理**: ユーザー認証、ロール管理、権限制御
- **プロジェクト管理**: プロジェクトの作成、編集、削除、メンバー管理
- **タスク管理**: タスクの作成、更新、ステータス管理
- **Kanban ボード**: ボード、カラムの管理とタスクの可視化
- **ユーザー権限**: 詳細なロールベースのアクセス制御（RBAC）
- **タスク詳細情報**: タスクの詳細情報、コメント、添付ファイルなど

## 技術スタック

### コア技術
- **Java 17**: プログラミング言語
- **Spring Boot 3.5.9**: アプリケーション フレームワーク
- **Spring Security**: 認証・認可機構
- **Spring Cache**: キャッシング システム
- **Spring AOP**: アスペクト指向プログラミング

### データベース & ORM
- **MySQL**: リレーショナル データベース
- **MyBatis-Plus 3.5.9**: ORM フレームワーク
- **MyBatis-Plus Generator**: コード自動生成ツール

### その他のライブラリ
- **Spring Validation**: データ検証
- **JWT**: トークンベース認証
- **Freemarker**: テンプレート エンジン

## プロジェクト構造

```
src/
├── main/
│   ├── java/com/example/ai_kanban/
│   │   ├── AiKanbanApplication.java       # メイン アプリケーション クラス
│   │   ├── aop/                            # アスペクト指向プログラミング
│   │   │   ├── annotation/                 # カスタム アノテーション
│   │   │   └── aspect/                     # アスペクト 実装
│   │   ├── common/                         # 共通 ユーティリティ
│   │   │   ├── ApiResponse.java            # API レスポンス 定義
│   │   │   ├── ResultCode.java             # 結果 コード 定義
│   │   │   ├── config/                     # 設定 クラス
│   │   │   ├── enums/                      # 列挙型 定義
│   │   │   ├── exception/                  # 例外 クラス
│   │   │   ├── handlers/                   # エラー ハンドラー
│   │   │   ├── interfaces/                 # 共通 インターフェース
│   │   │   └── utils/                      # ユーティリティ クラス
│   │   ├── controller/                     # API コントローラー
│   │   │   ├── AdminUserController.java
│   │   │   ├── BoardColumnController.java
│   │   │   ├── ProjectController.java
│   │   │   ├── ProjectMemberController.java
│   │   │   ├── RoleController.java
│   │   │   ├── TaskController.java
│   │   │   ├── TaskDetailController.java
│   │   │   ├── UserController.java
│   │   │   └── UserRoleController.java
│   │   ├── domain/                         # ドメイン層
│   │   │   ├── dto/                        # データ転送 オブジェクト
│   │   │   ├── entity/                     # JPA エンティティ
│   │   │   ├── mapper/                     # MyBatis マッパー
│   │   │   └── service/                    # ビジネス ロジック
│   │   ├── permission/                     # 権限 管理
│   │   │   └── annotation/                 # 権限 アノテーション
│   │   ├── security/                       # セキュリティ 設定
│   │   │   ├── config/                     # セキュリティ 設定
│   │   │   ├── handler/                    # セキュリティ ハンドラー
│   │   │   ├── jwt/                        # JWT 実装
│   │   │   ├── model/                      # セキュリティ モデル
│   │   │   ├── service/                    # セキュリティ サービス
│   │   │   └── utils/                      # セキュリティ ユーティリティ
│   │   └── validation/                     # データ 検証
│   │       ├── annotation/                 # 検証 アノテーション
│   │       └── validator/                  # 検証 実装
│   └── resources/
│       ├── application.properties           # アプリケーション 設定
│       └── mapper/                          # MyBatis XML マッパー
└── test/                                   # テスト クラス
```

## セットアップ手順

### 前提条件

- Java 17 以上
- Maven 3.8.1 以上
- MySQL 5.7 以上
- Git

### インストール

1. リポジトリをクローンします

```bash
git clone <repository-url>
cd ai_kanban/backend
```

2. MySQL データベースを作成します

```sql
CREATE DATABASE ai_kanban CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. `application.properties` ファイルを設定します

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ai_kanban
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis Configuration
mybatis-plus.mapper-locations=classpath*:mapper/*.xml
mybatis-plus.type-aliases-package=com.example.ai_kanban.domain.entity
```

4. 依存関係をインストールしビルドします

```bash
mvn clean install
```

5. アプリケーションを起動します

```bash
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## API エンドポイント

### ユーザー管理
- `GET /api/users` - ユーザー一覧を取得
- `GET /api/users/{id}` - ユーザー詳細を取得
- `POST /api/users` - 新規ユーザーを作成
- `PUT /api/users/{id}` - ユーザー情報を更新
- `DELETE /api/users/{id}` - ユーザーを削除

### プロジェクト管理
- `GET /api/projects` - プロジェクト一覧を取得
- `POST /api/projects` - 新規プロジェクトを作成
- `GET /api/projects/{id}` - プロジェクト詳細を取得
- `PUT /api/projects/{id}` - プロジェクトを更新
- `DELETE /api/projects/{id}` - プロジェクトを削除

### タスク管理
- `GET /api/tasks` - タスク一覧を取得
- `POST /api/tasks` - 新規タスクを作成
- `GET /api/tasks/{id}` - タスク詳細を取得
- `PUT /api/tasks/{id}` - タスクを更新
- `DELETE /api/tasks/{id}` - タスクを削除

### Kanban ボード
- `GET /api/boards` - ボード一覧を取得
- `GET /api/board-columns` - ボードカラム一覧を取得
- `POST /api/board-columns` - 新規ボードカラムを作成

## 設定ファイル

### application.properties

メインの設定ファイルで、以下の項目を設定できます：

- データベース接続情報
- ポート番号
- ログレベル
- キャッシュ設定
- JWT 設定

## テスト

ユニット テストを実行するには：

```bash
mvn test
```

## トラブルシューティング

### MySQL 接続エラー
- MySQL サーバーが起動しているか確認してください
- `application.properties` のデータベース URL、ユーザー名、パスワードを確認してください

### ポート競合エラー
- ポート 8080 が他のアプリケーションで使用されていないか確認してください
- `application.properties` で別のポートを指定できます：`server.port=8081`

### 権限エラー
- ユーザーに適切なロールと権限が割り当てられているか確認してください

## 開発ガイドライン

### コーディング規約
- Java 命名規約に従います
- インデントは 4 スペースを使用します
- コメントは日本語または英語で記載します

### Git ワークフロー
1. 機能ごとに新しいブランチを作成します
2. 変更をコミットします
3. プル リクエストを作成します
4. コード レビュー後にマージします

## ライセンス

このプロジェクトはライセンスの下で公開されています。詳細は LICENSE ファイルをご覧ください。

## サポート

問題や質問がある場合は、GitHub Issues でご報告ください。

## 変更履歴

### v0.0.1-SNAPSHOT
- 初期リリース
- 基本的なユーザー管理
- プロジェクト管理
- タスク管理
- Kanban ボード

---

**最終更新**: 2026年1月20日
