# Kanban Task Tracker
Kanban Task Tracker — это проект для управления задачами с использованием методологии Kanban. Трекер позволяет создавать, обновлять, удалять и отслеживать задачи, объединяя их в подзадачи и эпики. Программа поддерживает базовые CRUD операции, а также предоставляет функционал для просмотра истории действий.

## Функции трекера задач

### 1. Управление задачами (Tasks)
- Добавление новой задачи.
- Просмотр всех задач.
- Обновление данных задачи.
- Поиск задачи по идентификатору.
- Удаление задачи по идентификатору.
- Удаление всех задач.

### 2. Управление саб-задачами (Subtasks)
- Добавление саб-задачи к существующему эпику.
- Просмотр всех саб-задач для определенного эпика.
- Обновление данных саб-задачи.
- Поиск саб-задачи по идентификатору.
- Удаление саб-задачи по идентификатору.
- Удаление всех саб-задач.

### 3. Управление эпиками (Epics)
- Добавление нового эпика.
- Просмотр списка всех эпиков и их связанных саб-задач.
- Обновление информации об эпике.
- Поиск эпика по идентификатору.
- Удаление эпика по идентификатору (удаляются все связанные саб-задачи).
- Удаление всех эпиков.

### 4. История задач (History)
- Поддержка истории задач: каждая просмотренная задача автоматически добавляется в список.
- Хранение истории последних 10 задач.
- Получение списка задач из истории.

## Структура проекта

### Пакеты
- `task`:
   - **Task**: Основной класс для представления задачи.
   - **Subtask**: Класс для подзадач, которые принадлежат эпику.
   - **Epic**: Класс для задач, которые группируют саб-задачи.
   - **TaskStatus**: Перечисление для статусов задачи (NEW, IN_PROGRESS, DONE).

- `manager`:
   - **TaskManager**: Интерфейс для управления задачами.
   - **InMemoryTaskManager**: Реализация менеджера задач, работающая в памяти.
   - **Managers**: Утилитарный класс для получения менеджеров.

- `history`:
   - **HistoryManager**: Интерфейс для работы с историей.
   - **InMemoryHistoryManager**: Реализация менеджера истории, работающая в памяти.

- `test`:
   - **InMemoryTaskManagerTest**: Тесты для `InMemoryTaskManager`.
   - **InMemoryHistoryManagerTest**: Тесты для `InMemoryHistoryManager`.
   - **ManagersTest**: Проверка утилитарного класса `Managers`.
