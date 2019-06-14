
 function decode(br) {
    switch (br) {
        case '0':
            return '49';

        case '10':
            return '48';

        case '20':
            return '47';

        case '30':
            return '46';

        case '40':
            return '45';

        case '50':
            return '44';

        case '60':
            return '43';

        case '70':
            return '42';

        case '80':
            return '41';

        case '90':
            return '40';

        case '100':
            return '39';

        default:

    }
}

 function findInArray(array, value) {

            for (let i = 0; i < array.length; i++) {
                if (array[i] === value) return i;
            }
            return -1;


}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}


function sleep_ms(millisecs) {
    let initiation = new Date().getTime();
    while ((new Date().getTime() - initiation) < millisecs) ;
}


