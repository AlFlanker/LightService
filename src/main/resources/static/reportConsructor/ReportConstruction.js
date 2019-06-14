'use strict';
/*====================================================================================================================*/
/* Описание: Основная функция приложения.
 * Входящие данные: -
 * Исходящие данные: -
 */
$(document).ready(function () {
    let formArea = $("#formArea"); // область формы карточки
    let datetimePickerOne = $('#datetimepicker1'); // выбор начала периода
    let datetimePickerTwo = $('#datetimepicker2'); // выбор окончания периода
    let datetimeErazerOne = $('#datetimeeraser1'); // очистка выбора первого периода
    let datetimeErazerTwo = $('#datetimeeraser2'); // очистка выбора второго периода
    let chartTypePicker = $('#chartTypePicker'); // выбор типа графика
    let devTypePicker = $('#devTypePicker'); // выбор типа устройства
    let cpGroupPicker = $('#cpGroupPicker'); // выбор группы устройств КП/всех устройств КП
    let reportCPMulti = $('#reportCPMulti'); // переключатель групп устройств
    let singleCPDevLabel = $('#singleCPDevLabel');
    let multiCPDevLabel = $('#multiCPDevLabel');
    let devPicker = $('#devPicker'); // выбор устройства
    let reportChartMulti = $('#reportChartMulti'); // выбор манеры отображения графика: в одном или в нескольких
    let paramPickerDiv = $('#paramPickerDiv'); // блок выбора параметров
    let paramPicker = $('#paramPicker'); // выбор параметров
    let datetimeChoosing = $('#datetimeChoosing'); // блок выбора времени
    let choiceManualLabel = $('#choiceManualLabel'); // заголовок выбора времени вручную
    let devTypePickerDiv = $('#devTypePickerDiv'); // блок выбора типа устройства
    let devPickerDiv = $('#devPickerDiv'); // блок выбора наименования устройства
    let choiceDayLabel = $('#choiceDayLabel'); // заголовок выбора суточного диапазона
    let choiceWeekLabel = $('#choiceWeekLabel'); // заголовок выбора недельного диапазона
    let choiceMonthLabel = $('#choiceMonthLabel'); // заголовок выбора месячного диапазона
    let errorBlock = $("#errorBlock"); // блок сообщений об ошибках
    /*--------------------------------------------------------------------------------------------------------------------*/
    $.fn.select2.defaults.set('amdBase', 'select2/'); // общие глобальные зависимости на случай использования AMD или CommonJS
    $.fn.select2.defaults.set('amdLanguageBase', 'select2/i18n/'); // языковые глобальные зависимости на случай использования AMD или CommonJS
    let chartTypePickerData = [{id: 0, text: "Таблица"}, // варианты выбора типа отчёта
        {id: 1, text: "Линейный график"},
        {id: 2, text: "График со столбцами"},
        {id: 3, text: "Круговая диаграмма"}];
    chartTypePicker.select2({ // объявление селекторов select2
        language: "ru",
        placeholder: "Выберите тип графика",
        minimumResultsForSearch: Infinity,
        data: chartTypePickerData
    });
    devTypePicker.select2({
        language: "ru",
        placeholder: "Выберите тип объекта"
    });
    cpGroupPicker.select2({
        language: "ru",
        placeholder: "Выберите устройство в группе"
    });
    devPicker.select2({
        language: "ru",
        placeholder: "Выберите объект"
    });
    paramPicker.select2({
        language: "ru",
        placeholder: "Выберите параметры",
        allowClear: true
    });

    $('label').each(function () {
        if (this.id === "choiceManualLabel") {
            this.classList.add("active");
            this.control.checked = true;
        }
    });

    /* Описание: Относительно "универсальный" метод GET-запроса к серверу, основанный на "обещаной" функции.
     * Входящие данные: строка запроса к серверу
     * Исходящие данные: ответ сервера на запрос
     */
    function ReportDataGetter(requestString) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: requestString,
                type: 'GET',
                contentType: "application/json",
                dataType: 'json',
                success: function (response) {
                    return resolve(response);
                },
                error: function (error) {
                    return reject(error);
                }
            });
        });
    }

    /* Описание: Реализация функции получения данных с сервера для типов устройств, параметров и самих устройств.
     * Входящие данные: строка запроса к серверу -> результат первого запроса
     * Исходящие данные: данные сервера о запрошенных типах устройств и параметрах -> список устройств -> вызов функции обработки результата
     */
    let urlType = "/report/type"; // путь для получения списка типов и параметров
    let urlObj = "/report/objects"; // путь для получения списка устройств
    let urlState = "/report/allstates";  // путь для получения статистики
    ReportDataGetter(urlType)
        .then( // первый запрос (верхнего уровня) - типы устройств и параметры
            function (typeResponse) {
                ReportDataGetter(urlObj)
                    .then( // второй запрос (вложеный) - устройства
                        function (objResponse) {
                            let devTypes = []; // исходные типы устройств и параметров
                            let devObjects = []; // исходный список устройств
                            let typePickerData = []; // данные для селектора типов устройств
                            let paramPickerData = []; // данные для селектора типов параметров
                            let devPickerData = []; // данные для селектора устройств
                            let typePickerAlias = ""; // удобочитаемая маска типов устройств
                            let paramPickerAlias = ""; // удобочитаемая маска параметров устройств
                            devTypes = typeResponse;
                            let newTypeOption = [typePickerData.length - 1];
                            for (let obj = 0; obj < typeResponse.length; obj++) { // начальное заполнение формы - типы устройств
                                if (typeResponse[obj].type === "lamp") { // замена известных типов устройств на удобочитаемые
                                    typePickerAlias = "Лампа";
                                } else if (typeResponse[obj].type === "controlpoint") {
                                    typePickerAlias = "КП";
                                } else { // если тип неизвестен, писать его как есть
                                    typePickerAlias = typeResponse[obj].type;
                                }
                                typePickerData.push({id: obj, text: typePickerAlias}); // формирование массива вариантов выбора select2 для типов устройств
                                newTypeOption[obj] = new Option(typePickerData[obj].text, typePickerData[obj].id, false, false); // добавление вариантов типов устройств в массив параметров
                            }
                            devTypePicker.append(newTypeOption).trigger('change'); // обновление данные селектора select2 для типов устройств
                            let validParamCounter = 0; // счётчик заранее известных параметров для последовательной индексации в селекторе
                            let newParamOption = [paramPickerData.length - 1]; // очередной параметр для заполнения
                            for (let param = 0; param < typeResponse[0].fields.length; param++) { // начальное заполнение формы - устройства первого по порядку типа
                                if (typeResponse[0].fields[param] === "i_ac" || typeResponse[0].fields[param] === "v_ac" || typeResponse[0].fields[param] === "temperature" || typeResponse[0].fields[param] === "brightness" || typeResponse[0].fields[param] === "state") { // !!! неизвестные типов параметров игнорируются
                                    switch (typeResponse[0].fields[param]) { // интерпретация известных типов параметров для удобочитаемости
                                        case "i_ac":
                                            paramPickerAlias = "Сила тока";
                                            break;
                                        case "v_ac":
                                            paramPickerAlias = "Напряжение";
                                            break;
                                        case "temperature":
                                            paramPickerAlias = "Температура";
                                            break;
                                        case "brightness":
                                            paramPickerAlias = "Яркость";
                                            break;
                                        case "state":
                                            paramPickerAlias = "Состояние";
                                            break;
                                    }
                                    paramPickerData.push({id: validParamCounter, text: paramPickerAlias}); // добавление интерпретированного параметра в массив параметров селектора select2
                                    newParamOption[validParamCounter] = new Option(paramPickerData[validParamCounter].text, paramPickerData[validParamCounter].id, false, false); // перевод массива параметров в массив опций для селектора
                                    validParamCounter = validParamCounter + 1; // суффиксный инкремент счётчика известных параметров
                                }
                            }
                            paramPicker.append(newParamOption).trigger('change'); // обновление селектора select2 для выбора
                            function GetPropPick(callback) { // выбранный тип устройства
                                if (devTypePicker[0][devTypePicker.val()].innerText === "Лампа") { // злоебучие интерпретации
                                    return callback("lamp");
                                } else if (devTypePicker[0][devTypePicker.val()].innerText === "КП") {
                                    return callback("controlpoint");
                                } else {
                                    return callback(devTypePicker[0][devTypePicker.val()].innerText);
                                }
                            }
/*--------------------------------------------------------------------------------------------------------------------*/
                            devObjects = objResponse;
                            let curTypePick = GetPropPick(function (result) {
                                return result;
                            });
                            let newDevOption = [devPickerData.length - 1];
                            for (let i = 0; i < objResponse.length; i++) { // проход по исходному массиву
                                if (objResponse[i].type === curTypePick) { // если выбранный тип
                                    for (let dev = 0; dev < objResponse[i].fields.length; dev++) { // ролходить по устройствам типа
                                        //devPickerData.push({id: dev, text: objResponse[i].fields[dev][1]});
                                        devPickerData.push({id: dev, text: objResponse[i].fields[dev][2] + " (EUI:" + objResponse[i].fields[dev][1] + " КП:" + objResponse[i].fields[dev][3] + ")"});
                                        newDevOption[dev] = new Option(devPickerData[dev].text, devPickerData[dev].id, false, false);
                                    }
                                }
                            }
                            devPicker.append(newDevOption).trigger('change');
/*--------------------------------------------------------------------------------------------------------------------*/
                            // переключение типов отчёта
                            chartTypePicker.on('select2:select', function () {
                                FormModeSwitcher(chartTypePicker, devTypePickerDiv, devTypePicker, devPickerDiv, devPicker, reportChartMulti, reportCPMulti, singleCPDevLabel, multiCPDevLabel, cpGroupPicker, paramPickerDiv, paramPicker, choiceManualLabel, choiceDayLabel, choiceWeekLabel, choiceMonthLabel, datetimeChoosing, datetimePickerOne, datetimePickerTwo); // переключение формы в режим таблицы
                            });

                            // переключение выбора типа устройства
                            devTypePicker.on('select2:select', function () {

                                if (devTypePicker[0][devTypePicker.val()].innerText === "КП") {
                                    cpGroupPicker.prop('disabled', true);
                                    document.getElementById("singleCPDevLabel").removeAttribute("disabled");
                                    document.getElementById("multiCPDevLabel").removeAttribute("disabled");
                                    reportCPMulti.removeClass("btn-group-inactive");
                                    $('label').each(function () {
                                        if (this.id === "multiCPDevLabel") {
                                            this.classList.add("active"); // снятие выделения пункта группы кнопок
                                            this.control.checked = true; // снятие отметки о выборе пункта
                                        }
                                    });
                                } else {
                                    cpGroupPicker.prop('disabled', true);
                                    document.getElementById("singleCPDevLabel").setAttribute("disabled", "disabled");
                                    document.getElementById("multiCPDevLabel").setAttribute("disabled", "disabled");
                                    reportCPMulti.addClass("btn-group-inactive");
                                    $('label').each(function () {
                                        if (this.id === "singleCPDevLabel" || this.id === "multiCPDevLabel") {
                                            this.classList.remove("active"); // снятие выделения пункта группы кнопок
                                            this.control.checked = false; // снятие отметки о выборе пункта
                                        }
                                    });
                                }
                                let typePick = GetPropPick(function (result) {
                                    return result;
                                });
                                paramPickerData = []; // очищение списка параметров перед новым заполнением
                                devPickerData = []; // очищение списка устройств перед новым заполнением
                                paramPicker.empty(); // очищение селектора параметров перед новым заполнением
                                devPicker.empty(); // очищение селектора устройств перед новым заполнением
                                let validParamCounter = 0; // счётчик "правильных" (принятых к отчёту) параметров
                                // переопределение списка параметров после нового выбора типа устройства
                                for (let typpie = 0; typpie < devTypes.length; typpie++) { // проход по типам
                                    if (typePick === devTypes[typpie].type) { // и отбор интересующего
                                        for (let param = 0; param < devTypes[typpie].fields.length; param++) { // отбор параметров
                                            if (devTypes[typpie].fields[param] === "i_ac" || devTypes[typpie].fields[param] === "v_ac" || devTypes[typpie].fields[param] === "temperature" || devTypes[typpie].fields[param] === "brightness" || devTypes[typpie].fields[param] === "state") { // !!! отсеивание неизвестных (пока не требуемых) типов параметров
                                                switch (devTypes[typpie].fields[param]) { // замена известных типов параметров
                                                    case "i_ac":
                                                        paramPickerAlias = "Сила тока";
                                                        break;
                                                    case "v_ac":
                                                        paramPickerAlias = "Напряжение";
                                                        break;
                                                    case "temperature":
                                                        paramPickerAlias = "Температура";
                                                        break;
                                                    case "brightness":
                                                        paramPickerAlias = "Яркость";
                                                        break;
                                                    case "state":
                                                        paramPickerAlias = "Состояние";
                                                        break;
                                                }
                                                paramPickerData.push({id: validParamCounter, text: paramPickerAlias});
                                                validParamCounter = validParamCounter + 1; // суффиксный инкремент
                                            }
                                        }
                                    }
                                }
                                for (let typpie = 0; typpie < devObjects.length; typpie++) { // переопределение списка устройств после нового выбора типа устройств
                                    if (typePick === devObjects[typpie].type) { // предполагается, что выбор осуществляется из присланного
                                        for (let dev = 0; dev < devObjects[typpie].fields.length; dev++) {
                                            //devPickerData.push({id: dev, text: devObjects[typpie].fields[dev][1]}); // выбор EUI
                                            devPickerData.push({id: dev, text: devObjects[typpie].fields[dev][2] + " (EUI:" + devObjects[typpie].fields[dev][1] + " КП:" + devObjects[typpie].fields[dev][3] + ")"}); // выбор EUI
                                        }
                                    } // но отработать всё равно стоило бы
                                }
                                paramPicker.select2({ // переопределение селектора параметров
                                    language: "ru",
                                    placeholder: "Выберите параметры",
                                    allowClear: true,
                                    data: paramPickerData
                                });
                                devPicker.select2({ // переопределение селектора устройств
                                    language: "ru",
                                    placeholder: "Выберите объект",
                                    data: devPickerData
                                });
                                devTypePickerDiv.removeClass('not-valid-object'); // отключение сигнальной подсветки блока выбора типа устройств
                                devPickerDiv.removeClass('not-valid-object'); // отключение сигнальной подсветки блока выбора устройства
                                paramPickerDiv.removeClass('not-valid-object'); // отключение сигнальной подсветки блока выбора параметров
                            });
                            // переключение выбора устройства
                            devPicker.on('select2:select', function () { // переключение типа устройства
                                devPickerDiv.removeClass('not-valid-object'); // отключение сигнальной подсветки блока выбора устройства
                            });
                            // переключение выбора параметров
                            paramPicker.on('select2:select', function () { // переключение типа устройства
                                paramPickerDiv.removeClass('not-valid-object'); // отключение сигнальной подсветки блока выбора устройства
                            });
/*--------------------------------------------------------------------------------------------------------------------*/
                            // установки выбора времени
                            datetimePickerOne.datetimepicker({ // объявление и настройка выбора начала периода
                                locale: "ru", // локализация
                                showClose: true, // отображение кнопки закрытия в окне выбора времени
                                icons: { // переопределение иконок DateTimePicker
                                    time: 'fa fa-clock-o',
                                    date: 'fa fa-calendar',
                                    up: 'fa fa-chevron-up',
                                    down: 'fa fa-chevron-down',
                                    previous: 'fa fa-chevron-left',
                                    next: 'fa fa-chevron-right',
                                    today: 'fa fa-crosshairs',
                                    clear: 'fa fa-trash-o',
                                    close: 'fa fa-times'
                                }
                            });
                            datetimePickerTwo.datetimepicker({ // объявление и настройка выбора окончания периода
                                locale: "ru", // локализация
                                showClose: true, // отображение кнопки закрытия в окне выбора времени
                                icons: { // переопределение иконок DateTimePicker
                                    time: 'fa fa-clock-o',
                                    date: 'fa fa-calendar',
                                    up: 'fa fa-chevron-up',
                                    down: 'fa fa-chevron-down',
                                    previous: 'fa fa-chevron-left',
                                    next: 'fa fa-chevron-right',
                                    today: 'fa fa-crosshairs',
                                    clear: 'fa fa-trash-o',
                                    close: 'fa fa-times'
                                },
                                useCurrent: false // выбор окончания периода - ведомый
                            });
                            datetimePickerOne.on("dp.change", function (e) { // установка минимальной даты для конечной даты
                                datetimePickerTwo.data("DateTimePicker").minDate(e.date);
                            });
                            datetimePickerTwo.on("dp.change", function (e) { // установка максимальной даты для начальной даты
                                datetimePickerOne.data("DateTimePicker").maxDate(e.date);
                            });
                            datetimeErazerOne.click(function () { // поведение кнопки очистки начальной даты
                                datetimePickerOne.data("DateTimePicker").clear();
                                datetimePickerTwo.data("DateTimePicker").clear();
                            });
                            datetimeErazerTwo.click(function () { // поведение кнопки очистки конечной даты
                                datetimePickerTwo.data("DateTimePicker").clear();
                            });
/*--------------------------------------------------------------------------------------------------------------------*/
                            // установки поведения формы при выбранном по умолчанию режиме таблицы
                            FormModeSwitcher(chartTypePicker, devTypePickerDiv, devTypePicker, devPickerDiv, devPicker, reportChartMulti, reportCPMulti, singleCPDevLabel, multiCPDevLabel, cpGroupPicker, paramPickerDiv, paramPicker, choiceManualLabel, choiceDayLabel, choiceWeekLabel, choiceMonthLabel, datetimeChoosing, datetimePickerOne, datetimePickerTwo);

                            $('label').each(function () {
                                if (this.id === "singleCPDevLabel" || this.id === "multiCPDevLabel") {
                                    this.classList.remove("active");
                                    this.control.checked = false;
                                }
                            });

/*--------------------------------------------------------------------------------------------------------------------*/
                            // переключение группы кнопок выбора периода
                            $('.datetimes input[type=radio]').on('change', function () {
                                if (this.checked) { // если выбран
                                    if (this.value === "choiceDay" || this.value === "choiceWeek" || this.value === "choiceMonth") { // фиксированный период
                                        datetimePickerOne.data("DateTimePicker").disable(); // выбор времени блокируется
                                        datetimePickerTwo.data("DateTimePicker").disable();
                                    } else { // иначе
                                        datetimePickerOne.data("DateTimePicker").enable(); // выбор времени доступен
                                        datetimePickerTwo.data("DateTimePicker").enable();
                                    }
                                } // при любом изменении - отключать сигнальную подсветку
                                datetimePickerOne.removeClass('not-valid-object');
                                datetimePickerTwo.removeClass('not-valid-object');
                                datetimeChoosing.removeClass('not-valid-object');
                            });

                            $('.cpdevs input[type=radio]').on('change', function () {
                                if (this.checked) {
                                    if (this.value === "multiChart") {
                                        cpGroupPicker.prop('disabled', true);
                                    } else { // иначе
                                        cpGroupPicker.prop('disabled', false);
                                    }
                                }
                            });

/*====================================================================================================================*/
                            /* Описание: Построение графиков и отрисовка отчёта.
                             * Входящие данные: выбранный массив показаний и данные об устройствах.
                             * Исходящие данные: форматированый вывод выбранных данных.
                             */
                            $("#showChart").click(function () {
                                ShowResultProgressBar(); // показ загрузочной крутилки до того, как всё загрузилось
                                let paramPickerArray = [];
                                formArea.removeClass('form-ui-style'); // очищение стилей области построения графика
                                formArea.empty().html('<div id="reportArea"></div>'); // возврат содержимого области отчёта к значению по умолчанию
                                let reportArea = $("#reportArea"); // получение сброшенного по умолчанию объекта для построения графика
                                for (let k = 0; k < paramPicker[0].selectedOptions.length; k++) { // заполнение выбранных значений параметров по идентификаторам
                                    paramPickerArray[k] = paramPicker[0].selectedOptions[k].innerText;
                                }
/*--------------------------------------------------------------------------------------------------------------------*/
                                // задание метода отрисовки графика
                                let chartmultiplying = $('.chartmultiplying input[type=radio]'); // регулировка метода отрисовки графика
                                let chartMethod = "single"; // метод отрисовки всех линий на одном графике по умолчанию
                                for (let obj in chartmultiplying) {
                                    if (chartmultiplying.hasOwnProperty(obj)) {
                                        if (chartmultiplying[obj].checked) {
                                            if (chartmultiplying[obj].value === "multiChart") { // выборный метод отрисовки
                                                chartMethod = "multi";
                                            } else {
                                                chartMethod = "single"; // следует гарантировать получение значения по умолчанию
                                            }
                                        }
                                    }
                                }
/*--------------------------------------------------------------------------------------------------------------------*/
                                // получение и форматирование временного диапазона
                                moment.locale('ru');
                                moment.tz.setDefault("Europe/Moscow");
                                let datetimes = $('.datetimes input[type=radio]'); // объект группы радиокнопок
                                let sTime, fTime, sMomentTime, fMomentTime, sConvertedTime, fConvertedTime;
                                for (let obj in datetimes) {
                                    if (datetimes.hasOwnProperty(obj)) {
                                        if (datetimes[obj].checked) {
                                            if (datetimes[obj].value === "choiceManual") { // выборный период
                                                sTime = datetimePickerOne.data("DateTimePicker").date();
                                                fTime = datetimePickerTwo.data("DateTimePicker").date();
                                            } else if (datetimes[obj].value === "choiceDay") {
                                                sTime = Date.now() - 86400000; // сутки (24 часа) в миллисекундах 86400000
                                                fTime = Date.now();
                                            } else if (datetimes[obj].value === "choiceWeek") {
                                                sTime = Date.now() - 604800000; // неделя (7 суток) в миллисекундах 604800000
                                                fTime = Date.now();
                                            } else if (datetimes[obj].value === "choiceMonth") {
                                                sTime = Date.now() - 2592000000; // месяц (30 суток) в миллисекундах 2592000000
                                                fTime = Date.now();
                                            }
                                        }
                                    }
                                }
                                sMomentTime = moment.utc(sTime).format(); // дата-время начала в формате Moment-объекта
                                fMomentTime = moment.utc(fTime).format(); // дата-время окончания в формате Moment-объекта
                                sConvertedTime = moment(sMomentTime).format('YYYY-MM-DDTHH:mm:ss'); // дата-время начала в формате ISO-8601
                                fConvertedTime = moment(fMomentTime).format('YYYY-MM-DDTHH:mm:ss'); // дата-время окончания в формате ISO-8601
                                function GetDevType(callback) { // обработка ввода типа устройства
                                    if (devTypePicker[0].innerHTML === "" || chartTypePicker[0].innerHTML === null || chartTypePicker[0].innerHTML === undefined) {
                                        return callback("");
                                    } else {
                                        return callback(devTypePicker[0][devTypePicker.val()].innerText);
                                    }
                                }
                                let devType = GetDevType(function (result) {
                                    return result;
                                });
                                function GetDevName(callback) { // обработка ввода наименования устройства
                                    if (devPicker[0].innerHTML === "" || devPicker[0].innerHTML === null || devPicker[0].innerHTML === undefined) {
                                        return callback("");
                                    } else {
                                        let str = devPicker[0][devPicker.val()].innerText;
                                        let res = str.match(/\w{2}-\w{2}-\w{2}-\w{2}-\w{2}-\w{2}-\w{2}-\w{2}/);
                                        return callback(res[0]);
                                        //return callback(devPicker[0][devPicker.val()].innerText);
                                    }
                                }
                                let devName = GetDevName(function (result) {
                                    return result;
                                });
                                function GetChartType(callback) { // обработка ввода типа графика
                                    if (chartTypePicker[0].innerHTML === "" || chartTypePicker[0].innerHTML === null || chartTypePicker[0].innerHTML === undefined) {
                                        return callback("");
                                    } else {
                                        return callback(chartTypePicker[0][chartTypePicker.val()].innerText);
                                    }
                                }
                                let chartType = GetChartType(function (result) {
                                    return result;
                                });
/*--------------------------------------------------------------------------------------------------------------------*/
                                // обработка исключений ввода на форме
                                if (chartType === "" || chartType === null || chartType === undefined) {
                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки, если всё фигня
                                    devTypePickerDiv.addClass('not-valid-object');
                                    FormErrorMessage("NoChartType", errorBlock);
                                } else if (devType === "" || devType === null || devType === undefined) {
                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки, если всё фигня
                                    devTypePickerDiv.addClass('not-valid-object');
                                    FormErrorMessage("NoDevType", errorBlock);
                                } else if (devName === "" || devName === null || devName === undefined) {
                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки, если всё фигня
                                    devPickerDiv.addClass('not-valid-object');
                                    FormErrorMessage("NoDevName", errorBlock);
                                } else if (sTime === null || sTime === undefined || fTime === null || fTime === undefined) { // если время не указано
                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки, если всё фигня
                                    datetimePickerOne.addClass('not-valid-object');
                                    datetimePickerTwo.addClass('not-valid-object');
                                    datetimeChoosing.addClass('not-valid-object');
                                    FormErrorMessage("NoInputPeriod", errorBlock);
                                } else if ((chartType !== "Таблица") && (paramPickerArray.length === 0)) { // если не таблица, а параметры не выбраны
                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки, если всё фигня
                                    paramPickerDiv.addClass('not-valid-object');
                                    FormErrorMessage("NoInputParams", errorBlock);
/*--------------------------------------------------------------------------------------------------------------------*/
                                } else { // если замечаний по заполнению нет
                                    devTypePickerDiv.removeClass('not-valid-object');
                                    devPickerDiv.removeClass('not-valid-object');
                                    paramPickerDiv.removeClass('not-valid-object');
                                    datetimePickerOne.removeClass('not-valid-object');
                                    datetimePickerTwo.removeClass('not-valid-object');
                                    datetimeChoosing.removeClass('not-valid-object');
                                    let deviceID = "";
                                    for (let devtype = 0; devtype < devObjects.length; devtype++) { // цикл по типам с устройствами
                                        switch (devType) {  // !!! обратное преобразование типа устройства
                                            case "Лампа":
                                                devType = "lamp";
                                                break;
                                            case "КП":
                                                devType = "controlpoint";
                                                break;
                                        } // !!! неизвестные типы устройств игнорируются
                                        if (devObjects[devtype].type === devType) { // если тип совпадает с выбранным
                                            for (let dev = 0; dev < devObjects[devtype].fields.length; dev++) { // проходить по устройствам
                                                if (devObjects[devtype].fields[dev][1] === devName) { // сравнение EUI
                                                    deviceID = devObjects[devtype].fields[dev][0]; // а присвоение - ID
                                                }
                                            }
                                        }
                                    }
                                    let paramConverted = ""; // обратно преобразованные имена параметров для отправки на сервер (формат строки с разделителем "запятая")
                                    for (let param = 0; param < paramPickerArray.length; param++) {
                                        switch (paramPickerArray[param]) { // замена известных типов параметров
                                            case "Сила тока":
                                                if (paramConverted === "") {
                                                    paramConverted = "i_ac"
                                                } else {
                                                    paramConverted = paramConverted + "," + "i_ac";
                                                }
                                                break;
                                            case "Напряжение":
                                                if (paramConverted === "") {
                                                    paramConverted = "v_ac"
                                                } else {
                                                    paramConverted = paramConverted + "," + "v_ac";
                                                }
                                                break;
                                            case "Температура":
                                                if (paramConverted === "") {
                                                    paramConverted = "temperature"
                                                } else {
                                                    paramConverted = paramConverted + "," + "temperature";
                                                }
                                                break;
                                            case "Яркость":
                                                if (paramConverted === "") {
                                                    paramConverted = "brightness"
                                                } else {
                                                    paramConverted = paramConverted + "," + "brightness";
                                                }
                                                break;
                                            case "Состояние":
                                                if (paramConverted === "") {
                                                    paramConverted = "state"
                                                } else {
                                                    paramConverted = paramConverted + "," + "state";
                                                }
                                                break;
                                        } // неизвестные параметры игнорируются
                                    }
                                    let requestString = ""; // параметризированный запрос к серверу
                                    if (chartType === "Таблица") { // при выборе таблицы выбираются все параметры
                                        requestString = "?datefrom=" + sConvertedTime + "Z&dateto=" + fConvertedTime + "Z&objectsid=" + deviceID + "&tags=v_ac,i_ac,temperature,brightness,state,date_of_changed" + "&limit=2000000";
                                    } else {
                                        requestString = "?datefrom=" + sConvertedTime + "Z&dateto=" + fConvertedTime + "Z&objectsid=" + deviceID + "&tags=" + paramConverted + ",date_of_changed";
                                    }
/*--------------------------------------------------------------------------------------------------------------------*/
                                    urlState = "/report/allstates" + requestString;
                                    ReportDataGetter(urlState)
                                        .then(
                                            function (response) {
                                                if (response.body[0] === 0 || response.body[0] === null || response.body[0] === undefined || response.body[0].states.length === 0) { // сообщение, если данных нет
                                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки
                                                    FormErrorMessage("DataNotFound", errorBlock);
                                                } else { // а если данные есть...
                                                    let paramLegend = ""; // легенда измеряемых величин
                                                    let axisLegend = ""; // легенда оси измеряемых величин
                                                    let tempArray = []; // временный массив для обработки данных
                                                    let chartOptions = {}; // массив настроек графика
                                                    let dataPoints = []; // данные для построения графиков/диаграмм
                                                    let data = []; // массив данных для отрисовки (параметры настроек графиков/диаграмм и данные для построения)
                                                    let sReportDate = reportDateFormatter(sConvertedTime); // преобразование даты начала выбранного периода в формат для именования файла выгружаемой таблицы
                                                    let fReportDate = reportDateFormatter(fConvertedTime); // преобразование даты завершения выбранного периода в формат для именования файла выгружаемой таблицы
                                                    let reportTitle = 'Отчёт за период с ' + sReportDate + ' по ' + fReportDate; // заголовок отчёта с выбранным периодом
                                                    FormErrorMessage("ClearErrorField", errorBlock); // очищение блока отображения ошибок
/*--------------------------------------------------------------------------------------------------------------------*/
                                                    if (chartType === "Таблица") { // отображение таблицы с данными
                                                        let statesNumber = response.body[0].states.length; // число записей за выбранный период
                                                        let tooMuchTrigger = false; // флаг для отображения кнопок экспорта
                                                        let tooMuchMessage = ""; // сообщение о превышении лимита записей
                                                        for (let i = 0; i < statesNumber; i++) {
                                                            data.push({ // заполнение данных для отчётной таблицы
                                                                eui: devName,
                                                                date: response.body[0].states[i][5],
                                                                v_ac: response.body[0].states[i][0],
                                                                i_ac: response.body[0].states[i][1],
                                                                temperature: response.body[0].states[i][2],
                                                                brightness: response.body[0].states[i][3],
                                                                state: response.body[0].states[i][4],
                                                            });
                                                        }
                                                        if(statesNumber > 3000) {
                                                            tooMuchTrigger = true;
                                                            if (statesNumber > 2000000) {
                                                                tooMuchMessage = '<div class="alert alert-info" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p>Количество записей превысило два миллиона. Экспорт в файл затруднён. Отображение записей свыше двух миллионов затруднено.</p>' + '</div>';
                                                            } else {
                                                                tooMuchMessage = '<div class="alert alert-info" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p>Количество записей превысило три тысячи. Экспорт в файл затруднён.</p>' + '</div>';
                                                            }
                                                        }
                                                        formArea.addClass('form-ui-style');
                                                        reportArea.empty().html('<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">' +
                                                            '            <h2>Список состояний</h2>' +
                                                            '        </div>' +
                                                            '        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mb-2" id="dataTableBtnGroup"></div>' +
                                                            '        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">' +
                                                            '            <table class="table table-striped table-bordered" id="states_table" width="100%">' +
                                                            '               <thead>' +
                                                            '                   <tr>' +
                                                            '                       <th>Устройство (EUI)</th>' +
                                                            '                       <th>Дата показания (ISO 8601)</th>' +
                                                            '                       <th>Напряжение (В)</th>' +
                                                            '                       <th>Сила тока (мА)</th>' +
                                                            '                       <th>Температура (°C)</th>' +
                                                            '                       <th>Яркость (лм)</th>' +
                                                            '                       <th>Состояние (код)</th>' +
                                                            '                   </tr>' +
                                                            '           </thead>' +
                                                            '<tbody></tbody>' +
                                                            '            </table>' +
                                                            '        </div>');
                                                        let statesTable = $('#states_table').DataTable({
                                                            language: { // перевод элементов таблицы
                                                                processing: "Подождите...",
                                                                search: "Поиск:",
                                                                lengthMenu: "Показать _MENU_ записей",
                                                                info: "Записи с _START_ до _END_ из _TOTAL_ записей",
                                                                infoEmpty: "Записи с 0 до 0 из 0 записей",
                                                                infoFiltered: "(отфильтровано из _MAX_ записей)",
                                                                infoPostFix: "",
                                                                loadingRecords: "Загрузка записей...",
                                                                zeroRecords: "Записи отсутствуют.",
                                                                emptyTable: "В таблице отсутствуют данные",
                                                                paginate: {
                                                                    first: "Первая",
                                                                    previous: "Предыдущая",
                                                                    next: "Следующая",
                                                                    last: "Последняя"
                                                                },
                                                                aria: {
                                                                    sortAscending: ": активировать для сортировки столбца по возрастанию",
                                                                    sortDescending: ": активировать для сортировки столбца по убыванию"
                                                                }
                                                            },
                                                            data: data, // данные для построения таблицы
                                                            columns: [ // объявление столбцов данных
                                                                {data: 'eui'},
                                                                {data: 'date'},
                                                                {data: 'v_ac'},
                                                                {data: 'i_ac'},
                                                                {data: 'temperature'},
                                                                {data: 'brightness'},
                                                                {data: 'state'}
                                                            ],
                                                            responsive: true, // гибкие стили таблицы
                                                            deferRender: true, // постепенная подгрузка результатов
                                                            order: [[ 1, "desc" ]], // установка сортировки результатов
                                                            buttons: [ // кнопки выгрузки данных таблицы в файл
                                                                {
                                                                    extend: 'print',
                                                                    text: 'Печать',
                                                                    title: reportTitle
                                                                },
                                                                {
                                                                    extend: 'excelHtml5',
                                                                    text: 'Экспорт в EXCEL',
                                                                    title: reportTitle
                                                                },
                                                                {
                                                                    extend: 'pdfHtml5',
                                                                    text: 'Экспорт в PDF',
                                                                    title: reportTitle
                                                                },
                                                                {
                                                                    extend: 'csvHtml5',
                                                                    text: 'Экспорт в CSV',
                                                                    title: reportTitle
                                                                }
                                                            ]
                                                        });
                                                        let dataTableBtnGroup = $('#dataTableBtnGroup'); // область отрисовки кнопок
                                                        if(tooMuchTrigger === false) {
                                                            statesTable.buttons().container().appendTo(dataTableBtnGroup); // обновление таблицы для отображения кнопок
                                                            let dataTableGroup = document.getElementsByClassName("dt-buttons btn-group");
                                                            dataTableGroup[0].classList.add("btn-group-sm");
                                                            for (let i = 0; i < dataTableGroup[0].children.length; i++) {
                                                                dataTableGroup[0].children[i].classList.remove("btn-secondary");
                                                                dataTableGroup[0].children[i].classList.add("btn-outline-primary");
                                                            }
                                                        } else {
                                                            dataTableBtnGroup.html(tooMuchMessage);
                                                        }
/*--------------------------------------------------------------------------------------------------------------------*/
                                                    } else if (chartType === "Линейный график") { // форматирование для линейного графика
                                                        let dataChartPattern = {};
                                                        let chartOptionsArray = [];
                                                        let appendingChild = document.createElement("div");
                                                        let repoName = "";
                                                        for (let j = 0; j < paramPickerArray.length; j++) { // определение легенды графика и заполнение данными
                                                            dataChartPattern = DataChartAssembler(chartMethod, paramPickerArray[j], paramPickerArray.length, response, j);
                                                            data.push(dataChartPattern);
                                                            if (paramPickerArray.length !== 1 && chartMethod === "single") { // если несколько параметров на одном графике
                                                                axisLegend = "Измеряемые параметры";
                                                            }
                                                            if (chartMethod === "multi") {
                                                                if (j === 0) { // отрисовка для первого
                                                                    formArea.addClass('form-ui-style'); // настройка карточки с тенями
                                                                    reportArea.addClass('chart-ui-style'); // настройка области построения графика
                                                                    chartOptions = new CanvasJS.Chart("reportArea", { // настройка области построения графика
                                                                        zoomEnabled: true,
                                                                        animationEnabled: true,
                                                                        exportEnabled: true,
                                                                        exportFileName: 'Отчёт за период с ' + sReportDate + ' по ' + fReportDate,
                                                                        title: {
                                                                            text: reportTitle,
                                                                            fontSize: 20
                                                                        },
                                                                        axisX: {
                                                                            includeZero: false,
                                                                            title: "Время измерений",
                                                                            valueFormatString: "DD.MM.YYYY HH:mm:ss",
                                                                            labelWrap: true,
                                                                            titleFontSize: 16,
                                                                            labelFontSize: 12,
                                                                            gridColor: "#000000",
                                                                            gridThickness: 1
                                                                        },
                                                                        axisY: {
                                                                            includeZero: false,
                                                                            title: axisLegend,
                                                                            titleFontSize: 16,
                                                                            gridColor: "#000000",
                                                                            gridThickness: 1
                                                                        },
                                                                        data: data,
                                                                        rangeChanged: function(e){
                                                                            if (e.trigger === "zoom") {
                                                                                let viewMin = new Date(e.axisX[0].viewportMinimum); // начало периода
                                                                                let viewMax = new Date(e.axisX[0].viewportMaximum); // окончание периода
                                                                                viewMin = moment.utc(viewMin).format(); // дата-время начала в формате Moment-объекта
                                                                                viewMax = moment.utc(viewMax).format(); // дата-время окончания в формате Moment-объекта
                                                                                viewMin = moment(viewMin).format('YYYY-MM-DDTHH:mm:ss'); // дата-время начала в формате ISO-8601
                                                                                viewMax = moment(viewMax).format('YYYY-MM-DDTHH:mm:ss'); // дата-время окончания в формате ISO-8601
                                                                                requestString = "?datefrom=" + viewMin + "Z&dateto=" + viewMax + "Z&objectsid=" + deviceID + "&tags=" + paramConverted + ",date_of_changed";
                                                                                urlState = "/report/allstates" + requestString;

                                                                                ReportDataGetter(urlState) // Promise-функция для обновления данных
                                                                                    .then(
                                                                                        function (updateResponse) {
                                                                                            data = [];
                                                                                            // определение легенды графика и заполнение данными
                                                                                            dataChartPattern = DataChartAssembler(chartMethod, paramPickerArray[j], paramPickerArray.length, updateResponse, j);
                                                                                            data.push(dataChartPattern);
                                                                                            chartOptionsArray[0].options.data = data;
                                                                                            chartOptionsArray[0].render(); // перестроение графика
                                                                                        }, function (error) {
                                                                                            HideResultProgressBar();
                                                                                            FormErrorMessage("StatDataLoadError", errorBlock);
                                                                                        }
                                                                                    );
                                                                            }
                                                                        }
                                                                    });
                                                                    chartOptionsArray[0] = chartOptions;
                                                                    chartOptionsArray[0].render(); // построение графика
                                                                } else if (j > 0) { // отрисовка для последующих
                                                                    appendingChild = document.createElement("div");
                                                                    appendingChild.id = "reportArea" + (j + 1);
                                                                    appendingChild.classList.add("chart-ui-style");
                                                                    document.querySelector(".chart-container").appendChild(appendingChild);
                                                                    repoName = "reportArea" + (j + 1);
                                                                    reportArea = $("#" + repoName);
                                                                    chartOptions = new CanvasJS.Chart(repoName, { // настройка области построения графика
                                                                        zoomEnabled: true,
                                                                        animationEnabled: true,
                                                                        exportEnabled: true,
                                                                        exportFileName: 'Отчёт за период с ' + sReportDate + ' по ' + fReportDate,
                                                                        title: {
                                                                            text: reportTitle,
                                                                            fontSize: 20
                                                                        },
                                                                        axisX: {
                                                                            includeZero: false,
                                                                            title: "Время измерений",
                                                                            valueFormatString: "DD.MM.YYYY HH:mm:ss",
                                                                            labelWrap: true,
                                                                            titleFontSize: 16,
                                                                            labelFontSize: 12,
                                                                            gridColor: "#000000",
                                                                            gridThickness: 1
                                                                        },
                                                                        axisY: {
                                                                            includeZero: false,
                                                                            title: axisLegend,
                                                                            titleFontSize: 16,
                                                                            gridColor: "#000000",
                                                                            gridThickness: 1
                                                                        },
                                                                        data: data,
                                                                        rangeChanged: function(e){
                                                                            if (e.trigger === "zoom") {
                                                                                let viewMin = new Date(e.axisX[0].viewportMinimum); // начало периода
                                                                                let viewMax = new Date(e.axisX[0].viewportMaximum); // окончание периода
                                                                                viewMin = moment.utc(viewMin).format(); // дата-время начала в формате Moment-объекта
                                                                                viewMax = moment.utc(viewMax).format(); // дата-время окончания в формате Moment-объекта
                                                                                viewMin = moment(viewMin).format('YYYY-MM-DDTHH:mm:ss'); // дата-время начала в формате ISO-8601
                                                                                viewMax = moment(viewMax).format('YYYY-MM-DDTHH:mm:ss'); // дата-время окончания в формате ISO-8601
                                                                                requestString = "?datefrom=" + viewMin + "Z&dateto=" + viewMax + "Z&objectsid=" + deviceID + "&tags=" + paramConverted + ",date_of_changed";
                                                                                urlState = "/report/allstates" + requestString;

                                                                                ReportDataGetter(urlState) // Promise-функция для обновления данных
                                                                                    .then(
                                                                                        function (updateResponse) {
                                                                                            data = [];
                                                                                            // определение легенды графика и заполнение данными
                                                                                            dataChartPattern = DataChartAssembler(chartMethod, paramPickerArray[j], paramPickerArray.length, updateResponse, j);
                                                                                            data.push(dataChartPattern);
                                                                                            chartOptionsArray[j].options.data = data;
                                                                                            chartOptionsArray[j].render(); // перестроение графика
                                                                                        }, function (error) {
                                                                                            HideResultProgressBar();
                                                                                            FormErrorMessage("StatDataLoadError", errorBlock);
                                                                                        }
                                                                                    );
                                                                            }
                                                                        }
                                                                    });
                                                                    chartOptionsArray[j] = chartOptions;
                                                                    chartOptionsArray[j].render(); // построение графика
                                                                }
                                                                data = [];
                                                                chartOptions = {};
                                                            }
                                                        }
                                                        if (chartMethod === "single") {
                                                            chartOptions = new CanvasJS.Chart("reportArea", { // настройка области построения графика
                                                                zoomEnabled: true,
                                                                animationEnabled: true,
                                                                exportEnabled: true,
                                                                exportFileName: 'Отчёт за период с ' + sReportDate + ' по ' + fReportDate,
                                                                connectNullData: false,
                                                                title: {
                                                                    text: reportTitle,
                                                                    fontSize: 20
                                                                },
                                                                toolTip: {
                                                                    shared: true,
                                                                },
                                                                axisX: {
                                                                    includeZero: false,
                                                                    title: "Время измерений",
                                                                    valueFormatString: "DD.MM.YYYY HH:mm:ss",
                                                                    labelWrap: true,
                                                                    titleFontSize: 16,
                                                                    labelFontSize: 12,
                                                                    gridColor: "#000000",
                                                                    gridThickness: 1
                                                                },
                                                                axisY: {
                                                                    includeZero: false,
                                                                    title: axisLegend,
                                                                    titleFontSize: 16,
                                                                    gridColor: "#000000",
                                                                    gridThickness: 1
                                                                },
                                                                data: data,
                                                                rangeChanged: function(e){
                                                                    if (e.trigger === "zoom") {
                                                                        let viewMin = new Date(e.axisX[0].viewportMinimum); // начало периода
                                                                        let viewMax = new Date(e.axisX[0].viewportMaximum); // окончание периода
                                                                        viewMin = moment.utc(viewMin).format(); // дата-время начала в формате Moment-объекта
                                                                        viewMax = moment.utc(viewMax).format(); // дата-время окончания в формате Moment-объекта
                                                                        viewMin = moment(viewMin).format('YYYY-MM-DDTHH:mm:ss'); // дата-время начала в формате ISO-8601
                                                                        viewMax = moment(viewMax).format('YYYY-MM-DDTHH:mm:ss'); // дата-время окончания в формате ISO-8601
                                                                        requestString = "?datefrom=" + viewMin + "Z&dateto=" + viewMax + "Z&objectsid=" + deviceID + "&tags=" + paramConverted + ",date_of_changed";
                                                                        urlState = "/report/allstates" + requestString;

                                                                        ReportDataGetter(urlState) // Promise-функция для обновления данных
                                                                            .then(
                                                                                function (updateResponse) {
                                                                                    data = [];
                                                                                    for (let j = 0; j < paramPickerArray.length; j++) { // определение легенды графика и заполнение данными
                                                                                        dataChartPattern = DataChartAssembler(chartMethod, paramPickerArray[j], paramPickerArray.length, updateResponse, j);
                                                                                        data.push(dataChartPattern);
                                                                                    }
                                                                                    chartOptions.options.data = data;
                                                                                    chartOptions.render(); // перестроение графика
                                                                                }, function (error) {
                                                                                    HideResultProgressBar();
                                                                                    FormErrorMessage("StatDataLoadError", errorBlock);
                                                                                }
                                                                            );
                                                                    }
                                                                }
                                                            });
                                                            formArea.addClass('form-ui-style'); // настройка карточки с тенями
                                                            reportArea.addClass('chart-ui-style'); // настройка области построения графика
                                                            chartOptions.render(); // построение графика
                                                        }
/*--------------------------------------------------------------------------------------------------------------------*/
                                                    } else if (chartType === "График со столбцами") { // форматирование для графика со столбцами
                                                        for (let i = 0; i < response.body[0].states.length; i++) {
                                                            tempArray.push(response.body[0].states[i][0]);
                                                        }
                                                        if (tempArray.length === 0) {
                                                            HideResultProgressBar(); // прекращение показа загрузочной крутилки
                                                            FormErrorMessage("DataNotFound", errorBlock);
                                                        } else {
                                                            dataPoints = []; // очистка данных для графика перед заполнением
                                                            AvrCounter(tempArray, function (avrCountResponse, error) { // подсчёт и группировка повторений
                                                                if (error) { // обработка ошибок
                                                                    throw error; // !!! нужен модуль обработки ошибок
                                                                } else { // получение сгруппированных данных
                                                                    dataPoints = avrCountResponse;
                                                                }
                                                            });
                                                            if (paramPickerArray[0] === "Напряжение") { // определение легенды графика
                                                                paramLegend = " вольт";
                                                                axisLegend = "Вольты";
                                                            } else if (paramPickerArray[0] === "Сила тока") {
                                                                paramLegend = " миллиампер";
                                                                axisLegend = "Миллиамперы";
                                                            } else if (paramPickerArray[0] === "Температура") {
                                                                paramLegend = " °C";
                                                                axisLegend = "Градусы Цельсия";
                                                            } else if (paramPickerArray[0] === "Яркость") {
                                                                paramLegend = " лм";
                                                                axisLegend = "Люмен";
                                                            } else if (paramPickerArray[0] === "Состояние") {
                                                                paramLegend = " код";
                                                                axisLegend = "Коды состояния";
                                                            } else {
                                                                paramLegend = " " + paramPickerArray[0];
                                                                axisLegend = paramPickerArray[0];
                                                            }
                                                            let dataSeries = { // определяющие параметры графика
                                                                type: "column",
                                                                toolTipContent: "<b>{label} " + paramLegend + "</b>: измерений - {y} "
                                                            };
                                                            dataSeries.dataPoints = dataPoints; // добавление данных для отрисовки графика
                                                            data.push(dataSeries); // формирование пакета настроек линии графика для построения
                                                            let barOptions = new CanvasJS.Chart("reportArea", { // настройка области построения графика
                                                                animationEnabled: true,
                                                                exportEnabled: true,
                                                                exportFileName: 'Отчёт за период с ' + sReportDate + ' по ' + fReportDate,
                                                                title: {
                                                                    text: reportTitle,
                                                                    fontSize: 20
                                                                },
                                                                axisY: {
                                                                    title: "Количество измерений",
                                                                    includeZero: false,
                                                                    labelWrap: true,
                                                                    titleFontSize: 16,
                                                                    labelFontSize: 12,
                                                                    gridColor: "#000000",
                                                                    gridThickness: 1
                                                                },
                                                                axisX: {
                                                                    title: axisLegend,
                                                                    titleFontSize: 16,
                                                                    gridColor: "#000000",
                                                                    gridThickness: 1
                                                                },
                                                                data: data
                                                            });
                                                            FormErrorMessage("ClearErrorField", errorBlock); // очищение блока отображения ошибок
                                                            formArea.addClass('form-ui-style'); // настройка карточки с тенями
                                                            reportArea.addClass('chart-ui-style'); // настройка области построения графика
                                                            barOptions.render(); // построение графика
                                                        }
/*--------------------------------------------------------------------------------------------------------------------*/
                                                    } else if (chartType === "Круговая диаграмма") { // форматирование для круговой диаграммы
                                                        for (let i = 0; i < response.body[0].states.length; i++) {
                                                            tempArray.push(response.body[0].states[i][0]);
                                                        }
                                                        if (tempArray.length === 0) {
                                                            HideResultProgressBar(); // прекращение показа загрузочной крутилки
                                                            FormErrorMessage("DataNotFound", errorBlock);
                                                        } else {
                                                            AvrCounter(tempArray, function (avrCountResponse, error) { // подсчёт и группировка повторений
                                                                if (error) { // обработка ошибок
                                                                    throw error; // !!! нужен модуль обработки ошибок
                                                                } else { // получение сгруппированных данных
                                                                    dataPoints = avrCountResponse;
                                                                }
                                                            });
                                                            if (paramPickerArray[0] === "Напряжение") { // определение легенды графика
                                                                paramPickerArray[0] = "vac";
                                                                paramLegend = " вольт";
                                                            } else if (paramPickerArray[0] === "Сила тока") {
                                                                paramPickerArray[0] = "iac";
                                                                paramLegend = " миллиампер";
                                                            } else if (paramPickerArray[0] === "Температура") {
                                                                paramPickerArray[0] = "temperature";
                                                                paramLegend = " °C";
                                                            } else if (paramPickerArray[0] === "Яркость") {
                                                                paramPickerArray[0] = "brightness";
                                                                paramLegend = " лм";
                                                            } else if (paramPickerArray[0] === "Состояние") {
                                                                paramPickerArray[0] = "state";
                                                                paramLegend = " код";
                                                            } else {
                                                                paramLegend = " " + paramPickerArray[0];
                                                            }
                                                            let dataSeries = { // определяющие параметры диаграммы
                                                                type: "pie",
                                                                startAngle: 40,
                                                                toolTipContent: "<b>{label} " + paramLegend + "</b>: измерений - {y}",
                                                                showInLegend: false,
                                                                indexLabelFontSize: 16,
                                                                indexLabel: "{label} " + paramLegend + ": измерений - {y}"
                                                            };
                                                            dataSeries.dataPoints = dataPoints; // добавление данных для отрисовки графика
                                                            data.push(dataSeries); // формирование пакета настроек линии графика для построения
                                                            let circleOptions = new CanvasJS.Chart("reportArea", { // настройка области построения графика
                                                                animationEnabled: true,
                                                                exportEnabled: true,
                                                                exportFileName: 'Отчёт за период с ' + sReportDate + ' по ' + fReportDate,
                                                                title: {
                                                                    text: reportTitle,
                                                                    fontSize: 20
                                                                },
                                                                data: data
                                                            });
                                                            FormErrorMessage("ClearErrorField", errorBlock); // очищение блока отображения ошибок
                                                            formArea.addClass('form-ui-style'); // настройка карточки с тенями
                                                            reportArea.addClass('chart-ui-style'); // настройка области построения графика
                                                            circleOptions.render(); // построение графика
                                                        }
/*--------------------------------------------------------------------------------------------------------------------*/
                                                    } else {
                                                        FormErrorMessage("ChartTypeUnresolved", errorBlock); // сообщение о сбое в определении типа графика
                                                    }
                                                    HideResultProgressBar(); // прекращение показа загрузочной крутилки, если всё загрузилось
                                                }
                                            }, function (error) {
                                                HideResultProgressBar();
                                                FormErrorMessage("StatDataLoadError", errorBlock);
                                            }
                                        );
                                }
                            });
/*--------------------------------------------------------------------------------------------------------------------*/
                        }, function (error) {
                            HideLoadProgressBar();
                            FormErrorMessage("ObjDataLoadError", errorBlock);
                        }
                    );
            }, function (error) {
                HideLoadProgressBar();
                FormErrorMessage("TypeDataLoadError", errorBlock);
            }
        );
});
/*====================================================================================================================*/

