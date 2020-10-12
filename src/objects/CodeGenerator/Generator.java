package objects.CodeGenerator;

/**
 * Генератор кода на языке Ассемблера на основе обхода дерева, получившегося в результате синтаксического разбора
 * исходного текста программы.
 *
 * Операторы Ассемблера (из методички):
 *
 * "m" обозначает название ячейки памяти
 * LOAD m   : c(m) -> сумматор                              - загружает значение из ячейки m в сумматор
 * ADD m    : c(сумматор) + c(m) -> сумматор                - складывает значение из сумматора с значением в ячейке m и помещает результат в сумматор
 * SUB m    : c(сумматор) - c(m) -> сумматор                - вычитает из значения в сумматоре значение в ячейке m и помещает результат в сумматор
 * MPY m    : c(сумматор) * c(m) -> сумматор                - перемножает значение из сумматора с значением в ячейке m и помещает результат в сумматор
 * DIV m    : с(сумматор) / c(m) -> сумматор                - делит значение из сумматора на значение в ячейке m и помещает результат в сумматор
 * STORE m  : c(сумматор) -> m                              - записывает значение из сумматора в ячейку m
 * AND m    : c(сумматор) AND c(m) -> сумматор              - совершает побитовую операцию И над значением из сумматора и значением в ячейке m и помещает результат в сумматор
 * OR m     : c(сумматор) OR c(m) -> сумматор               - совершает побитовую операцию ИЛИ над значением из сумматора и значением в ячейке m и помещает результат в сумматор
 * XOR m    : c(сумматор) XOR c(m) -> сумматор              - совершает побитовую операцию ИСКЛЮЧАЮЩЕЕ ИЛИ над значением из сумматора и значением в ячейке m и помещает результат в сумматор
 * NOT      : NOT c(сумматор) -> сумматор                   - совершает побитовую операцию НЕ над значением в сумматоре и помещает результат в сумматор
 * CMP m    : if c(сумматор) == c(m) then FLAG = 0
 *            else c(сумматор) > c(m) then FLAG = 1
 *            else FLAG = -1
 * - сравнивает значения в сумматоре со значением в ячейке m, и в случае их равенства устанавливает флаг равным 0, если значение в сумматоре больше - равным 1, иначе - -1.
 * JE m     : if FLAG == 0 then JUMP c(m)                   - если флаг равен 0 (результат сравенения - равно), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JG m     : if FLAG == 1 then JUMP c(m)                   - если флаг равен 1 (результат сравнения - больше), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JL m     : if FLAG == -1 then JUMP c(m)                  - если флаг равен -1 (результат сравнения - меньше), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JMP m    : JUMP c(m)                                     - совершает безусловную передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JMP
 *
 * "=m" обозначает численное значение
 * LOAD =m  : m -> сумматор                                 - помещает значение m в сумматор
 * ADD =m   : c(сумматор) + m -> сумматор                   - складывает значение из сумматора с значением m и помещает результат в сумматор
 * SUB =m   : c(сумматор) - m -> сумматор
 * MPY =m   : c(сумматор) * m -> сумматор                   - перемножает значение из сумматора с значением m и помещает результат в сумматор
 * DIV =m    : с(сумматор) / m -> сумматор
 * AND =m   : c(сумматор) AND m -> сумматор
 * OR =m    : c(сумматор) OR m -> сумматор
 * XOR =m   : c(сумматор) XOR m -> сумматор
 * CMP =m   : if c(сумматор) == m then FLAG = 1 else FLAG = 0
 *
 * Операторы исходного языка и соотносящийся им код Ассемблера:
 * a + b        -> ... b; STORE $b; LOAD $a; ADD $b;
 * a * b        -> ... b; STORE $b; LOAD $a; MUL $b;
 * a - b        -> ... b; STORE $b; LOAD $a; SUB $b;
 * a / b        -> ... b; STORE $b; LOAD $a; DIV $b;
 * a AND b      -> ... b; STORE $b; LOAD $a; AND $b;
 * a OR b       -> ... b; STORE $b; LOAD $a; OR $b;
 * a XOR b      -> ... b; STORE $b; LOAD $a; XOR $b;
 * NOT a        -> ... a; LOAD $a; NOT;
 */
public class Generator {
}
