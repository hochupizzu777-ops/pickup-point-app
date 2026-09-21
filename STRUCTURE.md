# Структура pickup-point-app без реализации

Этот архив дополняет ваш существующий Maven-проект. Здесь только объявления типов,
пакеты, связи наследования/реализации, параметры generics и значения enum.
Нет полей, конструкторов, методов, бизнес-логики или выполненных тестов.
Интерфейсы временно пустые: это структура, а не уже согласованные контракты методов.
Когда появятся методы интерфейса, их понадобится добавить и в реализации.

## Как добавить в существующий проект

1. Сохраните текущую работу коммитом. Проверьте git status.
2. Если ветка chore/shared-contracts ещё используется и не слита, продолжайте в ней.
   Иначе из обновлённой main создайте chore/project-structure.
3. Распакуйте архив в ОТДЕЛЬНУЮ папку вне проекта.
4. Скопируйте папку src из архива в корень своего pickup-point-app.
5. При совпадении имён выберите «Пропустить эти файлы», НЕ «Заменить».
   Обязательно сохраните существующие Main, CellSize, OrderStatus, Recipient,
   OrderItem и любые другие уже написанные файлы.
6. Этот STRUCTURE.md можно скопировать в корень проекта как описание структуры.
   README.md, pom.xml и .gitignore в архиве отсутствуют: существующие сохраняются.
7. В IntelliJ выполните Maven → Lifecycle → compile и test-compile.
   Это проверка компиляции; настоящих тестов в новых файлах пока нет.
8. Проверьте git diff --stat и git status. Добавьте только нужные файлы через git add.
   Создайте коммит и отправьте ветку. После проверки объедините PR с main.
9. Напарник обновляет main; затем каждый создаёт ветку своей реализации.

Если вы хотите писать структуру вручную: создавайте перечисленные пакеты и типы
через New → Java Class / Interface / Enum. Не создавайте дополнительные корни
participant1 и participant2: это одно приложение, владельцы указаны в таблице.

## Собственные типы приложения

Все перечисленные ниже пакеты находятся под ru.team.pickup в src/main/java.

| Пакет | Тип | Вид | Ответственный | Назначение |
|---|---|---|---|---|
| model | `Order` | class | участник 1 | Заказ. |
| model | `OrderItem` | class | участник 1 | Позиция заказа: товар и количество. |
| model | `Recipient` | class | участник 1 | Получатель заказа. |
| model | `StorageCell` | class | участник 2 | Ячейка хранения. |
| model | `AppSnapshot` | class | участник 2; контракт согласуют оба | Снимок состояния заказов и ячеек. |
| model | `OrderStatus` | enum | участник 1; значения согласуют оба | Статусы заказа. |
| model | `CellSize` | enum | участник 2; значения согласуют оба | Размеры ячейки и заказа. |
| repository | `Repository` | interface | участник 1; контракт согласуют оба | Общий контракт хранилища. |
| repository | `InMemoryOrderRepository` | class | участник 1 | Хранилище в оперативной памяти. |
| repository | `InMemoryCellRepository` | class | участник 2 | Хранилище в оперативной памяти. |
| service | `WarehouseOperations` | interface | участник 2; контракт согласуют оба | Граница взаимодействия заказов со складом. |
| service | `WarehouseService` | class | участник 2 | Операции размещения на складе. |
| service | `PickupPointService` | class | участник 1 | Координация операций с заказами и ячейками. |
| service | `OrderQueries` | class | участник 1 | Поиск, фильтрация и сортировка заказов. |
| service | `ReportService` | class | участник 2 | Статистика и отчёты. |
| persistence | `LineCodec` | interface | участник 1; контракт согласуют оба | Преобразование объекта в строку и обратно. |
| persistence | `StateStore` | interface | участник 2; контракт согласуют оба | Сохранение и загрузка общего состояния. |
| persistence | `OrderLineCodec` | class | участник 1 | Преобразование записи для файла состояния. |
| persistence | `CellLineCodec` | class | участник 2 | Преобразование записи для файла состояния. |
| persistence | `TextStateStore` | class | участник 2 | Текстовый файл состояния. |
| ui | `OrderMenu` | class | участник 1 | Команды работы с заказами. |
| ui | `WarehouseMenu` | class | участник 2 | Команды склада и отчётов. |
| ui | `ConsoleInput` | class | участник 1; используют оба | Общий консольный ввод. |
| ui | `ConsoleApplication` | class | участник 2; используют оба | Главное меню приложения. |
| background | `ExpirationScheduler` | class | участник 2 | Запуск и остановка фоновой проверки просрочки. |
| exception | `PickupPointException` | class | участник 1 | Базовое исключение бизнес-операций. |
| exception | `OrderOperationException` | class | участник 1 | Недопустимая операция с заказом. |
| exception | `EntityNotFoundException` | class | участник 1 | Обязательная сущность не найдена. |
| exception | `NoFreeCellException` | class | участник 2 | Нет подходящей свободной ячейки. |
| exception | `DataFormatException` | class | участник 2 | Некорректный формат файла. |
| (корневой) | `Main` | class | участник 1; запуск согласуют оба | Точка входа и создание объектов. Существующий Main не заменять. |

