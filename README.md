# Реализация RxJava

Проект представляет собой пользовательскую реализацию RxJava

## Основные сущности

1. Интерфейс Observer
2. Класс Observable (Основной класс для работы с потоками данных)
3. Интерфейс Disposable (позволяет отменять подписку)

## Планировщики

В проекте реализованы следующие планировщики:

1. IOThreadScheduler (аналог Schedulers.io(), использующий CachedThreadPool).
2. ComputationScheduler (аналог Schedulers.computation(), использующий FixedThreadPool).
3. SingleThreadScheduler (аналог Schedulers.single(), использующий один поток).

## Операторы

1. Map (преобразует поток данных)
2. Filter (отфильтровывает ненужные элементы)
3. FlatMap (преобразует элементы в новый Observable)