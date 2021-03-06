# Компилятор псевдо-Ассемблер кода

Данная программа переводит текст, написанный на проприетарном языке программирования, в текст кода на языке упрощённого Ассемблера.
Проприентарный язык имеет следующие возможности:
- Инициализация переменной (i = 5; j = i;)
- Арифметические операции (i = 2 * 3; j = (9 / 3) + 2;)
- Логические операции (i = 15; j = 11; z = i AND j;)
- Оператор условного ветвления (if ( 5 < 10) { a = 7; } )

Программа состоит из 4 составляющих:
- Лексический анализатор (разбор исходного текста на отдельные части - лексемы)
- Синтаксический анализатор (представление лексем в виде дерева)
- Генератор кода и оптимизатор (проход по дереву и генерация кода)
- Пользовательский интерфейс (графическое общение с пользователем, вывод промежуточных шагов, ошибок и Ассемблер-кода)

В программе используются следующие технологии:
- хеш-таблица с открытой адресацией
- JavaFx

# Пример использования
Имеем исходный код:
```
a = 10;
b = -5;
IF (a < b){
c = a;
}
ELSE{
c = b;
}
OUT c;
```

Программа вернёт следующий код на Ассемблере:
```
LOAD 10;
CMP -5;
JS BLOCK_1;
LOAD BLOCK_1_end;
ADD 1;
STORE $0;
JMP $0;
LOAD 10;
OUT;
```

![Исходный код](https://i.imgur.com/bu0teHL.png)
![Выходной код](https://i.imgur.com/USHfZYI.png)
