export function isNumber(val) {
    return !isNaN(parseFloat(val)) && isFinite(val)
}