/* Описание: Обработка исключений при работе с формой конструктора отчётов.
 * Входящие данные: шифр ошибки и объект блока, куда нужно отрисовывать ошибку.
 * Исходящие данные: -
 */
function DataChartAssembler(chartMethod, paramPick, paramArrLength, response, j) {
    let paramLegend = "";
    let axisLegend = "";
    let dataChartPattern = {};
    let dataPoints = [];
    let paramDate = new Date;
    let prevParamDate = new Date(); // время получения предыдущего показания
    let deltaParamDate = new Date();

    if (paramPick === "Напряжение") { // известные параметры
        paramLegend = " В";
        axisLegend = "Вольты";
    } else if (paramPick === "Сила тока") {
        paramLegend = " мА";
        axisLegend = "Миллиамперы";
    } else if (paramPick === "Температура") {
        paramLegend = " °C";
        axisLegend = "Градусы Цельсия";
    } else if (paramPick === "Яркость") {
        paramLegend = " лм";
        axisLegend = "Люмен";
    } else if (paramPick === "Состояние") {
        paramLegend = " код";
        axisLegend = "Коды состояния";
    } else { // неизвестные размерности
        paramLegend = " " + paramPick;
        axisLegend = paramPick;
    }
    if (paramArrLength === 1 || chartMethod === "multi") {
        dataChartPattern = { // определяющие параметры графика
            type: "line",
            color: "#c0504e",
            connectNullData: true,
            xValueFormatString: "DD.MM.YYYY HH:mm:ss",
            toolTipContent: "<strong>{y}</strong> " + paramLegend + ": время - {x}"
        };
    } else {
        dataChartPattern = { // определяющие параметры графика
            type: "line",
            showInLegend: true,
            legendText: axisLegend,
            connectNullData: true,
            xValueFormatString: "DD.MM.YYYY HH:mm:ss",
            toolTipContent: "<strong>{y}</strong> " + paramLegend + ": время - {x}"
        };
    }
    for (let i = 0; i < response.body[0].states.length; i++) { // проходить по результатам запроса
        paramDate = new Date(Date.parse(response.body[0].states[i][paramArrLength])); // текущее время показания
        if (i > 0) { // вставка пунктирных разрывов между далеко расположенными на оси данными
            prevParamDate = new Date(Date.parse(response.body[0].states[i - 1][paramArrLength])); // предыдущее время показания
            deltaParamDate = paramDate - prevParamDate; // вычисление разницы между показаниями
            if (deltaParamDate > 86400000) { // если разница более суток
                dataPoints.push({ // добавлять показание с null секундой позже для включения пунктирной линии
                    x: new Date(paramDate.getFullYear(), paramDate.getMonth(), paramDate.getDate(), paramDate.getHours(), paramDate.getMinutes(), paramDate.getSeconds() + 1),
                    y: null
                });
            }
        }
        dataPoints.push({ // заполнение массива данных для построения графика
            x: new Date(paramDate.getFullYear(), paramDate.getMonth(), paramDate.getDate(), paramDate.getHours(), paramDate.getMinutes(), paramDate.getSeconds()),
            y: response.body[0].states[i][j]
        });
    }
    dataChartPattern.dataPoints = dataPoints; // добавление данных для отрисовки графика
    return dataChartPattern;
}
/*====================================================================================================================*/

