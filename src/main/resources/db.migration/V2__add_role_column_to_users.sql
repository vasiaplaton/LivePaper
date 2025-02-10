-- Шаг 1: Добавляем колонку без NOT NULL
ALTER TABLE users
ADD COLUMN role VARCHAR(50);

-- Шаг 2: Устанавливаем значение по умолчанию для всех существующих пользователей
UPDATE users
SET role = 'ROLE_USER'

-- Шаг 3: Делаем колонку NOT NULL и добавляем проверку на допустимые значения
ALTER TABLE users
ALTER COLUMN role SET NOT NULL;

ALTER TABLE users
ADD CONSTRAINT role_check CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'));