# ----------------------------------------------------
# ARTEFACT PLUGIN DOCUMENTATION
# ----------------------------------------------------

## Plugin Overview

A plugin for artifacts that reward donation currency from the PlayerPoints plugin.

## Commands

| Command | Usage | Description | Permission |
| :--- | :--- | :--- | :--- |
| /art help | /art help | Displays the plugin's help messages. | None (default) |
| /art open | /art open | Opens the Artefact exchange menu (GUI). | None (default) |
| /art give | /art give <name> <player> <amount> | Gives a specific artefact to an online player. | artefacts.admin.give |
| /art reload | /art reload [all|config|menu] | Reloads specific parts or the entire plugin. | artefacts.admin.reload |
| /art reload all | /art reload all | Fully reloads the plugin (config, menu, tasks). | artefacts.admin.reload |
| /art reload config | /art reload config | Reloads config.yml and artefact templates. | artefacts.admin.reload |
| /art reload menu | /art reload menu | Reloads menu.yml (the exchange menu configuration). | artefacts.admin.reload |

---

## Configuration File: config.yml

### 1. Help Messages (help-message)
# The 'help-message' section defines the lines that can be displayed to the player for reference.

| Message Type | Description |
| :--- | :--- |
| Menu Access Message | Information on how to access the Artefact exchange menu. |
| Help Reference Message | Information on how to get plugin assistance. |
| Reload Function Message | Information regarding the configuration reload feature. |
| Give Function Message | Information regarding the artefact spawning feature. |

---

### 2. Economy Settings (points-per-value)
# Defines how the Artefact's base Value is converted into the game currency (PlayerPoints).

| Setting | Value | Description |
| :--- | :--- | :--- |
| **points-per-value** | 100 | Multiplier. Formula: Price (Points) = Value × points-per-value. |
| **Example** | | If Value is 5.25, the price will be 525 points. |

---

### 3. Artefact Growth Settings (artefact-growth)
# Controls the background task that automatically increases the value of Artefacts over time.

| Setting | Value | Description |
| :--- | :--- | :--- |
| **enabled** | true/false | Enables or disables the automatic value growth task. |
| **interval-minutes** | 60 | The interval (in minutes) between value increases. |
| **value-increase** | 0.10 | The amount the base Value (%chance%) is increased by per interval. |
| **max-value** | 15.00 | The maximum Value limit the Artefact can reach. Use -1.0 to disable the limit. |

---

### 4. Artefact Templates (artefacts)
# Definitions for all types of Artefacts available in the plugin.

| Parameter | Description |
| :--- | :--- |
| **<ID>** | Unique identifier for the Artefact (used for internal references). |
| **material** | The Minecraft item type that will be used as the Artefact. |
| **display_name** | The item's display name. |
| **lore** | A list of descriptive lines for the item. |
| **value.min/max** | The range from which the initial Value (%chance%) is randomly selected when the Artefact is created. |

**LORE Placeholders:**
* **%chance%**: The Artefact's current base Value.
* **%cost%**: The Artefact's current Price in PlayerPoints.

---
---
---

### 🇷🇺 Русская Версия

```markdown
# ----------------------------------------------------
# ДОКУМЕНТАЦИЯ ПЛАГИНА ARTEFACT
# ----------------------------------------------------

## Обзор Плагина

Плагин для артефактов, которые можно обменять на донат-валюту из плагина PlayerPoints.

## Команды

| Команда | Использование | Описание | Разрешение (Permission) |
| :--- | :--- | :--- | :--- |
| /art help | /art help | Отображает справочные сообщения плагина. | Нет (по умолчанию) |
| /art open | /art open | Открывает меню обмена Артефактов (GUI). | Нет (по умолчанию) |
| /art give | /art give <name> <player> <amount> | Выдает конкретный артефакт онлайн-игроку. | artefacts.admin.give |
| /art reload | /art reload [all|config|menu] | Перезагружает определенные части или весь плагин. | artefacts.admin.reload |
| /art reload all | /art reload all | Полностью перезагружает плагин (конфиг, меню, задачи). | artefacts.admin.reload |
| /art reload config | /art reload config | Перезагружает config.yml и шаблоны артефактов. | artefacts.admin.reload |
| /art reload menu | /art reload menu | Перезагружает menu.yml (конфигурацию меню обмена). | artefacts.admin.reload |

---

## Конфигурационный Файл: config.yml

### 1. Сообщения помощи (help-message)
# Секция 'help-message' определяет сообщения, которые могут быть отображены игроку для справки.

| Тип сообщения | Описание |
| :--- | :--- |
| Сообщение об открытии меню | Информация о доступе к меню обмена Артефактами. |
| Справочное сообщение | Информация о том, как получить помощь. |
| Сообщение о перезагрузке | Информация о функции перезагрузки конфигурации. |
| Сообщение о выдаче | Информация о функции выдачи Артефактов. |

---

### 2. Настройка экономики (points-per-value)
# Определяет, как базовая Ценность Артефакта переводится в игровую валюту (PlayerPoints).

| Настройка | Значение | Описание |
| :--- | :--- | :--- |
| **points-per-value** | 100 | Множитель. Формула: Цена (поинты) = Ценность (Value) × points-per-value. |
| **Пример** | | Если Ценность 5.25, цена будет 525 поинтов. |

---

### 3. Настройки роста ценности (artefact-growth)
# Контролирует фоновую задачу, которая автоматически повышает ценность Артефактов с течением времени.

| Настройка | Значение | Описание |
| :--- | :--- | :--- |
| **enabled** | true/false | Включает или отключает задачу автоматического роста ценности. |
| **interval-minutes** | 60 | Интервал (в минутах) между повышениями ценности. |
| **value-increase** | 0.10 | Значение, на которое увеличивается базовая Ценность (%chance%) за один интервал. |
| **max-value** | 15.00 | Максимальный предел Ценности, до которого может дорасти Артефакт. Используйте -1.0 для отключения лимита. |

---

### 4. Шаблоны Артефактов (artefacts)
# Определения всех типов Артефактов, доступных в плагине.

| Параметр | Описание |
| :--- | :--- |
| **<ID>** | Уникальный идентификатор Артефакта (используется для внутренних ссылок). |
| **material** | Тип предмета Minecraft, который будет использоваться как Артефакт. |
| **display_name** | Отображаемое имя предмета. |
| **lore** | Список строк описания предмета. |
| **value.min/max** | Диапазон, из которого случайно выбирается начальная Ценность (%chance%) при создании Артефакта. |

**Плейсхолдеры в LORE:**
* **%chance%**: Текущая базовая Ценность Артефакта (Value).
* **%cost%**: Текущая Цена Артефакта в PlayerPoints.
