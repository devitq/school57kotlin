# Задания

## Задание 1. Поиск ошибок в логах
Найти все строки со словом ERROR во всех логах в каталоге logs (включая logs/old) и сохранить их в файл errors.txt в корне проекта.

```bash
grep -Ri "ERROR" logs > errors.txt
```

## Задание 2. Архивация старых логов
Создать каталог archived/ в корне проекта и переместить туда все файлы из logs/old.

```bash
mkdir -p archived && mv logs/old/* archived/ 2>/dev/null || true
```

## Задание 3. Подсчёт размера логов
Посчитать общий размер каталога logs и записать результат в logs_size.txt.

```bash
du -sh logs | awk '{ print $1 }' > logs_size.txt
```

## Задание 4. Нахождение самого большого лог-файла
Найти самый большой файл в каталоге logs (без учёта подкаталогов) и записать его имя в файл biglog.txt.

```bash
find logs -maxdepth 1 -type f -exec du -sh {} \; | sort -h -r | head -1 | awk '{ print $2 }' > biglog.txt
```

## Задание 5. Подсчёт количества логов
Подсчитать количество файлов с расширением .log во всём каталоге logs и сохранить результат в log_count.txt.

```bash
find logs -type f -name '*.logs' | wc -l > log_count.txt
```

## Задание 6. Поиск конфигурационных параметров
Найти во всех config/*.conf строки, содержащие слово "host", и записать в host_params.txt.

```bash
find config -name '*.conf' -type f -exec grep -i "host" {} \; > host_params.txt
```

## Задание 7. Создание резервного архива конфигов
Создать zip-архив config_backup.zip, содержащий все файлы из config/.

```bash
zip -r config_backup.zip config
```

## Задание 8. Создание общего резервного архива
Создать zip-архив project_backup.zip, куда включить:
- все *.conf из config/
- все *.log из logs (включая old/)
- файл errors.txt (если он есть)

```bash
zip project_backup.zip errors.txt logs/**/*.logs config/*.conf
```

## Задание 9. Очистка пустых строк в логах
Создать файл cleaned_app.log, содержащий содержимое app.log без пустых строк.

```bash
cat logs/app.logs | sed -r '/^\s*$/d' > cleaned_app.log
```

## Задание 10. Подсчёт количества строк в каждом конфиге
Создать файл conf_stats.txt с вида:
app.conf 12  
db.conf 8  
(где число — количество строк в файле)

```bash
find config -type f -exec wc -l {} \; | awk '{ print $2 " " $1 }' > conf_stats.txt
```


## Задача: Создать собственный архиватор
Напишите функцию, которая:

1. Принимает на вход **каталог с файлами** (например, `project_data/`) и путь к архиву (`archive.zip`).
2. Создает **ZIP-архив** всех файлов внутри каталога, сохраняя структуру подкаталогов.
3. Использует классы `FileInputStream`, `FileOutputStream` и `java.util.zip.ZipOutputStream`.
4. Обеспечивает корректное закрытие потоков и обработку исключений.
5. Добавить фильтр по расширению файлов, чтобы архивировать только `.txt` или `.log`.

### Требования:
- Не использовать сторонние библиотеки для архивирования (только стандартный API).
- Программа должна выводить в консоль список добавляемых файлов и их размер.