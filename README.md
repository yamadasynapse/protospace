# テーブル設計

## users テーブル

| Column         | Type         | Options         |
| -------------- | ------------ | --------------- |
| id             | SERIAL       | NOT NULL        |
| name           | VARCHAR(128) | NOT NULL        |
| email          | VARCHAR(128) | NOT NULL UNIQUE |
| password       | VARCHAR(512) | NOT NULL        |
| profile        | VARCHAR(128) | NOT NULL        |
| affiliation    | VARCHAR(128) | NOT NULL        |
| position       | VARCHAR(128) | NOT NULL        |
### Option
- PRIMARY KEY (id)

## protype テーブル(投稿用フォーム)

| Column     | Type         | Options                            |
| ---------- | ------------ | ---------------------------------- |
| id         | SERIAL       | NOT NULL                           |
| taitle     | VARCHAR(512) | NOT NULL                           |
| concept    | VARCHAR(512) | NOT NULL                           |
| user_id    | INT          | NOT NULL                           |
| coment_id  | INT          | NOT NULL                           |
| image      | VARCHAR(512) |                                    |
| created_at | TIMESTAMP    | NOT NULL DEFAULT CURRENT_TIMESTAMP |

### Option
- PRIMARY KEY (id)
- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
- FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE

### Commentテーブル　
| Column     | Type         | Options                            |
| ---------- | ------------ | ---------------------------------- |
| id         | SERIAL       | NOT NULL                           |
| content    | VARCHAR(512) | NOT NULL                           |
| user_id    | INT          | NOT NULL                           |
| protype_id | INT          | NOT NULL                           |
| created_at | TIMESTAMP    | NOT NULL DEFAULT CURRENT_TIMESTAMP |

### 外部キー制約（例）
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (protype_id) REFERENCES protypes(id) ON DELETE CASCADE