/* Описание: Обработка исключений при работе с формой конструктора отчётов.
 * Входящие данные: шифр ошибки и объект блока, куда нужно отрисовывать ошибку.
 * Исходящие данные: -
 */
function FormErrorMessage(state, errorBlock) {
    switch(state) {
        case "DataLoadError":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой загрузки!</strong></p><p>Данные загрузить не удалось.</p>' + '</div>');
            break;
        case "TypeDataLoadError":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой загрузки типов устройств!</strong></p><p>Данные о типах устройств и типах параметров загрузить не удалось.</p>' + '</div>');
            break;
        case "ObjDataLoadError":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой загрузки списка устройств!</strong></p><p>Данные об имеющихся устройствах загрузить не удалось.</p>' + '</div>');
            break;
        case "StatDataLoadError":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой загрузки показаний устройств!</strong></p><p>Данные показаний устройств загрузить не удалось.</p>' + '</div>');
            break;
        case "ChartTypeUnresolved":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой построения графика!</strong></p><p>Тип графика не определён.</p>' + '</div>');
            break;
        case "DataNotFound":
            errorBlock.empty().html('<div class="alert alert-warning" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Увы!</strong></p><p>Данных за выбранный период не обнаружено.</p>' + '</div>');
            break;
        case "NoInputParams":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой построения графика!</strong></p><p>Не выбран ни один параметр.</p>' + '</div>');
            break;
        case "NoInputPeriod":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой построения графика!</strong></p><p>Не выбран период отчёта.</p>' + '</div>');
            break;
        case "ClearErrorField":
            errorBlock.empty().html("");
            break;
        case "NoChartType":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой построения графика!</strong></p><p>Не выбран тип графика.</p>' + '</div>');
            break;
        case "NoDevType":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой построения графика!</strong></p><p>Не выбран тип устройства.</p>' + '</div>');
            break;
        case "NoDevName":
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Сбой построения графика!</strong></p><p>Не выбрано наименование устройства.</p>' + '</div>');
            break;
        default:
            errorBlock.empty().html('<div class="alert alert-danger" role="alert">' + '<a href="#" class="close" data-dismiss="alert">×</a>' + '<p><strong>Внимание!</strong></p><p>Неопознанная ошибка.</p>' + '</div>');
            break;
    }
}
/*====================================================================================================================*/