## Заготовки тестов

Все находятся в src/test/java, под пакетом ru.team.pickup.

| Пакет | Класс | Ответственный |
|---|---|---|
| service | `PickupPointServiceTest` | 1 |
| service | `OrderQueriesTest` | 1 |
| persistence | `OrderLineCodecTest` | 1 |
| service | `WarehouseServiceTest` | 2 |
| service | `ReportServiceTest` | 2 |
| persistence | `TextStateStoreTest` | 2 |
| (корневой) | `ApplicationScenarioTest` | оба |
| (корневой) | `ConcurrentOperationsTest` | оба |
| (корневой) | `TestRunner` | оба |

## Основные связи

- InMemoryOrderRepository implements Repository<Order, String>.
- InMemoryCellRepository implements Repository<StorageCell, String>.
- WarehouseService implements WarehouseOperations.
- OrderLineCodec implements LineCodec<Order>.
- CellLineCodec implements LineCodec<StorageCell>.
- TextStateStore implements StateStore.
- PickupPointException extends RuntimeException.
- OrderOperationException, EntityNotFoundException и NoFreeCellException
  наследуются от PickupPointException.
- DataFormatException extends IOException.

Все остальные классы пока пустые. Модели отмечены final как подготовка к
неизменяемости; одного final у класса недостаточно, окончательную защиту полей
добавит реализация. Конструкторы исключений с сообщениями добавим позже.

## Уже написанные типы

В архиве CellSize содержит только SMALL, MEDIUM, LARGE. В вашем существующем
CellSize уже есть level, getLevel и fits: оставьте ваш файл.
OrderStatus содержит READY_FOR_PICKUP, ISSUED, EXPIRED, RETURNED.
Пустые Recipient и OrderItem не должны заменять ваши готовые классы.
Пустой Main нужен только как обозначение места в структуре. Для запуска
сохраните свой Main с методом main; архив сам по себе не запускает приложение.

## Работа после общей структуры

Участник 1 реализует заказы, участник 2 — склад. Классы общих интерфейсов
редактирует назначенный владелец после согласования сигнатур с напарником.
Создание пустых типов ещё не снимает зависимость от будущих конструкторов и
методов. Прежде чем вызывать чужую часть, согласуйте её контракт.

## Что не создаём самостоятельно

List, Map, Optional, Comparator, Predicate, LocalDate, Clock, Path, Files,
IOException и типы java.util.concurrent уже входят в JDK. Сейчас они не нужны
в пустых заготовках, кроме IOException в иерархии исключений. Импорты остальных
появятся при добавлении сигнатур и реализации.

## Проверка архива

Все исходники приложения и заготовки тестов проверены совместной компиляцией
через javac на JDK 17 (доступном в среде подготовки). Каркас использует базовый
синтаксис, совместимый с целевой Java 21 вашего проекта. Maven-сборка вашего
локального проекта здесь не запускалась. Бизнес-поведение не проверялось:
оно намеренно отсутствует.
