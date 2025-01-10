# Kanban Task Tracker

Kanban Task Tracker — проект для управления задачами на основе методологии Kanban. Позволяет создавать, обновлять и удалять задачи, организовывать подзадачи и эпики, а также сохранять историю действий и поддерживать автосохранение состояния задач в файл.

## Основные возможности

### 1. Управление задачами (Tasks)
- Добавление, обновление и удаление задач.
- Поиск задач по идентификатору.
- Просмотр всех задач.
- Массовое удаление всех задач.

### 2. Управление подзадачами (Subtasks)
- Добавление подзадачи к эпику.
- Обновление и удаление подзадач.
- Поиск подзадач по идентификатору.
- Получение списка всех подзадач для конкретного эпика.
- Массовое удаление всех подзадач.

### 3. Управление эпиками (Epics)
- Добавление новых эпиков.
- Просмотр всех эпиков и их связанных подзадач.
- Обновление и удаление эпиков (удаляются все связанные подзадачи).
- Поиск эпиков по идентификатору.

### 4. История задач (History)
- Автоматическое добавление задач в историю просмотров.
- Хранение истории последних 10 просмотренных задач.
- Получение списка задач из истории.

### 5. Автосохранение и восстановление из файла
- Автоматическое сохранение всех задач, эпиков и подзадач в файл `autoSave.csv`.
- Восстановление состояния менеджера задач из файла при запуске программы.
- Поддержка сериализации задач в строковый формат и их десериализация.

---

## Структура проекта

- **task**:
  - Классы для представления задач: `Task`, `Subtask`, `Epic`.
  - Перечисление статусов задачи: `TaskStatus`.

- **manager**:
  - Интерфейс управления задачами: `TaskManager`.
  - Реализация менеджеров задач: `InMemoryTaskManager`, `FileBackedTaskManager`.
  - Утилитарный класс для получения менеджеров: `Managers`.

- **history**:
  - Интерфейс для работы с историей: `HistoryManager`.
  - Реализация менеджера истории: `InMemoryHistoryManager`.
  - Узлы двусвязного списка для хранения истории: `Node`.

- **exception**:
  - Исключение для обработки ошибок при сохранении задач в файл: `ManagerSaveException`.

- **test**:
  - Тесты для проверки работы менеджеров и истории: `FileBackedTaskManagerTest`, `InMemoryTaskManagerTest`, `InMemoryHistoryManagerTest`, `ManagersTest`.

---

## Запуск проекта

1. Убедитесь, что JDK версии 11 или выше установлена.
2. Запустите `Main.java` для работы с приложением.
3. Все изменения задач будут автоматически сохраняться в файл `autoSave.csv`.