/* Описание: Подсчёт количества повторяющихся значений в массиве.
 * Входящие данные: отсортированный по возрастанию массив; функция обратного вызова.
 * Исходящие данные: результаты подсчёта: y - количество повторений, label - подсчитываемый параметр.
 */
function AvrCounter(tempArray, callback) {
    tempArray.sort(function(a, b) {
        return a - b;
    });
    let repCounter = 0;
    let dataPoints = [];
    for (let j = 0; j < tempArray.length; j++) {
            if (tempArray[j] === tempArray[j + 1]) {
                repCounter = repCounter + 1;
            } else {
                dataPoints.push({
                    y: repCounter + 1,
                    label: tempArray[j]
                });
                repCounter = 0;
        }
    }
    return callback(dataPoints);
}
/*====================================================================================================================*/

/* Описание: Выключение загрузочной крутилки для области результатов.
 * Входящие данные: -
 * Исходящие данные: -
 */
function HideLoadProgressBar() {
    $("#dignissim-animunculus").addClass('pre-loader-awaiter');
}
/*====================================================================================================================*/

/* Описание: Включение загрузочной крутилки для области результатов.
 * Входящие данные: -
 * Исходящие данные: -
 */
function ShowLoadProgressBar() {
    $("#dignissim-animunculus").removeClass('pre-loader-awaiter');
}
/*====================================================================================================================*/
/* Описание: Выключение загрузочной крутилки для области результатов.
 * Входящие данные: -
 * Исходящие данные: -
 */
