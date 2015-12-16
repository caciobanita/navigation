﻿import TypeConverter = require('./TypeConverter');

class NumberConverter extends TypeConverter {
    constructor(key: string) {
        super(key, 'number');
    }

    convertFrom(val: string): any {
        if (isNaN(+val))
            throw Error(val + ' is not a valid number');
        return +val;
    }

    convertTo(val: any): { val: string, vals?: string[] } {
        return { val: val.toString() };
    }
}
export = NumberConverter;
