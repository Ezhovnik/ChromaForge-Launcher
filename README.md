# ChromaForge Launcher

**CLI-лаунчер для воксельного движка [ChromaForge](https://github.com/Ezhovnik/ChromaForge-v2).**

Скачивает релизы движка, проверяет их целостность, устанавливает и управляет изолированными мирами (инстансами) — прямо из терминала.

![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Windows CI](https://github.com/Ezhovnik/ChromaForge-Launcher/actions/workflows/windows.yml/badge.svg)
![Linux CI](https://github.com/Ezhovnik/ChromaForge-Launcher/actions/workflows/linux.yml/badge.svg)

---

## Возможности

- **Обзор релизов** — список всех опубликованных версий движка из GitHub со статусом установки
- **Безопасная установка** — файлы проверяются по SHA-256, собираются во временной папке и лишь затем переносятся на место
- **Проверка целостности** — перепроверить файлы любой установленной версии можно в любой момент
- **Изолированные миры** — каждый мир живёт в своей папке и привязан к своей версии движка
- **Версионированные реестры** — файлы, записанные более новым лаунчером, отклоняются вместо неверного прочтения; старые продолжают работать
- **Проброс аргументов движку** — любые флаги движка после `--`
- **Портативный режим** — положите пустой `settings.toml` рядом с исполняемым файлом, и все данные будут храниться рядом

## Демо

```text
$ launcher fetch

Found releases  (4)
  Version          Date         Status
  --------------------------------------------------
  0.4.1            2026-08-08   ✓ INSTALLABLE
  0.4.0            2026-08-07   ✓ INSTALLABLE
  0.3.0            2026-05-23   ✗ NO_BUILD
  0.2.0            2026-03-12   ✗ NO_BUILD

$ launcher install 0.4.1
  → Installing '0.4.1'...
  ✓ Successfully installed '0.4.1'

$ launcher new my_world 0.4.1
  ✓ The instance 'my_world' was successfully created on engine version '0.4.1'

$ launcher launch my_world
  → Launching 'my_world'...
```

## Требования

- **Windows 10+** или **Linux** (x86_64)
- Java 17 — **встроена** в собранные сборки; отдельно нужна только при запуске из обычного `.jar`
- Интернет для команд `fetch` и `install`

> [!WARNING]
> На данный момент MacOS не поддерживается лаунчером.

## Быстрый старт

```bash
launcher install 0.4.1           # скачать, проверить и установить движок
launcher ls                      # список установленных версий
launcher new my_instance 0.4.1   # создать мир
launcher launch my_instance      # запустить мир
launcher check 0.4.1             # проверить файлы движка в любой момент
```

## Команды

|Команда|Описание|
|---|---|
|`version`|Вывести версию лаунчера|
|`help [command]`|Справка по лаунчеру или одной команде|
|`where`|Показать папку данных|
|`fetch`|Список доступных релизов движка и их статус установки|
|`install <version>`|Скачать, проверить и установить версию движка|
|`ls`|Список установленных версий движка|
|`check <version>`|Проверка целостности установленной версии|
|`new <name> <version>`|Создать новый инстанс (мир)|
|`instances`|Список существующих инстансов|
|`launch <instance>`|Запустить инстанс|
|`rmi <instance>`|Удалить инстанс|
|`rm <version>`|Удалить версию движка (запрещено, пока её использует инстанс)|

### Передача аргументов движку

Всё, что указано после `--`, передаётся движку как есть:

```bash
launcher launch my_instance -- --headless
launcher launch my_instance -- --sps 60
```

Синтаксис объясняет `launcher help launch`, а полный список флагов движка — `--help` самого движка.

## Где хранятся данные

Всё лежит в одной папке в профиле пользователя:

|ОС|Путь|
|---|---|
|Windows|`%LOCALAPPDATA%\ChromaForge-Launcher`|
|Linux|`~/.local/share/ChromaForge-Launcher`|
|macOS|`~/Library/Application Support/ChromaForge-Launcher`|

```text
ChromaForge-Launcher/
├── cores/
│   ├── chromaforge-v0.4.1/     установленный движок
│   └── lock.toml               реестр установленных версий
├── instances/
│   ├── my_instance/               данные мира
│   └── lock.toml               реестр инстансов
└── meta/
    └── checksums/              SHA-256 суммы установленных файлов
```

**Портативный режим:** положите пустой `settings.toml` рядом с исполняемым
файлом — и всё вышеперечисленное будет храниться рядом с ним. `launcher where`
всегда показывает активную папку данных.

> [!NOTE]
> На данный момент `settings.toml` не используется для хранения каких-либо данных.

## Коды выхода

|Код|Значение|
|---|---|
|`0`|Успех|
|`1`|Ошибка использования (аргументы, «версия не установлена» и т.п.)|
|`2`|Сбой операции|

## Сборка из исходников

Нужны Java 17 и Maven:

```bash
mvn -B package
```

Результат — `target/ChromaForge-Launcher-0.1.0.jar`. Собрать app-image:

```bash
jpackage --input target --name ChromaForge-Launcher \
  --main-jar ChromaForge-Launcher-0.1.0.jar --main-class chromaforge.launcher.Launcher \
  --type app-image --dest build
```

На Windows внутри `build/ChromaForge-Launcher` уже лежит готовый `ChromaForge-Launcher.exe`.

---

Автор: [Ezhovnik](https://github.com/Ezhovnik) · Движок: [ChromaForge-v2](https://github.com/Ezhovnik/ChromaForge-v2)