function HideResultProgressBar() {
    $("#dignissim-animunculus").addClass('pre-loader-awaiter');
}
/*====================================================================================================================*/

/* Описание: Включение загрузочной крутилки для области результатов.
 * Входящие данные: -
 * Исходящие данные: -
 */
function ShowResultProgressBar() {
    $("#dignissim-animunculus").removeClass('pre-loader-awaiter');
}
/*====================================================================================================================*/

/* Описание: Изменение режима отображения формы при выборе типа отчёта.
 * Входящие данные: идентификатор режима, объекты формы
 * Исходящие данные: -
 */
function FormModeSwitcher(chartTypePicker, devTypePickerDiv, devTypePicker, devPickerDiv, devPicker, reportChartMulti, reportCPMulti, singleCPDevLabel, multiCPDevLabel, cpGroupPicker, paramPickerDiv, paramPicker, choiceManualLabel, choiceDayLabel, choiceWeekLabel, choiceMonthLabel, datetimeChoosing, datetimePickerOne, datetimePickerTwo) {

    let label = $('label');

    $('.datetimes input[type=radio]').each(function () { // проверка и поддержание политики состояния выбра времени
        if (this.checked) { // если выбран
            if (this.value === "choiceDay" || this.value === "choiceWeek" || this.value === "choiceMonth") { // фиксированный период
                datetimePickerOne.data("DateTimePicker").disable(); // выбор времени блокируется
                datetimePickerTwo.data("DateTimePicker").disable();
            } else { // иначе
                datetimePickerOne.data("DateTimePicker").enable(); // выбор времени доступен
                datetimePickerTwo.data("DateTimePicker").enable();
            }
        }
    });

    if (devTypePicker[0][devTypePicker.val()].innerText === "КП") {
        cpGroupPicker.prop('disabled', true);
        document.getElementById("singleCPDevLabel").removeAttribute("disabled");
        document.getElementById("multiCPDevLabel").removeAttribute("disabled");
        reportCPMulti.removeClass("btn-group-inactive");
        label.each(function () {
            if (this.id === "multiCPDevLabel") {
                this.classList.add("active"); // снятие выделения пункта группы кнопок
                this.control.checked = true; // снятие отметки о выборе пункта
            }
        });
    } else {
        cpGroupPicker.prop('disabled', true);
        document.getElementById("singleCPDevLabel").setAttribute("disabled", "disabled");
        document.getElementById("multiCPDevLabel").setAttribute("disabled", "disabled");
        reportCPMulti.addClass("btn-group-inactive");
        label.each(function () {
            if (this.id === "singleCPDevLabel" || this.id === "multiCPDevLabel") {
                this.classList.remove("active"); // снятие выделения пункта группы кнопок
                this.control.checked = false; // снятие отметки о выборе пункта
            }
        });
    }

    if (chartTypePicker[0][chartTypePicker.val()].innerText === "Таблица") { // табличное представление данных
        datetimeChoosing.removeClass("btn-group-inactive"); // включение выбора диапазонов
        reportChartMulti.addClass("btn-group-inactive"); // отключение выбора манеры построения графиков

        devTypePickerDiv.removeClass('not-valid-object');
        devPickerDiv.removeClass('not-valid-object');
        paramPickerDiv.removeClass('not-valid-object');

        datetimePickerOne.removeClass('not-valid-object');
        datetimePickerTwo.removeClass('not-valid-object');
        datetimeChoosing.removeClass('not-valid-object');

        devTypePicker.prop('disabled', false); // включение выбора типа устройства
        devPicker.prop('disabled', false); // включение выбора устройства
        paramPicker.prop('disabled', true); // отключение выбора параметров

        paramPicker.val(null).trigger("change");
        paramPicker.select2('destroy');
        paramPicker.select2({
            language: "ru",
            placeholder: "Выберите параметры",
            allowClear: true
        });

        // снятие выбора периода и манеры построения графика (мультиграфики доступны только с линейным графиком)
        label.each(function () {
            if (this.id === "singleChartLabel" || this.id === "multiChartLabel") {
                this.classList.remove("active"); // снятие выделения пункта группы кнопок
                this.control.checked = false; // снятие отметки о выборе пункта
            }
        });

        // отключение кнопок выбора периода
        document.getElementById("choiceManual").removeAttribute("disabled");
        document.getElementById("choiceDay").removeAttribute("disabled");
        document.getElementById("choiceWeek").removeAttribute("disabled");
        document.getElementById("choiceMonth").removeAttribute("disabled");
        document.getElementById("signleChart").removeAttribute("disabled");
        document.getElementById("multiChart").removeAttribute("disabled");
    } else if (chartTypePicker[0][chartTypePicker.val()].innerText === "График со столбцами" || chartTypePicker[0][chartTypePicker.val()].innerText === "Круговая диаграмма") { // усредняющие графики
        datetimeChoosing.removeClass("btn-group-inactive"); // включение выбора диапазонов
        reportChartMulti.addClass("btn-group-inactive"); // отключение выбора манеры построения графиков

        devTypePickerDiv.removeClass('not-valid-object');
        devPickerDiv.removeClass('not-valid-object');
        paramPickerDiv.removeClass('not-valid-object');

        datetimePickerOne.removeClass('not-valid-object');
        datetimePickerTwo.removeClass('not-valid-object');
        datetimeChoosing.removeClass('not-valid-object');

        devTypePicker.prop('disabled', false); // включение выбора типа устройства
        devPicker.prop('disabled', false); // включение выбора устройства
        paramPicker.prop('disabled', false); // включение выбора параметров

        paramPicker.val(null).trigger("change");
        paramPicker.select2('destroy');
        paramPicker.select2({
            language: "ru",
            placeholder: "Выберите параметр",
            allowClear: true,
            maximumSelectionLength: 1
        });

        // снятие выбора периода и манеры построения графика (мультиграфики доступны только с линейным графиком)
        label.each(function () {
            if(this.id === "singleChartLabel" || this.id === "multiChartLabel") {
                this.classList.remove("active"); // снятие выделения пункта группы кнопок
                this.control.checked = false; // снятие отметки о выборе пункта
            }
        });

        // включение кнопок выбора периода
        document.getElementById("choiceManual").removeAttribute("disabled");
        document.getElementById("choiceDay").removeAttribute("disabled");
        document.getElementById("choiceWeek").removeAttribute("disabled");
        document.getElementById("choiceMonth").removeAttribute("disabled");
        document.getElementById("signleChart").removeAttribute("disabled");
        document.getElementById("multiChart").removeAttribute("disabled");
    } else { // линейные графики
        datetimeChoosing.removeClass("btn-group-inactive"); // включение выбора манеры построения графиков
        reportChartMulti.removeClass("btn-group-inactive"); // включение выбора манеры построения графиков

        devTypePickerDiv.removeClass('not-valid-object');
        devPickerDiv.removeClass('not-valid-object');
        paramPickerDiv.removeClass('not-valid-object');

        datetimePickerOne.removeClass('not-valid-object');
        datetimePickerTwo.removeClass('not-valid-object');
        datetimeChoosing.removeClass('not-valid-object');

        devTypePicker.prop('disabled', false); // включение выбора типа устройства
        devPicker.prop('disabled', false); // включение выбора устройства
        paramPicker.prop('disabled', false); // включение выбора параметров

        paramPicker.val(null).trigger("change");
        paramPicker.select2('destroy');
        paramPicker.select2({
            language: "ru",
            placeholder: "Выберите параметры",
            allowClear: true
        });

        // снятие выбора периода и установка манеры отрисовки графиков по умолчанию
        label.each(function () {
            if(this.id === "singleChartLabel") {
                this.classList.add("active"); // снятие выделения пункта группы кнопок
                this.control.checked = true; // снятие отметки о выборе пункта
            }
        });

        // включение кнопок выбора периода
        document.getElementById("choiceManual").removeAttribute("disabled");
        document.getElementById("choiceDay").removeAttribute("disabled");
        document.getElementById("choiceWeek").removeAttribute("disabled");
        document.getElementById("choiceMonth").removeAttribute("disabled");
        document.getElementById("signleChart").removeAttribute("disabled");
        document.getElementById("multiChart").removeAttribute("disabled");
    }
}
/*====================================================================================================================*/

/* Описание: Форматирование выбранного диапазона для использование в имени файла при сохранении таблицы отчёта в файл.
 * Входящие данные: одна из дат выбранного диапазона
 * Исходящие данные: дата и время, преобразованные к формату "dd.mm.yyyy hh-mm"
 */
function reportDateFormatter(originDate) { // форматирование
    let sDTime = originDate.split("T");
    let sDateSplitted = sDTime[0].split("-");
    let sTimeSplitted = sDTime[1].split(":");
    return sDateSplitted[2] + "." + sDateSplitted[1] + "." + sDateSplitted[0] + " " + sTimeSplitted[0] + "ч" + sTimeSplitted[1] + "м";
}
/*====================================================================================================================*/
